package com.example.bioprep.auth;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.bioprep.database.AppDatabase;
import com.example.bioprep.database.UserAccountEntity;
import com.example.bioprep.utils.FirebaseSyncManager;
import com.example.bioprep.utils.UserPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * FirebaseAuthRepository connects the application to Google Firebase Authentication
 * and Cloud Firestore ("BioPrep-HSC"), while maintaining full offline cache
 * and Room Database synchronization.
 */
public class FirebaseAuthRepository implements AuthRepository {

    private static final String TAG = "FirebaseAuthRepository";
    private static volatile FirebaseAuthRepository INSTANCE;

    private final Context context;
    private final AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private FirebaseAuthRepository(Context context) {
        this.context = context.getApplicationContext();
        this.db = AppDatabase.getInstance(this.context);
        FirebaseSyncManager.configureFirestore();
    }

    public static FirebaseAuthRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (FirebaseAuthRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FirebaseAuthRepository(context);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void register(String name, String email, String password, AuthCallback callback) {
        if (name == null || name.trim().isEmpty()) {
            postFailure(callback, "Please enter your name.");
            return;
        }
        if (email == null || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            postFailure(callback, "Please enter a valid email address.");
            return;
        }
        if (password == null || password.length() < 6) {
            postFailure(callback, "Password must be at least 6 characters.");
            return;
        }

        final String cleanName = name.trim();
        final String cleanEmail = email.trim().toLowerCase();

        try {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(cleanEmail, password)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser firebaseUser = authResult.getUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();

                            // 1. Update Firebase User display name
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(cleanName)
                                    .build();
                            firebaseUser.updateProfile(profileUpdates);

                            // 2. Initialize Firestore user profile document
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("uid", uid);
                            userData.put("name", cleanName);
                            userData.put("email", cleanEmail);
                            userData.put("bio", "HSC Biology Student");
                            userData.put("studentLevel", UserPreferences.STUDENT_LEVEL_TITLE);
                            userData.put("avatarPath", "");
                            userData.put("dailyGoalMinutes", UserPreferences.DEFAULT_DAILY_GOAL_MINUTES);
                            userData.put("createdAt", com.google.firebase.Timestamp.now());
                            userData.put("lastSyncedAt", com.google.firebase.Timestamp.now());

                            Map<String, Object> performance = new HashMap<>();
                            performance.put("studyTimeSeconds", 0);
                            performance.put("activeStreak", 0);
                            performance.put("quizzesCompleted", 0);
                            performance.put("accuracyRate", 0);
                            performance.put("totalQuestionsAnswered", 0);
                            performance.put("totalCorrectAnswers", 0);
                            userData.put("performance", performance);

                            FirebaseFirestore.getInstance().collection("users").document(uid)
                                    .set(userData, SetOptions.merge())
                                    .addOnFailureListener(e -> Log.w(TAG, "Notice setting Firestore profile: " + e.getMessage()));

                            // 3. Cache locally in Room DB and UserPreferences
                            executor.execute(() -> {
                                UserAccountEntity localUser = new UserAccountEntity(
                                        cleanName,
                                        cleanEmail,
                                        password,
                                        null,
                                        System.currentTimeMillis()
                                );
                                db.userDao().insertUser(localUser);
                            });

                            UserPreferences.saveSession(context, cleanName, cleanEmail);
                            postSuccess(callback, cleanName, cleanEmail);
                        } else {
                            postFailure(callback, "Registration failed: No user returned.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Firebase createUser error: " + e.getMessage());
                        // Fallback: If network is offline or Firebase configuration issue, register locally
                        executor.execute(() -> {
                            UserAccountEntity existing = db.userDao().getUserByEmail(cleanEmail);
                            if (existing != null) {
                                postFailure(callback, "An account with this email already exists.");
                            } else {
                                UserAccountEntity newUser = new UserAccountEntity(
                                        cleanName,
                                        cleanEmail,
                                        password,
                                        null,
                                        System.currentTimeMillis()
                                );
                                db.userDao().insertUser(newUser);
                                UserPreferences.saveSession(context, cleanName, cleanEmail);
                                postSuccess(callback, cleanName, cleanEmail);
                            }
                        });
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception during register: " + e.getMessage(), e);
            postFailure(callback, "Auth error: " + e.getMessage());
        }
    }

    @Override
    public void signIn(String email, String password, AuthCallback callback) {
        if (email == null || email.trim().isEmpty()) {
            postFailure(callback, "Please enter your email.");
            return;
        }
        if (password == null || password.isEmpty()) {
            postFailure(callback, "Please enter your password.");
            return;
        }

        final String cleanEmail = email.trim().toLowerCase();

        try {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signInWithEmailAndPassword(cleanEmail, password)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser firebaseUser = authResult.getUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            String fallbackName = firebaseUser.getDisplayName() != null && !firebaseUser.getDisplayName().isEmpty()
                                    ? firebaseUser.getDisplayName() : cleanEmail.split("@")[0];

                            // Pull latest profile data from Cloud Firestore
                            FirebaseFirestore.getInstance().collection("users").document(uid).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        String resolvedName = fallbackName;
                                        if (documentSnapshot != null && documentSnapshot.exists()) {
                                            String remoteName = documentSnapshot.getString("name");
                                            if (remoteName != null && !remoteName.trim().isEmpty()) {
                                                resolvedName = remoteName.trim();
                                            }
                                            Long dailyGoal = documentSnapshot.getLong("dailyGoalMinutes");
                                            if (dailyGoal != null && dailyGoal > 0) {
                                                UserPreferences.setDailyGoalMinutes(context, dailyGoal.intValue());
                                            }
                                        }

                                        final String finalName = resolvedName;
                                        UserPreferences.saveSession(context, finalName, cleanEmail);

                                        // Cache into Room
                                        executor.execute(() -> {
                                            UserAccountEntity local = db.userDao().getUserByEmail(cleanEmail);
                                            if (local == null) {
                                                db.userDao().insertUser(new UserAccountEntity(
                                                        finalName,
                                                        cleanEmail,
                                                        password,
                                                        null,
                                                        System.currentTimeMillis()
                                                ));
                                            }
                                        });

                                        postSuccess(callback, finalName, cleanEmail);
                                    })
                                    .addOnFailureListener(e -> {
                                        UserPreferences.saveSession(context, fallbackName, cleanEmail);
                                        postSuccess(callback, fallbackName, cleanEmail);
                                    });
                        } else {
                            postFailure(callback, "Sign in failed: No user returned.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Firebase signIn failed, attempting local Room fallback: " + e.getMessage());
                        // Offline fallback: Check Room SQLite
                        executor.execute(() -> {
                            UserAccountEntity cachedUser = db.userDao().getUserByEmail(cleanEmail);
                            if (cachedUser != null && cachedUser.getPassword().equals(password)) {
                                UserPreferences.saveSession(context, cachedUser.getName(), cachedUser.getEmail());
                                postSuccess(callback, cachedUser.getName(), cachedUser.getEmail());
                            } else {
                                postFailure(callback, e.getMessage() != null ? e.getMessage() : "Authentication failed.");
                            }
                        });
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception during signIn: " + e.getMessage(), e);
            executor.execute(() -> {
                UserAccountEntity cachedUser = db.userDao().getUserByEmail(cleanEmail);
                if (cachedUser != null && cachedUser.getPassword().equals(password)) {
                    UserPreferences.saveSession(context, cachedUser.getName(), cachedUser.getEmail());
                    postSuccess(callback, cachedUser.getName(), cachedUser.getEmail());
                } else {
                    postFailure(callback, "Authentication error: " + e.getMessage());
                }
            });
        }
    }

    @Override
    public void signOut(Runnable onComplete) {
        try {
            FirebaseAuth.getInstance().signOut();
        } catch (Exception e) {
            Log.w(TAG, "Notice during signOut: " + e.getMessage());
        }

        UserPreferences.clearSession(context);

        if (onComplete != null) {
            mainHandler.post(onComplete);
        }
    }

    @Override
    public boolean isUserSignedIn() {
        if (!UserPreferences.isLoggedIn(context)) {
            return false;
        }
        try {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                return true;
            }
        } catch (Exception ignored) {}
        String email = UserPreferences.getUserEmail(context);
        return email != null && !email.trim().isEmpty();
    }

    @Override
    public String getCurrentUserName() {
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
                return user.getDisplayName();
            }
        } catch (Exception ignored) {}
        return UserPreferences.getUserName(context);
    }

    @Override
    public String getCurrentUserEmail() {
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
                return user.getEmail();
            }
        } catch (Exception ignored) {}
        return UserPreferences.getUserEmail(context);
    }

    private void postSuccess(AuthCallback callback, String name, String email) {
        if (callback != null) {
            mainHandler.post(() -> callback.onSuccess(name, email));
        }
    }

    private void postFailure(AuthCallback callback, String errorMessage) {
        if (callback != null) {
            mainHandler.post(() -> callback.onFailure(errorMessage));
        }
    }
}

