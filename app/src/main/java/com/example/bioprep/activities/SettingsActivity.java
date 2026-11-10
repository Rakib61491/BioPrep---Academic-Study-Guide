package com.example.bioprep.activities;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.bioprep.R;
import com.example.bioprep.utils.FirebaseSyncManager;
import com.example.bioprep.utils.UserPreferences;

import java.io.File;

/**
 * Settings Activity providing app configuration:
 * - Study preferences: daily study goal target (time).
 * - Breakdown targets: Active Recall, Practice Exam, Flashcards.
 * - Notifications & sound feedback toggles.
 * - Cloud Account & Firebase synchronization.
 * - Local cache cleaner and version info.
 */
public class SettingsActivity extends AppCompatActivity {

    private TextView tvSettingDailyGoalSummary;
    private TextView tvSettingTargetActiveRecall;
    private TextView tvSettingTargetExam;
    private TextView tvSettingTargetFlashcard;
    private TextView tvSettingSyncStatus;
    private SwitchCompat switchReminder;
    private SwitchCompat switchSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
        loadSettings();
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btnSettingsBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        tvSettingDailyGoalSummary = findViewById(R.id.tvSettingDailyGoalSummary);
        tvSettingTargetActiveRecall = findViewById(R.id.tvSettingTargetActiveRecall);
        tvSettingTargetExam = findViewById(R.id.tvSettingTargetExam);
        tvSettingTargetFlashcard = findViewById(R.id.tvSettingTargetFlashcard);
        tvSettingSyncStatus = findViewById(R.id.tvSettingSyncStatus);
        switchReminder = findViewById(R.id.switchReminder);
        switchSound = findViewById(R.id.switchSound);

        // Daily Study Time Goal setting
        View btnDailyGoal = findViewById(R.id.btnSettingDailyGoal);
        if (btnDailyGoal != null) {
            btnDailyGoal.setOnClickListener(v -> showDailyGoalDialog());
        }

        // Active Recall Target setting
        View btnTargetRecall = findViewById(R.id.btnSettingTargetActiveRecall);
        if (btnTargetRecall != null) {
            btnTargetRecall.setOnClickListener(v -> showTargetDialog(
                    "Active Recall Daily Target",
                    "Enter target number of active recall questions to review daily:",
                    UserPreferences.getTargetActiveRecall(this),
                    (newVal) -> {
                        UserPreferences.setTargetActiveRecall(this, newVal);
                        loadSettings();
                    }
            ));
        }

        // Exam Practice Target setting
        View btnTargetExam = findViewById(R.id.btnSettingTargetExam);
        if (btnTargetExam != null) {
            btnTargetExam.setOnClickListener(v -> showTargetDialog(
                    "Exam Practice Daily Target",
                    "Enter target number of practice questions to complete daily:",
                    UserPreferences.getTargetExam(this),
                    (newVal) -> {
                        UserPreferences.setTargetExam(this, newVal);
                        loadSettings();
                    }
            ));
        }

        // Flashcards Target setting
        View btnTargetFlashcard = findViewById(R.id.btnSettingTargetFlashcard);
        if (btnTargetFlashcard != null) {
            btnTargetFlashcard.setOnClickListener(v -> showTargetDialog(
                    "Flashcards Daily Target",
                    "Enter target number of flashcards to review daily:",
                    UserPreferences.getTargetFlashcard(this),
                    (newVal) -> {
                        UserPreferences.setTargetFlashcard(this, newVal);
                        loadSettings();
                    }
            ));
        }

