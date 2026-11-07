package com.example.bioprep.auth;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.bioprep.database.AppDatabase;
import com.example.bioprep.database.UserAccountEntity;
import com.example.bioprep.utils.UserPreferences;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Local/Demo implementation of AuthRepository backed by Room Database and UserPreferences.
 * Designed with a clean contract so that Firebase Authentication can easily be plugged in
 * by implementing AuthRepository with FirebaseAuth without altering the UI activities.
 */
public class LocalAuthRepository implements AuthRepository {

    private static volatile LocalAuthRepository INSTANCE;
    private final Context context;
    private final AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private LocalAuthRepository(Context context) {
        this.context = context.getApplicationContext();
        this.db = AppDatabase.getInstance(this.context);
    }

    public static LocalAuthRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LocalAuthRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LocalAuthRepository(context);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void register(String name, String email, String password, AuthCallback callback) {
        executor.execute(() -> {
            try {
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

                String cleanEmail = email.trim().toLowerCase();
                UserAccountEntity existing = db.userDao().getUserByEmail(cleanEmail);
                if (existing != null) {
                    postFailure(callback, "An account with this email already exists. Please Sign In.");
                    return;
                }

                UserAccountEntity newUser = new UserAccountEntity(
                        name.trim(),
                        cleanEmail,
                        password,
                        null,
                        System.currentTimeMillis()
                );
                db.userDao().insertUser(newUser);

                // Save session in UserPreferences
                UserPreferences.saveSession(context, newUser.getName(), newUser.getEmail());

                postSuccess(callback, newUser.getName(), newUser.getEmail());
            } catch (Exception e) {
                postFailure(callback, "Registration failed: " + e.getMessage());
            }
        });
    }

    @Override
    public void signIn(String email, String password, AuthCallback callback) {
        executor.execute(() -> {
            try {
                if (email == null || email.trim().isEmpty()) {
                    postFailure(callback, "Please enter your email.");
                    return;
                }
                if (password == null || password.isEmpty()) {
                    postFailure(callback, "Please enter your password.");
                    return;
                }

                String cleanEmail = email.trim().toLowerCase();
                UserAccountEntity user = db.userDao().getUserByEmail(cleanEmail);
                if (user == null) {
                    postFailure(callback, "No account found with this email. Please Register.");
                    return;
                }

                if (!user.getPassword().equals(password)) {
                    postFailure(callback, "Incorrect password. Please try again.");
                    return;
                }

                // Update session
                UserPreferences.saveSession(context, user.getName(), user.getEmail());
                postSuccess(callback, user.getName(), user.getEmail());
            } catch (Exception e) {
                postFailure(callback, "Sign In error: " + e.getMessage());
            }
        });
    }

    @Override
    public void signOut(Runnable onComplete) {
        executor.execute(() -> {
            UserPreferences.clearSession(context);
            if (onComplete != null) {
                mainHandler.post(onComplete);
            }
        });
    }

    @Override
    public boolean isUserSignedIn() {
        return UserPreferences.isLoggedIn(context);
    }

    @Override
    public String getCurrentUserName() {
        return UserPreferences.getUserName(context);
    }

    @Override
    public String getCurrentUserEmail() {
        return UserPreferences.getUserEmail(context);
    }

    private void postSuccess(AuthCallback callback, String name, String email) {
        if (callback != null) {
            mainHandler.post(() -> callback.onSuccess(name, email));
        }
    }

    private void postFailure(AuthCallback callback, String error) {
        if (callback != null) {
            mainHandler.post(() -> callback.onFailure(error));
        }
    }
}

