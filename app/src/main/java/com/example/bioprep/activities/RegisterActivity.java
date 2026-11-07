package com.example.bioprep.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bioprep.R;
import com.example.bioprep.auth.AuthRepository;
import com.example.bioprep.auth.LocalAuthRepository;
import com.example.bioprep.utils.UserPreferences;

/**
 * Register Activity implementing Requirement 12:
 * Form fields: Name, Email, Password, Confirm Password, Show/Hide Password toggles,
 * robust validation, and decoupled AuthRepository connection.
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private ImageButton btnTogglePassword;
    private ImageButton btnToggleConfirmPassword;
    private Button btnRegister;
    private TextView tvGoToSignIn;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authRepository = LocalAuthRepository.getInstance(this);

        initViews();
    }

    private void initViews() {
        etName = findViewById(R.id.etRegisterName);
        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        etConfirmPassword = findViewById(R.id.etRegisterConfirmPassword);
        btnTogglePassword = findViewById(R.id.btnToggleRegisterPassword);
        btnToggleConfirmPassword = findViewById(R.id.btnToggleRegisterConfirmPassword);
        btnRegister = findViewById(R.id.btnRegisterSubmit);
        tvGoToSignIn = findViewById(R.id.tvGoToSignIn);

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

        // Confirm Password visibility toggle
        if (btnToggleConfirmPassword != null) {
            btnToggleConfirmPassword.setOnClickListener(v -> {
                isConfirmPasswordVisible = !isConfirmPasswordVisible;
                if (isConfirmPasswordVisible) {
                    etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                etConfirmPassword.setSelection(etConfirmPassword.getText().length());
            });
        }

        // Register button
        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> performRegistration());
        }

        // Navigate to Sign In
        if (tvGoToSignIn != null) {
            tvGoToSignIn.setOnClickListener(v -> finish());
        }
    }

    private void performRegistration() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Please enter your name");
            etName.requestFocus();
            return;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        btnRegister.setEnabled(false);
        btnRegister.setText("Creating account...");

        authRepository.register(name, email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(String userName, String email) {
                btnRegister.setEnabled(true);
                btnRegister.setText("Create Account");
                Toast.makeText(RegisterActivity.this, "Account created successfully for " + userName + "!", Toast.LENGTH_SHORT).show();

                proceedToNextScreen();
            }

            @Override
            public void onFailure(String errorMessage) {
                btnRegister.setEnabled(true);
                btnRegister.setText("Create Account");
                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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