        // Reminders switch
        if (switchReminder != null) {
            switchReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
                getSharedPreferences("BioPrepSettings", MODE_PRIVATE)
                        .edit().putBoolean("pref_reminders", isChecked).apply();
            });
        }

        // Sound switch
        if (switchSound != null) {
            switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
                getSharedPreferences("BioPrepSettings", MODE_PRIVATE)
                        .edit().putBoolean("pref_sound", isChecked).apply();
            });
        }

        // Cloud sync now
        View btnSyncNow = findViewById(R.id.btnSettingSyncNow);
        if (btnSyncNow != null) {
            btnSyncNow.setOnClickListener(v -> {
                if (tvSettingSyncStatus != null) tvSettingSyncStatus.setText("Syncing with Firestore...");
                FirebaseSyncManager.syncProfileAndPerformance(this, new FirebaseSyncManager.SyncCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            if (tvSettingSyncStatus != null) {
                                tvSettingSyncStatus.setText("Project: bioprep-hsc • Synced just now");
                            }
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(() -> {
                            if (tvSettingSyncStatus != null) {
                                tvSettingSyncStatus.setText("Cloud sync pending");
                            }
                        });
                    }
                });
            });
        }

        // Clear cache
        View btnClearCache = findViewById(R.id.btnSettingClearCache);
        if (btnClearCache != null) {
            btnClearCache.setOnClickListener(v -> showClearCacheConfirmation());
        }
    }

    private void loadSettings() {
        int goalMins = UserPreferences.getDailyGoalMinutes(this);
        int hours = goalMins / 60;
        int mins = goalMins % 60;
        if (tvSettingDailyGoalSummary != null) {
            tvSettingDailyGoalSummary.setText(goalMins + " minutes (" + hours + "h " + (mins < 10 ? "0" : "") + mins + "m)");
        }

        int targetRecall = UserPreferences.getTargetActiveRecall(this);
        if (tvSettingTargetActiveRecall != null) {
            tvSettingTargetActiveRecall.setText(targetRecall + " questions / day");
        }

        int targetExam = UserPreferences.getTargetExam(this);
        if (tvSettingTargetExam != null) {
            tvSettingTargetExam.setText(targetExam + " questions / day");
        }

        int targetFlash = UserPreferences.getTargetFlashcard(this);
        if (tvSettingTargetFlashcard != null) {
            tvSettingTargetFlashcard.setText(targetFlash + " cards / day");
        }

        boolean reminderPref = getSharedPreferences("BioPrepSettings", MODE_PRIVATE)
                .getBoolean("pref_reminders", true);
        if (switchReminder != null) switchReminder.setChecked(reminderPref);

        boolean soundPref = getSharedPreferences("BioPrepSettings", MODE_PRIVATE)
                .getBoolean("pref_sound", true);
        if (switchSound != null) switchSound.setChecked(soundPref);

        String email = UserPreferences.getUserEmail(this);
        if (tvSettingSyncStatus != null && email != null && !email.isEmpty()) {
            tvSettingSyncStatus.setText("Synced with " + email + " (bioprep-hsc)");
        }
    }

    private void showDailyGoalDialog() {
        int currentMinutes = UserPreferences.getDailyGoalMinutes(this);
        int currentHours = currentMinutes / 60;
        int currentRemMins = currentMinutes % 60;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Configure Daily Study Goal");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 30, 60, 20);

        final EditText etHours = new EditText(this);
        etHours.setHint("Hours (e.g. 2)");
        etHours.setInputType(InputType.TYPE_CLASS_NUMBER);
        etHours.setText(String.valueOf(currentHours));
        layout.addView(etHours);

        final EditText etMinutes = new EditText(this);
        etMinutes.setHint("Minutes (e.g. 0)");
        etMinutes.setInputType(InputType.TYPE_CLASS_NUMBER);
        etMinutes.setText(String.valueOf(currentRemMins));
        layout.addView(etMinutes);

        builder.setView(layout);
        builder.setPositiveButton("Save Goal", (dialog, which) -> {
            try {
                String hStr = etHours.getText().toString().trim();
                String mStr = etMinutes.getText().toString().trim();
                int h = hStr.isEmpty() ? 0 : Integer.parseInt(hStr);
                int m = mStr.isEmpty() ? 0 : Integer.parseInt(mStr);
                int totalMins = Math.max(15, (h * 60) + m);

                UserPreferences.setDailyGoalMinutes(this, totalMins);
                FirebaseSyncManager.syncProfileAndPerformance(this, null);
                loadSettings();
            } catch (Exception ignored) {}
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private interface TargetCallback {
        void onTargetSaved(int value);
    }

    private void showTargetDialog(String title, String message, int currentVal, TargetCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(currentVal));
        input.setSelection(input.getText().length());

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(60, 20, 60, 10);
        container.addView(input);
        builder.setView(container);

        builder.setPositiveButton("Save Target", (dialog, which) -> {
            try {
                String text = input.getText().toString().trim();
                int val = Integer.parseInt(text);
                if (val > 0 && callback != null) {
                    callback.onTargetSaved(val);
                }
            } catch (Exception ignored) {}
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showClearCacheConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Clear Temporary Cache?")
                .setMessage("This will clean temporary image downloads and cached render files without affecting your notes or study records.")
                .setPositiveButton("Clear Cache", (dialog, which) -> {
                    try {
                        File cacheDir = getCacheDir();
                        if (cacheDir != null && cacheDir.isDirectory()) {
                            File[] files = cacheDir.listFiles();
                            if (files != null) {
                                for (File f : files) f.delete();
                            }
                        }
                    } catch (Exception ignored) {}
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
