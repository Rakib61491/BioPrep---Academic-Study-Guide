package com.example.bioprep.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bioprep.R;
import com.example.bioprep.utils.NotificationHelper;

/**
 * My Profile Activity implementing Stitch UI Design (Screen ID: 71d77bd7d6c14a188f6ba5eede1863d9).
 * Displays student stats, total study time, streak, school rank, and rich interactive settings.
 */
public class ProfileActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "BioPrepProfilePrefs";
    private static final String KEY_NAME = "user_name";
    private static final String KEY_YEAR = "user_year";
    private static final String KEY_ID = "user_id";

    private TextView tvProfileName;
    private TextView tvProfileSubtitle;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        initViews();
        loadProfileData();
        setupSettingsRows();
        setupNavigation();
    }

    private void initViews() {
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileSubtitle = findViewById(R.id.tvProfileSubtitle);

        View btnBack = findViewById(R.id.btnProfileBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        View btnSettings = findViewById(R.id.btnProfileSettings);
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> showPreferencesDialog());
        }

        View btnEditAvatar = findViewById(R.id.btnEditAvatar);
        if (btnEditAvatar != null) {
            btnEditAvatar.setOnClickListener(v -> showEditProfileDialog());
        }

        View btnLogout = findViewById(R.id.btnProfileLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
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
    }

    private void loadProfileData() {
        String name = prefs.getString(KEY_NAME, "HSC Warrior!");
        String year = prefs.getString(KEY_YEAR, "Year 12");
        String id = prefs.getString(KEY_ID, "#98324");

        if (tvProfileName != null) tvProfileName.setText(name);
        if (tvProfileSubtitle != null) tvProfileSubtitle.setText("Biology Enthusiast | " + year + "\nID: " + id);
    }

    private void setupSettingsRows() {
        // 1. Edit Personal Info
        View rowPersonalInfo = findViewById(R.id.rowPersonalInfo);
        if (rowPersonalInfo != null) {
            rowPersonalInfo.setOnClickListener(v -> showEditProfileDialog());
        }

        // 2. Preferences & Daily Goals -> opens Daily Goal & Analytics screen
        View rowGoals = findViewById(R.id.rowGoals);
        if (rowGoals != null) {
            rowGoals.setOnClickListener(v -> startActivity(new Intent(this, AnalyticsActivity.class)));
        }

        // 3. Achievements & Badges
        View rowBadges = findViewById(R.id.rowBadges);
        if (rowBadges != null) {
            rowBadges.setOnClickListener(v -> showBadgesDialog());
        }

        // 4. Notification Settings
        View rowNotifications = findViewById(R.id.rowNotifications);
        if (rowNotifications != null) {
            rowNotifications.setOnClickListener(v -> showNotificationSettingsDialog());
        }

        // 5. App Theme / Appearance
        View rowTheme = findViewById(R.id.rowTheme);
        if (rowTheme != null) {
            rowTheme.setOnClickListener(v -> {
                Toast.makeText(this, "Active Theme: Bioluminescent Abyssal Forest (Stitch Edition)", Toast.LENGTH_SHORT).show();
            });
        }

        // 6. Help & Support
        View rowSupport = findViewById(R.id.rowSupport);
        if (rowSupport != null) {
            rowSupport.setOnClickListener(v -> showHelpDialog());
        }
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Personal Profile");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 24, 32, 16);

        final EditText editName = new EditText(this);
        editName.setHint("Warrior Name");
        editName.setText(prefs.getString(KEY_NAME, "HSC Warrior!"));
        layout.addView(editName);

        final EditText editYear = new EditText(this);
        editYear.setHint("Batch / Year (e.g. Year 12)");
        editYear.setText(prefs.getString(KEY_YEAR, "Year 12"));
        layout.addView(editYear);

        final EditText editId = new EditText(this);
        editId.setHint("Student ID (e.g. #98324)");
        editId.setText(prefs.getString(KEY_ID, "#98324"));
        layout.addView(editId);

        builder.setView(layout);
        builder.setPositiveButton("Save Profile", (dialog, which) -> {
            String name = editName.getText().toString().trim();
            String year = editYear.getText().toString().trim();
            String id = editId.getText().toString().trim();

            if (!name.isEmpty()) {
                prefs.edit()
                        .putString(KEY_NAME, name)
                        .putString(KEY_YEAR, year.isEmpty() ? "Year 12" : year)
                        .putString(KEY_ID, id.isEmpty() ? "#98324" : id)
                        .apply();
                loadProfileData();
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showPreferencesDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Study Preferences")
                .setMessage("Target: HSC Biology 2026\nDaily Focus: 20 MCQs & 10 Flashcards\nActive Recall Algorithm: Spaced Repetition (Anki)")
                .setPositiveButton("Adjust in Analytics", (dialog, which) -> startActivity(new Intent(this, AnalyticsActivity.class)))
                .setNegativeButton("Close", null)
                .show();
    }

    private void showBadgesDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Your Badges & Honors 🏆")
                .setMessage("• 🧬 Biology Scout (Level 5)\n• 🔥 12-Day Study Streak\n• 🎯 100% MCQ Mastery (Cell & Function)\n• 🧠 Active Recall Champion\n• 📚 Full Syllabus Explorer")
                .setPositiveButton("Keep Learning!", null)
                .show();
    }

    private void showNotificationSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Daily Study Notifications 🔔")
                .setMessage("Daily study reminder is active for 8:00 PM every evening.\nWould you like to test a study alert now?")
                .setPositiveButton("Send Test Alert", (dialog, which) -> {
                    NotificationHelper.sendNotification(this, true);
                    Toast.makeText(this, "Test Study Notification Sent!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showHelpDialog() {
        new AlertDialog.Builder(this)
                .setTitle("BioPrep Study Guide Help")
                .setMessage("Welcome to BioPrep Academic Study Guide!\n\n• Active Recall: Test your memory without looking at notes.\n• Flashcards: Spaced repetition for Botany & Zoology.\n• Notes: SQLite-backed notebook for your own revision summaries.\n• Exam Practice: Board chapter MCQs and timed full mock exams.\n• Resources: Official NCTB textbooks and syllabus PDFs.\n\nNeed technical help? Email: support@bioprephsc.org")
                .setPositiveButton("Got It", null)
                .show();
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
