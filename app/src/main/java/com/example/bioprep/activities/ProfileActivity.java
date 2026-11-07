package com.example.bioprep.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bioprep.R;

/**
 * My Profile Activity implementing Stitch UI Design (Screen ID: 71d77bd7d6c14a188f6ba5eede1863d9).
 * Displays student stats, total study time, streak, school rank, and account settings.
 */
public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setupSettingsRows();
        setupNavigation();
    }

    private void initViews() {
        View btnBack = findViewById(R.id.btnProfileBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        View btnSettings = findViewById(R.id.btnProfileSettings);
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v ->
                    Toast.makeText(this, "Profile Settings: Sync Account, Manage Data", Toast.LENGTH_SHORT).show());
        }

        View btnEditAvatar = findViewById(R.id.btnEditAvatar);
        if (btnEditAvatar != null) {
            btnEditAvatar.setOnClickListener(v ->
                    Toast.makeText(this, "Change 3D Avatar (Feature unlocked at Level 6)", Toast.LENGTH_SHORT).show());
        }

        findViewById(R.id.btnProfileLogout).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Log Out")
                    .setMessage("Are you sure you want to log out of BioVerdant HSC?")
                    .setPositiveButton("Log Out", (dialog, which) -> {
                        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, WelcomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void setupSettingsRows() {
        findViewById(R.id.rowPersonalInfo).setOnClickListener(v ->
                Toast.makeText(this, "Student ID: #98324 | Batch: HSC '26", Toast.LENGTH_SHORT).show());

        // Preferences & Daily Goals opens Daily Goal & Analytics screen
        findViewById(R.id.rowGoals).setOnClickListener(v ->
                startActivity(new Intent(this, AnalyticsActivity.class)));

        findViewById(R.id.rowBadges).setOnClickListener(v ->
                Toast.makeText(this, "Badges: 🧬 Biology Scout, 🔥 Streak Master, 🎯 Perfect MCQ", Toast.LENGTH_SHORT).show());

        findViewById(R.id.rowNotifications).setOnClickListener(v ->
                Toast.makeText(this, "Daily study notifications enabled for 8:00 PM", Toast.LENGTH_SHORT).show());

        findViewById(R.id.rowSupport).setOnClickListener(v ->
                Toast.makeText(this, "HSC Biology Help Desk: support@bioprephsc.org", Toast.LENGTH_SHORT).show());
    }

    private void setupNavigation() {
        findViewById(R.id.navTabHome).setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        findViewById(R.id.navTabStudy).setOnClickListener(v ->
                startActivity(new Intent(this, ChapterActivity.class)));

        findViewById(R.id.navTabSaved).setOnClickListener(v ->
                startActivity(new Intent(this, SavedItemsActivity.class)));

        findViewById(R.id.navTabProfile).setOnClickListener(v ->
                Toast.makeText(this, "Already on My Profile", Toast.LENGTH_SHORT).show());
    }
}

