package com.example.bioprep.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bioprep.R;
import com.example.bioprep.auth.AuthRepository;
import com.example.bioprep.auth.FirebaseAuthRepository;
import com.example.bioprep.utils.FirebaseSyncManager;
import com.example.bioprep.utils.UserPreferences;

/**
 * Sign In Activity implementing Requirement 12:
 * Connected to decoupled AuthRepository for local SQLite storage with Firebase
 * Auth readiness.
 */
public class SignInActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private ImageButton btnTogglePassword;
    private Button btnSignIn;
    private TextView tvGoToRegister;

    private boolean isPasswordVisible = false;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        authRepository = FirebaseAuthRepository.getInstance(this);

        initViews();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etSignInEmail);
        etPassword = findViewById(R.id.etSignInPassword);
        btnTogglePassword = findViewById(R.id.btnToggleSignInPassword);
        btnSignIn = findViewById(R.id.btnSignInSubmit);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        // Password visibility toggle
        if (btnTogglePassword != null) {
            btnTogglePassword.setOnClickListener(v -> {
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                etPassword.setSelection(etPassword.getText().length());
            });
        }

        // Sign In button
        if (btnSignIn != null) {
            btnSignIn.setOnClickListener(v -> performSignIn());
        }

        // Navigate to Register
        if (tvGoToRegister != null) {
            tvGoToRegister.setOnClickListener(v -> {
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
            });
        }

        // Auto-scroll input fields into view when software keyboard appears
        com.example.bioprep.utils.KeyboardUtils.autoScrollOnFocus(
                findViewById(R.id.scrollSignIn),
                etEmail,
                etPassword
        );
    }

    private void performSignIn() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Please enter your email");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Please enter your password");
            etPassword.requestFocus();
            return;
        }

        btnSignIn.setEnabled(false);
        btnSignIn.setText("Signing in...");

        authRepository.signIn(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(String userName, String email) {
                btnSignIn.setEnabled(true);
                btnSignIn.setText("Sign In");

                FirebaseSyncManager.pullProfileFromCloud(SignInActivity.this, null);
                FirebaseSyncManager.syncProfileAndPerformance(SignInActivity.this, null);

                proceedToNextScreen();
            }

            @Override
            public void onFailure(String errorMessage) {
                btnSignIn.setEnabled(true);
                btnSignIn.setText("Sign In");
                etPassword.setError(errorMessage);
                etPassword.requestFocus();
            }
        });
    }

    private void proceedToNextScreen() {
        Intent intent;
        if (!UserPreferences.isAgreementAccepted(this)) {
            intent = new Intent(this, AgreementActivity.class);
        } else {
            intent = new Intent(this, HomeActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
