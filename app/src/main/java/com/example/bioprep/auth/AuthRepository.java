package com.example.bioprep.auth;

public interface AuthRepository {

    interface AuthCallback {
        void onSuccess(String userName, String email);
        void onFailure(String errorMessage);
    }

    void register(String name, String email, String password, AuthCallback callback);

    void signIn(String email, String password, AuthCallback callback);

    void signOut(Runnable onComplete);

    boolean isUserSignedIn();

    String getCurrentUserName();

    String getCurrentUserEmail();
}

