package com.example.bioprep.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.bioprep.database.AppDatabase;
import com.example.bioprep.database.NoteEntity;
import com.example.bioprep.database.PracticeAttemptEntity;
import com.example.bioprep.database.RecentActivityEntity;
import com.example.bioprep.database.SavedItemEntity;
import com.example.bioprep.database.StudySessionEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.PersistentCacheSettings;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * FirebaseSyncManager handles bi-directional synchronization between local Room SQLite
 * and Firebase Realtime Database (with Cloud Firestore backup) for the "BioPrep-HSC" project.
 *
 * Persists and restores:
 * - Profile & Daily Goals: name, email, bio, studentLevel, avatarPath, dailyGoalMinutes
 * - Performance: studyTimeSeconds, activeStreak, quizzesCompleted, accuracyRate,
 *                totalQuestionsAnswered, totalCorrectAnswers
 * - Active Recall saved portions (SavedItemEntity with type=ACTIVE_RECALL)
 * - Practice Exam progress & saved portions (PracticeAttemptEntity, and SavedItemEntity with type=MCQ)
 * - Flashcard progress & saved portions (SavedItemEntity with type=FLASHCARD, and StudySessionEntity)
 * - Notes portion (NoteEntity and SavedItemEntity with type=NOTE)
 * - Recent activities (RecentActivityEntity)
 */
public class FirebaseSyncManager {

    private static final String TAG = "FirebaseSyncManager";
    public static final String RTDB_URL = "https://bioprep-hsc-default-rtdb.asia-southeast1.firebasedatabase.app";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static boolean isFirestoreConfigured = false;
    private static boolean isRtdbPersistenceEnabled = false;

    public interface SyncCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    /**
     * Obtains the properly configured Firebase Realtime Database instance.
     */
    public static FirebaseDatabase getRtdbInstance() {
        try {
            return FirebaseDatabase.getInstance(RTDB_URL);
        } catch (Exception e) {
            return FirebaseDatabase.getInstance();
        }
    }

