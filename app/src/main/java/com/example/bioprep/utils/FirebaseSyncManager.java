package com.example.bioprep.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.bioprep.database.AppDatabase;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.PersistentCacheSettings;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * FirebaseSyncManager handles bi-directional synchronization between local Room SQLite
 * and Google Cloud Firestore for the "BioPrep-HSC" project.
 *
 * Persists:
 * - Profile: name, email, bio, studentLevel, avatar, dailyGoalMinutes
 * - Performance: studyTimeSeconds, activeStreak, quizzesCompleted, accuracyRate,
 *                totalQuestionsAnswered, totalCorrectAnswers, lastSyncedAt
 * - Subcollections: studySessions and quizAttempts
 */
public class FirebaseSyncManager {

    private static final String TAG = "FirebaseSyncManager";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static boolean isFirestoreConfigured = false;

    public interface SyncCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    /**
     * Configure Firestore settings with offline persistence.
     */
    public static synchronized void configureFirestore() {
        if (isFirestoreConfigured) return;
        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                    .build();
            db.setFirestoreSettings(settings);
            isFirestoreConfigured = true;
            Log.d(TAG, "Firestore initialized with offline cache.");
        } catch (Exception e) {
            Log.w(TAG, "Firestore setup notice: " + e.getMessage());
        }
    }

    /**
     * Gathers all local Room DB stats & UserPreferences, and pushes them to Cloud Firestore.
     */
    public static void syncProfileAndPerformance(Context context, SyncCallback callback) {
        configureFirestore();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            if (callback != null) {
                callback.onFailure("No authenticated Firebase user found.");
            }
            return;
        }

        String uid = currentUser.getUid();
        Context appCtx = context.getApplicationContext();

        executor.execute(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(appCtx);

                // 1. Compute dynamic performance from Room DB
                int totalStudySeconds = db.studySessionDao().getTotalStudySeconds();
                int quizCompletedCount = db.practiceDao().getCompletedQuizCount();
                int totalCorrect = db.practiceDao().getTotalCorrectAnswers();
                int totalAnswered = db.practiceDao().getTotalAnsweredQuestions();
                int accuracyPercent = totalAnswered > 0
                        ? (int) Math.round((totalCorrect / (double) totalAnswered) * 100.0) : 0;

                // Active streak calculation (days with >= daily goal)
                int dailyGoalSecs = UserPreferences.getDailyGoalMinutes(appCtx) * 60;
                SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String todayDate = dbDateFormat.format(new Date());
                int todaySecs = db.studySessionDao().getStudySecondsForDate(todayDate);

                int streak = 0;
                if (todaySecs >= dailyGoalSecs) {
                    streak++;
                }

                Calendar cal = Calendar.getInstance();
                for (int i = 0; i < 365; i++) {
                    cal.add(Calendar.DAY_OF_YEAR, -1);
                    String prevDate = dbDateFormat.format(cal.getTime());
                    int secs = db.studySessionDao().getStudySecondsForDate(prevDate);
                    if (secs >= dailyGoalSecs) {
                        streak++;
                    } else {
                        break;
                    }
                }

                // 2. Personal info from UserPreferences & FirebaseUser
                String name = UserPreferences.getUserName(appCtx);
                if (name == null || name.isEmpty() || name.equals("HSC Biology Student")) {
                    if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                        name = currentUser.getDisplayName();
                    }
                }
                String email = currentUser.getEmail() != null ? currentUser.getEmail() : UserPreferences.getUserEmail(appCtx);
                int dailyGoalMinutes = UserPreferences.getDailyGoalMinutes(appCtx);
                String avatarPath = UserPreferences.getAvatarPath(appCtx);

                // 3. Assemble Firestore payload
                Map<String, Object> userData = new HashMap<>();
                userData.put("uid", uid);
                userData.put("name", name);
                userData.put("email", email);
                userData.put("bio", "HSC Biology Student");
                userData.put("studentLevel", UserPreferences.STUDENT_LEVEL_TITLE);
                userData.put("avatarPath", avatarPath != null ? avatarPath : "");
                userData.put("dailyGoalMinutes", dailyGoalMinutes);
                userData.put("lastSyncedAt", FieldValue.serverTimestamp());

                Map<String, Object> performance = new HashMap<>();
                performance.put("studyTimeSeconds", totalStudySeconds);
                performance.put("activeStreak", streak);
                performance.put("quizzesCompleted", quizCompletedCount);
                performance.put("accuracyRate", accuracyPercent);
                performance.put("totalQuestionsAnswered", totalAnswered);
                performance.put("totalCorrectAnswers", totalCorrect);
                userData.put("performance", performance);

                // 4. Write to Cloud Firestore users/{uid}
                FirebaseFirestore.getInstance().collection("users").document(uid)
                        .set(userData, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Successfully synced profile & performance to Firestore.");
                            if (callback != null) {
                                mainHandler.post(callback::onSuccess);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.w(TAG, "Failed to sync to Firestore: " + e.getMessage());
                            if (callback != null) {
                                mainHandler.post(() -> callback.onFailure(e.getMessage()));
                            }
                        });

            } catch (Exception e) {
                Log.e(TAG, "Error compiling performance stats: " + e.getMessage(), e);
                if (callback != null) {
                    mainHandler.post(() -> callback.onFailure(e.getMessage()));
                }
            }
        });
    }

    /**
     * Pulls the user profile and settings from Cloud Firestore into local SharedPreferences/Room.
     */
    public static void pullProfileFromCloud(Context context, SyncCallback callback) {
        configureFirestore();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            if (callback != null) {
                callback.onFailure("No authenticated user.");
            }
            return;
        }

        String uid = currentUser.getUid();
        Context appCtx = context.getApplicationContext();

        FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc != null && doc.exists()) {
                        String name = doc.getString("name");
                        String email = doc.getString("email");
                        Long dailyGoal = doc.getLong("dailyGoalMinutes");

                        if (name != null && !name.trim().isEmpty()) {
                            UserPreferences.setUserName(appCtx, name.trim());
                        }
                        if (email != null && !email.trim().isEmpty()) {
                            UserPreferences.saveSession(appCtx, UserPreferences.getUserName(appCtx), email.trim());
                        }
                        if (dailyGoal != null && dailyGoal > 0) {
                            UserPreferences.setDailyGoalMinutes(appCtx, dailyGoal.intValue());
                        }

                        Log.d(TAG, "Pulled profile for user: " + name);
                    }
                    if (callback != null) {
                        mainHandler.post(callback::onSuccess);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error pulling profile from cloud: " + e.getMessage());
                    if (callback != null) {
                        mainHandler.post(() -> callback.onFailure(e.getMessage()));
                    }
                });
    }

    /**
     * Record a completed quiz attempt to Firestore subcollection users/{uid}/quizAttempts.
     */
    public static void logQuizAttemptToCloud(Context context, String subject, String chapterTitle,
                                             int score, int totalQuestions, int timeSpentSeconds) {
        configureFirestore();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String uid = currentUser.getUid();
        Map<String, Object> attemptData = new HashMap<>();
        attemptData.put("subject", subject);
        attemptData.put("chapterTitle", chapterTitle);
        attemptData.put("score", score);
        attemptData.put("totalQuestions", totalQuestions);
        attemptData.put("timeSpentSeconds", timeSpentSeconds);
        attemptData.put("timestamp", FieldValue.serverTimestamp());

        FirebaseFirestore.getInstance().collection("users").document(uid)
                .collection("quizAttempts")
                .add(attemptData)
                .addOnSuccessListener(ref -> Log.d(TAG, "Quiz attempt logged to Firestore: " + ref.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Failed logging quiz attempt: " + e.getMessage()));

        // Also trigger full stats update
        syncProfileAndPerformance(context, null);
    }

    /**
     * Record a study session to Firestore subcollection users/{uid}/studySessions.
     */
    public static void logStudySessionToCloud(Context context, String subject, String chapterTitle,
                                              int durationSeconds) {
        configureFirestore();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String uid = currentUser.getUid();
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("subject", subject);
        sessionData.put("chapterTitle", chapterTitle);
        sessionData.put("durationSeconds", durationSeconds);
        sessionData.put("timestamp", FieldValue.serverTimestamp());

        FirebaseFirestore.getInstance().collection("users").document(uid)
                .collection("studySessions")
                .add(sessionData)
                .addOnSuccessListener(ref -> Log.d(TAG, "Study session logged to Firestore: " + ref.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Failed logging study session: " + e.getMessage()));

        // Also trigger full stats update
        syncProfileAndPerformance(context, null);
    }
}

