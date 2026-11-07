package com.example.bioprep.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.bioprep.R;
import com.example.bioprep.database.DatabaseInitializer;
import com.example.bioprep.utils.UserPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * First-Time Application Agreement & Permission Management System (Requirement 1).
 * Displays full Terms & Conditions in a scrollable area.
 * The user must scroll through to reach the end before the checkbox and "Agree and Continue"
 * button become unlocked. Saves acceptance persistently and manages runtime permissions.
 */
public class AgreementActivity extends AppCompatActivity {

    private ScrollView scrollAgreement;
    private TextView tvScrollHint;
    private CheckBox cbAgree;
    private Button btnAgreeContinue;

    private boolean hasScrolledToBottom = false;

    // Runtime permissions to request sequentially or in group
    private ActivityResultLauncher<String[]> multiPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);

        initViews();
        setupScrollListener();
        setupPermissionLauncher();
    }

    private void initViews() {
        scrollAgreement = findViewById(R.id.scrollAgreement);
        tvScrollHint = findViewById(R.id.tvScrollHint);
        cbAgree = findViewById(R.id.cbAgree);
        btnAgreeContinue = findViewById(R.id.btnAgreeContinue);

        // Initially disabled and greyed out
        btnAgreeContinue.setEnabled(false);
        btnAgreeContinue.setAlpha(0.4f);
        cbAgree.setEnabled(false);

        cbAgree.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateButtonState();
        });

        btnAgreeContinue.setOnClickListener(v -> {
            if (hasScrolledToBottom && cbAgree.isChecked()) {
                // Save agreement acceptance persistently
                UserPreferences.setAgreementAccepted(AgreementActivity.this, true);
                Toast.makeText(this, "Terms and Conditions accepted.", Toast.LENGTH_SHORT).show();

                // Initialize Database if not already done
                DatabaseInitializer.initializeDatabaseAsync(getApplicationContext(), null);

                // Proceed to runtime permissions flow
                promptApplicationPermissions();
            }
        });
    }

    private void setupScrollListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollAgreement.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                checkIfScrolledToBottom();
            });
        } else {
            scrollAgreement.getViewTreeObserver().addOnScrollChangedListener(this::checkIfScrolledToBottom);
        }
    }

    private void checkIfScrolledToBottom() {
        if (hasScrolledToBottom) return;

        View child = scrollAgreement.getChildAt(0);
        if (child != null) {
            int diff = (child.getBottom() - (scrollAgreement.getHeight() + scrollAgreement.getScrollY()));
            // Within 50px of the bottom
            if (diff <= 50) {
                hasScrolledToBottom = true;
                tvScrollHint.setText("✓ Reached the end. Please check the agreement box.");
                tvScrollHint.setTextColor(Color.parseColor("#059669")); // Emerald green
                cbAgree.setEnabled(true);
                updateButtonState();
            }
        }
    }

    private void updateButtonState() {
        boolean canEnable = hasScrolledToBottom && cbAgree.isChecked();
        btnAgreeContinue.setEnabled(canEnable);
        btnAgreeContinue.setAlpha(canEnable ? 1.0f : 0.4f);
    }

    // =========================================================================
    // Permission Management: Camera, Microphone, Contacts, Location
    // =========================================================================
    private void setupPermissionLauncher() {
        multiPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                (Map<String, Boolean> result) -> {
                    boolean allGranted = true;
                    List<String> deniedList = new ArrayList<>();

                    for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                        if (!entry.getValue()) {
                            allGranted = false;
                            deniedList.add(entry.getKey());
                        }
                    }

                    if (allGranted) {
                        Toast.makeText(this, "All requested permissions granted!", Toast.LENGTH_SHORT).show();
                        proceedToHome();
                    } else {
                        handleDeniedPermissions(deniedList);
                    }
                }
        );
    }

    private void promptApplicationPermissions() {
        List<String> needed = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            needed.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            needed.add(Manifest.permission.RECORD_AUDIO);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            needed.add(Manifest.permission.READ_CONTACTS);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            needed.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (needed.isEmpty()) {
            proceedToHome();
            return;
        }

        // Show educational explanation dialog first
        new AlertDialog.Builder(this)
                .setTitle("Application Permissions Needed")
                .setMessage("To provide full features, BioPrep requests:\n\n" +
                        "• Camera: To attach study diagrams and specimen photos to notes.\n" +
                        "• Microphone: For audio voice notes and verbal recall.\n" +
                        "• Contacts: To share study achievements with classmates.\n" +
                        "• Location: To locate HSC board exam centers.\n\n" +
                        "You can allow or deny each permission. The app functions even if optional permissions are declined.")
                .setPositiveButton("Continue", (dialog, which) -> {
                    multiPermissionLauncher.launch(needed.toArray(new String[0]));
                })
                .setNegativeButton("Skip for Now", (dialog, which) -> {
                    proceedToHome();
                })
                .setCancelable(false)
                .show();
    }

    private void handleDeniedPermissions(List<String> deniedList) {
        new AlertDialog.Builder(this)
                .setTitle("Permissions Notice")
                .setMessage("Some permissions were declined (" + deniedList.size() + "). You can still use the core features, and you may grant them anytime from your device's App Settings.")
                .setPositiveButton("Continue to App", (dialog, which) -> proceedToHome())
                .setNegativeButton("Open Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    proceedToHome();
                })
                .setCancelable(false)
                .show();
    }

    private void proceedToHome() {
        if (!UserPreferences.isLoggedIn(this)) {
            UserPreferences.saveSession(this, "HSC Biology Student", "student@bioprephsc.org");
        }
        Intent intent = new Intent(AgreementActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