    /**
     * Sanitize keys to be valid Firebase Realtime Database path keys.
     * Firebase keys cannot contain '.', '$', '#', '[', ']', '/', or spaces.
     */
    public static String safeFirebaseKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            return "item_" + System.currentTimeMillis();
        }
        return key.replace(".", "_")
                .replace("$", "_")
                .replace("#", "_")
                .replace("[", "_")
                .replace("]", "_")
                .replace("/", "_")
                .replace(" ", "_");
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

    // =========================================================================
    // Realtime Database Pull on Login (Fetches all progress into local app)
    // =========================================================================

    /**
     * Fetches all user progress from Firebase Realtime Database (Active Recall,
     * Practice Exam, Flashcards, Notes, Streak, Study Time) and restores it
     * into local Room SQLite database and UserPreferences upon login.
     */
    public static void pullAllProgressFromRealtimeDatabase(Context context, SyncCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            if (callback != null) {
                mainHandler.post(() -> callback.onFailure("No authenticated user."));
            }
            return;
        }

        String uid = currentUser.getUid();
        Context appCtx = context.getApplicationContext();

        DatabaseReference userRef = getRtdbInstance().getReference("users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.d(TAG, "No cloud progress found in Realtime Database for user " + uid + ". Seeding current local state.");
                    syncAllProgressToRealtimeDatabase(appCtx, null);
                    if (callback != null) {
                        mainHandler.post(callback::onSuccess);
                    }
                    return;
                }

                executor.execute(() -> {
                    try {
                        AppDatabase db = AppDatabase.getInstance(appCtx);

                        // 1. Restore Profile & Daily Goals
                        DataSnapshot profileSnap = snapshot.child("profile");
                        if (profileSnap.exists()) {
                            String name = profileSnap.child("name").getValue(String.class);
                            String email = profileSnap.child("email").getValue(String.class);
                            Long dailyGoal = profileSnap.child("dailyGoalMinutes").getValue(Long.class);

                            if (name != null && !name.trim().isEmpty()) {
                                UserPreferences.setUserName(appCtx, name.trim());
                            }
                            if (email != null && !email.trim().isEmpty()) {
                                UserPreferences.saveSession(appCtx, UserPreferences.getUserName(appCtx), email.trim());
                            }
                            if (dailyGoal != null && dailyGoal > 0) {
                                UserPreferences.setDailyGoalMinutes(appCtx, dailyGoal.intValue());
                            }
                        }

                        // 2. Restore Saved Items (Active Recall, MCQ, Flashcards, Notes)
                        DataSnapshot savedSnap = snapshot.child("savedItems");
                        if (savedSnap.exists()) {
                            for (DataSnapshot itemSnap : savedSnap.getChildren()) {
                                String type = itemSnap.child("type").getValue(String.class);
                                String itemId = itemSnap.child("itemId").getValue(String.class);
                                String title = itemSnap.child("title").getValue(String.class);
                                String content = itemSnap.child("content").getValue(String.class);
                                String extraInfo = itemSnap.child("extraInfo").getValue(String.class);
                                Long createdAt = itemSnap.child("createdAt").getValue(Long.class);

                                if (type != null && itemId != null) {
                                    if (db.savedItemDao().isItemSaved(type, itemId) == 0) {
                                        db.savedItemDao().insert(new SavedItemEntity(
                                                type,
                                                itemId,
                                                title != null ? title : "",
                                                content != null ? content : "",
                                                extraInfo != null ? extraInfo : "",
                                                createdAt != null ? createdAt : System.currentTimeMillis()
                                        ));
                                    }
                                }
                            }
                        }

                        // 3. Restore User Notes
                        DataSnapshot notesSnap = snapshot.child("notes");
                        if (notesSnap.exists()) {
                            for (DataSnapshot noteSnap : notesSnap.getChildren()) {
                                String title = noteSnap.child("title").getValue(String.class);
                                String content = noteSnap.child("content").getValue(String.class);
                                String imageUri = noteSnap.child("imageUri").getValue(String.class);
                                Long createdAt = noteSnap.child("createdAt").getValue(Long.class);
                                long t = (createdAt != null) ? createdAt : System.currentTimeMillis();

                                if (title != null && db.noteDao().checkNoteExists(title, t) == 0) {
                                    db.noteDao().insertNote(new NoteEntity(
                                            title,
                                            content != null ? content : "",
                                            imageUri,
                                            t
                                    ));
                                }
                            }
                        }

                        // 4. Restore Practice Exam Attempts
                        DataSnapshot attemptsSnap = snapshot.child("practiceAttempts");
                        if (attemptsSnap.exists()) {
                            for (DataSnapshot aSnap : attemptsSnap.getChildren()) {
                                String chapterId = aSnap.child("chapterId").getValue(String.class);
                                String paperId = aSnap.child("paperId").getValue(String.class);
                                String chapterTitle = aSnap.child("chapterTitle").getValue(String.class);
                                Long totalQ = aSnap.child("totalQuestions").getValue(Long.class);
                                Long correct = aSnap.child("correctCount").getValue(Long.class);
                                Long incorrect = aSnap.child("incorrectCount").getValue(Long.class);
                                Long unanswered = aSnap.child("unansweredCount").getValue(Long.class);
                                Long accuracy = aSnap.child("accuracyPercent").getValue(Long.class);
                                Long timeSpent = aSnap.child("timeSpentSeconds").getValue(Long.class);
                                Long timestamp = aSnap.child("timestamp").getValue(Long.class);
                                long t = (timestamp != null) ? timestamp : System.currentTimeMillis();

                                if (chapterId != null && db.practiceDao().checkAttemptExists(t, chapterId) == 0) {
                                    db.practiceDao().insertAttempt(new PracticeAttemptEntity(
                                            chapterId,
                                            paperId != null ? paperId : "",
                                            chapterTitle != null ? chapterTitle : "",
                                            totalQ != null ? totalQ.intValue() : 0,
                                            correct != null ? correct.intValue() : 0,
                                            incorrect != null ? incorrect.intValue() : 0,
                                            unanswered != null ? unanswered.intValue() : 0,
                                            accuracy != null ? accuracy.intValue() : 0,
                                            timeSpent != null ? timeSpent.intValue() : 0,
                                            t
                                    ));
                                }
                            }
                        }

                        // 5. Restore Study Sessions (Flashcards, Exam study time)
                        DataSnapshot sessionsSnap = snapshot.child("studySessions");
                        if (sessionsSnap.exists()) {
                            for (DataSnapshot sSnap : sessionsSnap.getChildren()) {
                                String sessionType = sSnap.child("sessionType").getValue(String.class);
                                String chapterId = sSnap.child("chapterId").getValue(String.class);
                                Long duration = sSnap.child("durationSeconds").getValue(Long.class);
                                Long timestamp = sSnap.child("timestamp").getValue(Long.class);
                                String dateString = sSnap.child("dateString").getValue(String.class);
                                long t = (timestamp != null) ? timestamp : System.currentTimeMillis();
                                int dur = (duration != null) ? duration.intValue() : 0;

                                if (db.studySessionDao().checkSessionExists(t, dur) == 0) {
                                    db.studySessionDao().insert(new StudySessionEntity(
                                            sessionType != null ? sessionType : "EXAM",
                                            chapterId != null ? chapterId : "",
                                            dur,
                                            t,
                                            dateString != null ? dateString : new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date(t))
                                    ));
                                }
                            }
                        }

                        // 6. Restore Recent Activities
                        DataSnapshot recentSnap = snapshot.child("recentActivities");
                        if (recentSnap.exists()) {
                            for (DataSnapshot rSnap : recentSnap.getChildren()) {
                                String actType = rSnap.child("activityType").getValue(String.class);
                                String chapterId = rSnap.child("chapterId").getValue(String.class);
                                String chapterTitle = rSnap.child("chapterTitle").getValue(String.class);
                                String paperId = rSnap.child("paperId").getValue(String.class);
                                Long timestamp = rSnap.child("timestamp").getValue(Long.class);

                                if (actType != null && chapterId != null && db.recentActivityDao().checkActivityExists(actType, chapterId) == 0) {
                                    db.recentActivityDao().insert(new RecentActivityEntity(
                                            actType,
                                            chapterId,
                                            chapterTitle != null ? chapterTitle : "",
                                            paperId != null ? paperId : "",
                                            timestamp != null ? timestamp : System.currentTimeMillis()
                                    ));
                                }
                            }
                        }

                        Log.d(TAG, "Successfully fetched and restored all progress from Realtime Database.");
                        if (callback != null) {
                            mainHandler.post(callback::onSuccess);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error applying fetched progress from RTDB: " + e.getMessage(), e);
                        if (callback != null) {
                            mainHandler.post(() -> callback.onFailure(e.getMessage()));
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed fetching progress from Realtime Database: " + error.getMessage());
                if (callback != null) {
                    mainHandler.post(() -> callback.onFailure(error.getMessage()));
                }
            }
        });
    }

    // =========================================================================
    // Realtime Database Push (Saves all progress into Cloud)
    // =========================================================================

    /**
     * Gathers all local Room DB stats & UserPreferences, and pushes them to
     * Firebase Realtime Database (and Cloud Firestore as backup).
     */
    public static void syncAllProgressToRealtimeDatabase(Context context, SyncCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            if (callback != null) {
                mainHandler.post(() -> callback.onFailure("No authenticated Firebase user found."));
            }
            return;
        }

        String uid = currentUser.getUid();
        Context appCtx = context.getApplicationContext();

        executor.execute(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(appCtx);

                // 1. Performance from Room DB
                int totalStudySeconds = db.studySessionDao().getTotalStudySeconds();
                int quizCompletedCount = db.practiceDao().getCompletedQuizCount();
                int totalCorrect = db.practiceDao().getTotalCorrectAnswers();
                int totalAnswered = db.practiceDao().getTotalAnsweredQuestions();
                int accuracyPercent = totalAnswered > 0
                        ? (int) Math.round((totalCorrect / (double) totalAnswered) * 100.0) : 0;

                int dailyGoalSecs = UserPreferences.getDailyGoalMinutes(appCtx) * 60;
                SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String todayDate = dbDateFormat.format(new Date());
                int todaySecs = db.studySessionDao().getStudySecondsForDate(todayDate);

                int streak = (todaySecs >= dailyGoalSecs) ? 1 : 0;
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

                // 2. Profile
                String name = UserPreferences.getUserName(appCtx);
                if (name == null || name.isEmpty() || name.equals("HSC Biology Student")) {
                    if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                        name = currentUser.getDisplayName();
                    }
                }
                String email = currentUser.getEmail() != null ? currentUser.getEmail() : UserPreferences.getUserEmail(appCtx);
                int dailyGoalMinutes = UserPreferences.getDailyGoalMinutes(appCtx);
                String avatarPath = UserPreferences.getAvatarPath(appCtx);

                Map<String, Object> profile = new HashMap<>();
                profile.put("name", name != null ? name : "Student");
                profile.put("email", email != null ? email : "");
                profile.put("bio", "HSC Biology Student");
                profile.put("studentLevel", UserPreferences.STUDENT_LEVEL_TITLE);
                profile.put("avatarPath", avatarPath != null ? avatarPath : "");
                profile.put("dailyGoalMinutes", dailyGoalMinutes);

                Map<String, Object> performance = new HashMap<>();
                performance.put("studyTimeSeconds", totalStudySeconds);
                performance.put("activeStreak", streak);
                performance.put("quizzesCompleted", quizCompletedCount);
                performance.put("accuracyRate", accuracyPercent);
                performance.put("totalQuestionsAnswered", totalAnswered);
                performance.put("totalCorrectAnswers", totalCorrect);

                // 3. Saved Items (Active Recall, MCQ, Flashcards, Notes)
                Map<String, Object> savedItemsMap = new HashMap<>();
                List<SavedItemEntity> savedList = db.savedItemDao().getAllSavedItems();
                if (savedList != null) {
                    for (SavedItemEntity item : savedList) {
                        Map<String, Object> m = new HashMap<>();
                        m.put("type", item.getType());
                        m.put("itemId", item.getItemId());
                        m.put("title", item.getTitle() != null ? item.getTitle() : "");
                        m.put("content", item.getContent() != null ? item.getContent() : "");
                        m.put("extraInfo", item.getExtraInfo() != null ? item.getExtraInfo() : "");
                        m.put("createdAt", item.getCreatedAt());
                        String key = safeFirebaseKey(item.getType() + "_" + item.getItemId());
                        savedItemsMap.put(key, m);
                    }
                }

                // 4. User Notes
                Map<String, Object> notesMap = new HashMap<>();
                List<NoteEntity> notesList = db.noteDao().getAllNotes();
                if (notesList != null) {
                    for (NoteEntity note : notesList) {
                        Map<String, Object> m = new HashMap<>();
                        m.put("title", note.getTitle() != null ? note.getTitle() : "");
                        m.put("content", note.getContent() != null ? note.getContent() : "");
                        m.put("imageUri", note.getImageUri() != null ? note.getImageUri() : "");
                        m.put("createdAt", note.getCreatedAt());
                        String key = safeFirebaseKey("note_" + note.getId() + "_" + note.getCreatedAt());
                        notesMap.put(key, m);
                    }
                }

                // 5. Practice Attempts
                Map<String, Object> attemptsMap = new HashMap<>();
                List<PracticeAttemptEntity> attemptsList = db.practiceDao().getAllAttempts();
                if (attemptsList != null) {
                    for (PracticeAttemptEntity a : attemptsList) {
                        Map<String, Object> m = new HashMap<>();
                        m.put("chapterId", a.getChapterId() != null ? a.getChapterId() : "");
                        m.put("paperId", a.getPaperId() != null ? a.getPaperId() : "");
                        m.put("chapterTitle", a.getChapterTitle() != null ? a.getChapterTitle() : "");
                        m.put("totalQuestions", a.getTotalQuestions());
                        m.put("correctCount", a.getCorrectCount());
                        m.put("incorrectCount", a.getIncorrectCount());
                        m.put("unansweredCount", a.getUnansweredCount());
                        m.put("accuracyPercent", a.getAccuracyPercent());
                        m.put("timeSpentSeconds", a.getTimeSpentSeconds());
                        m.put("timestamp", a.getTimestamp());
                        String key = safeFirebaseKey("attempt_" + a.getId() + "_" + a.getTimestamp());
                        attemptsMap.put(key, m);
                    }
                }

                // 6. Study Sessions
                Map<String, Object> sessionsMap = new HashMap<>();
                List<StudySessionEntity> sessionsList = db.studySessionDao().getAllSessions();
                if (sessionsList != null) {
                    for (StudySessionEntity s : sessionsList) {
                        Map<String, Object> m = new HashMap<>();
                        m.put("sessionType", s.getSessionType() != null ? s.getSessionType() : "");
                        m.put("chapterId", s.getChapterId() != null ? s.getChapterId() : "");
                        m.put("durationSeconds", s.getDurationSeconds());
                        m.put("timestamp", s.getTimestamp());
                        m.put("dateString", s.getDateString() != null ? s.getDateString() : "");
                        String key = safeFirebaseKey("session_" + s.getId() + "_" + s.getTimestamp());
                        sessionsMap.put(key, m);
                    }
                }

                // 7. Recent Activities
                Map<String, Object> recentsMap = new HashMap<>();
                List<RecentActivityEntity> recentsList = db.recentActivityDao().getAllRecentActivities();
                if (recentsList != null) {
                    for (RecentActivityEntity r : recentsList) {
                        Map<String, Object> m = new HashMap<>();
                        m.put("activityType", r.getActivityType() != null ? r.getActivityType() : "");
                        m.put("chapterId", r.getChapterId() != null ? r.getChapterId() : "");
                        m.put("chapterTitle", r.getChapterTitle() != null ? r.getChapterTitle() : "");
                        m.put("paperId", r.getPaperId() != null ? r.getPaperId() : "");
                        m.put("timestamp", r.getTimestamp());
                        String key = safeFirebaseKey(r.getActivityType() + "_" + r.getChapterId());
                        recentsMap.put(key, m);
                    }
                }

                // Assemble payload
                Map<String, Object> rootPayload = new HashMap<>();
                rootPayload.put("profile", profile);
                rootPayload.put("performance", performance);
                rootPayload.put("savedItems", savedItemsMap);
                rootPayload.put("notes", notesMap);
                rootPayload.put("practiceAttempts", attemptsMap);
                rootPayload.put("studySessions", sessionsMap);
                rootPayload.put("recentActivities", recentsMap);
                rootPayload.put("lastSyncedAt", System.currentTimeMillis());

                // Write to Realtime Database
                getRtdbInstance().getReference("users").child(uid).updateChildren(rootPayload)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Successfully synced all progress to Realtime Database.");
                            if (callback != null) {
                                mainHandler.post(callback::onSuccess);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.w(TAG, "Failed syncing to Realtime Database: " + e.getMessage());
                            if (callback != null) {
                                mainHandler.post(() -> callback.onFailure(e.getMessage()));
                            }
                        });

                // Write to Cloud Firestore backup
                try {
                    configureFirestore();
                    Map<String, Object> firestorePayload = new HashMap<>(rootPayload);
                    firestorePayload.put("lastSyncedAt", FieldValue.serverTimestamp());
                    FirebaseFirestore.getInstance().collection("users").document(uid)
                            .set(firestorePayload, SetOptions.merge());
                } catch (Exception ignored) {}

            } catch (Exception e) {
                Log.e(TAG, "Error packaging progress: " + e.getMessage(), e);
                if (callback != null) {
                    mainHandler.post(() -> callback.onFailure(e.getMessage()));
                }
            }
        });
    }

    /**
     * Backward-compatible alias for existing callers.
     */
    public static void syncProfileAndPerformance(Context context, SyncCallback callback) {
        syncAllProgressToRealtimeDatabase(context, callback);
    }

    /**
     * Backward-compatible alias for existing callers.
     */
    public static void pullProfileFromCloud(Context context, SyncCallback callback) {
        pullAllProgressFromRealtimeDatabase(context, callback);
    }

    // =========================================================================
    // Fine-Grained Live Operations (Realtime Database)
    // =========================================================================

    /**
     * Saves a saved item (Flashcard, MCQ, Active Recall, Note bookmark) directly to Realtime Database.
     */
    public static void saveSavedItemToCloud(Context context, SavedItemEntity item) {
        if (item == null) return;
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String uid = currentUser.getUid();
        Map<String, Object> m = new HashMap<>();
        m.put("type", item.getType());
        m.put("itemId", item.getItemId());
        m.put("title", item.getTitle() != null ? item.getTitle() : "");
        m.put("content", item.getContent() != null ? item.getContent() : "");
        m.put("extraInfo", item.getExtraInfo() != null ? item.getExtraInfo() : "");
        m.put("createdAt", item.getCreatedAt());

        String key = safeFirebaseKey(item.getType() + "_" + item.getItemId());
        getRtdbInstance().getReference("users").child(uid).child("savedItems").child(key).setValue(m);
    }

    /**
     * Removes a saved item from Realtime Database when the user un-saves it.
     */
    public static void removeSavedItemFromCloud(Context context, String type, String itemId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String uid = currentUser.getUid();
        String key = safeFirebaseKey(type + "_" + itemId);
        getRtdbInstance().getReference("users").child(uid).child("savedItems").child(key).removeValue();
    }

    /**
     * Saves a user-created note to Realtime Database.
     */
    public static void saveNoteToCloud(Context context, NoteEntity note) {
        if (note == null) return;
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String uid = currentUser.getUid();
        Map<String, Object> m = new HashMap<>();
        m.put("title", note.getTitle() != null ? note.getTitle() : "");
        m.put("content", note.getContent() != null ? note.getContent() : "");
        m.put("imageUri", note.getImageUri() != null ? note.getImageUri() : "");
        m.put("createdAt", note.getCreatedAt());

        String key = safeFirebaseKey("note_" + note.getId() + "_" + note.getCreatedAt());
        getRtdbInstance().getReference("users").child(uid).child("notes").child(key).setValue(m);
    }

    /**
     * Deletes a user note from Realtime Database.
     */
    public static void deleteNoteFromCloud(Context context, long createdAt, String title) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String uid = currentUser.getUid();
        getRtdbInstance().getReference("users").child(uid).child("notes")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Long cAt = child.child("createdAt").getValue(Long.class);
                            String t = child.child("title").getValue(String.class);
                            if ((cAt != null && cAt == createdAt) || (t != null && t.equals(title))) {
                                child.getRef().removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    /**
     * Record a completed quiz attempt to Realtime Database and Firestore.
     */
    public static void logQuizAttemptToCloud(Context context, String subject, String chapterTitle,
                                             int score, int totalQuestions, int timeSpentSeconds) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String uid = currentUser.getUid();
        long now = System.currentTimeMillis();

        Map<String, Object> attemptData = new HashMap<>();
        attemptData.put("subject", subject);
        attemptData.put("chapterTitle", chapterTitle);
        attemptData.put("score", score);
        attemptData.put("totalQuestions", totalQuestions);
        attemptData.put("timeSpentSeconds", timeSpentSeconds);
        attemptData.put("timestamp", now);

        getRtdbInstance().getReference("users").child(uid).child("practiceAttempts").push().setValue(attemptData);

        // Firestore mirror
        try {
            configureFirestore();
            Map<String, Object> fsData = new HashMap<>(attemptData);
            fsData.put("timestamp", FieldValue.serverTimestamp());
            FirebaseFirestore.getInstance().collection("users").document(uid)
                    .collection("quizAttempts")
                    .add(fsData);
        } catch (Exception ignored) {}

        // Trigger stats refresh
        syncAllProgressToRealtimeDatabase(context, null);
    }

    /**
     * Record a study session to Realtime Database and Firestore.
     */
    public static void logStudySessionToCloud(Context context, String subject, String chapterTitle,
                                              int durationSeconds) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String uid = currentUser.getUid();
        long now = System.currentTimeMillis();

        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("subject", subject);
        sessionData.put("chapterTitle", chapterTitle);
        sessionData.put("durationSeconds", durationSeconds);
        sessionData.put("timestamp", now);

        getRtdbInstance().getReference("users").child(uid).child("studySessions").push().setValue(sessionData);

        // Firestore mirror
        try {
            configureFirestore();
            Map<String, Object> fsData = new HashMap<>(sessionData);
            fsData.put("timestamp", FieldValue.serverTimestamp());
            FirebaseFirestore.getInstance().collection("users").document(uid)
                    .collection("studySessions")
                    .add(fsData);
        } catch (Exception ignored) {}

        // Trigger stats refresh
        syncAllProgressToRealtimeDatabase(context, null);
    }
}
