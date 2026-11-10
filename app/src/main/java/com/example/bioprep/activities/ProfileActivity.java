package com.example.bioprep.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bioprep.R;
import com.example.bioprep.auth.FirebaseAuthRepository;
import com.example.bioprep.database.AppDatabase;
import com.example.bioprep.utils.FirebaseSyncManager;
import com.example.bioprep.utils.UserPreferences;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Profile Activity implementing Requirement 12:
 * - Avatar Image Picker via Pen Icon, saved persistently and reflected in both Profile and Home.
 * - Student level displayed as "HSC Biology Student".
 * - Achievements displaying: Study Time, Active Streak, Quiz Completed, Accuracy Rate.
 * - Achievements and Badges dialog showing ONLY: Number of Streak, Number of Practice Sessions Completed, Accuracy Rate.
 * - Dedicated Help & Support launch (HelpSupportActivity).
 * - Full Log Out confirmation clearing personal user data (notes, avatar, sessions, attempts, streak)
 *   while preserving bundled educational content.
 */
public class ProfileActivity extends AppCompatActivity {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private TextView tvProfileName;
    private TextView tvProfileSubtitle;
    private TextView tvProfileLevel;
    private TextView tvAvatarPlaceholder;
    private ImageView ivAvatarImage;

    // Achievement Views
    private TextView tvProfileStudyTime;
    private TextView tvProfileStreak;
    private TextView tvProfileQuizCompleted;
    private TextView tvProfileAccuracyRate;

    // Cache of dynamic stats for badges dialog
    private int cachedStreak = 0;
    private int cachedSessionsCount = 0;
    private int cachedAccuracyRate = 0;

    // Avatar Image Picker Launcher
    private final ActivityResultLauncher<String> avatarPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    saveAvatarFromUri(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setupActionRows();
        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileData();
        loadDynamicAchievements();
        FirebaseSyncManager.syncProfileAndPerformance(this, null);
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btnProfileBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileSubtitle = findViewById(R.id.tvProfileSubtitle);
        tvProfileLevel = findViewById(R.id.tvProfileLevel);
        tvAvatarPlaceholder = findViewById(R.id.tvProfileAvatarPlaceholder);
        ivAvatarImage = findViewById(R.id.ivProfileAvatarImage);

        tvProfileStudyTime = findViewById(R.id.tvProfileStudyTime);
        tvProfileStreak = findViewById(R.id.tvProfileStreak);
        tvProfileQuizCompleted = findViewById(R.id.tvProfileQuizCompleted);
        tvProfileAccuracyRate = findViewById(R.id.tvProfileAccuracyRate);

        if (tvProfileLevel != null) {
            tvProfileLevel.setText(UserPreferences.STUDENT_LEVEL_TITLE + " 🧬");
        }

        // Pen icon on avatar picture to change avatar
        View btnEditAvatar = findViewById(R.id.btnEditAvatar);
        if (btnEditAvatar != null) {
            btnEditAvatar.setOnClickListener(v -> avatarPickerLauncher.launch("image/*"));
        }

        View btnSettings = findViewById(R.id.btnProfileSettings);
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        }

        View btnLogout = findViewById(R.id.btnProfileLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> showLogoutConfirmationDialog());
        }
    }

    private void loadProfileData() {
        String name = UserPreferences.getUserName(this);
        String email = UserPreferences.getUserEmail(this);

        if (tvProfileName != null) {
            tvProfileName.setText(name);
        }
        if (tvProfileSubtitle != null) {
            tvProfileSubtitle.setText(email.isEmpty() ? "HSC Candidate | Biology Major" : email);
        }

        // Load Avatar
        String avatarPath = UserPreferences.getAvatarPath(this);
        if (avatarPath != null && new File(avatarPath).exists()) {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(avatarPath);
                if (bitmap != null && ivAvatarImage != null) {
                    ivAvatarImage.setImageBitmap(bitmap);
                    ivAvatarImage.setVisibility(View.VISIBLE);
                    if (tvAvatarPlaceholder != null) tvAvatarPlaceholder.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                if (ivAvatarImage != null) {
                    ivAvatarImage.setImageResource(R.drawable.app_logo);
                    ivAvatarImage.setVisibility(View.VISIBLE);
                }
                if (tvAvatarPlaceholder != null) tvAvatarPlaceholder.setVisibility(View.GONE);
            }
        } else {
            if (ivAvatarImage != null) {
                ivAvatarImage.setImageResource(R.drawable.app_logo);
                ivAvatarImage.setVisibility(View.VISIBLE);
            }
            if (tvAvatarPlaceholder != null) tvAvatarPlaceholder.setVisibility(View.GONE);
        }
    }

    private void saveAvatarFromUri(Uri uri) {
        executor.execute(() -> {
            try {
                File dir = new File(getFilesDir(), "avatars");
                if (!dir.exists()) dir.mkdirs();
                File destFile = new File(dir, "avatar_user_" + System.currentTimeMillis() + ".jpg");

                try (InputStream in = getContentResolver().openInputStream(uri);
                     OutputStream out = new FileOutputStream(destFile)) {
                    byte[] buffer = new byte[8192];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    out.flush();
                }

                UserPreferences.setAvatarPath(this, destFile.getAbsolutePath());
                FirebaseSyncManager.syncProfileAndPerformance(this, null);

                mainHandler.post(this::loadProfileData);
            } catch (Exception e) {
                // Ignore avatar update error
            }
        });
    }

    private void loadDynamicAchievements() {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);

            int totalStudySeconds = db.studySessionDao().getTotalStudySeconds();
            int quizCompletedCount = db.practiceDao().getCompletedQuizCount();
            int totalCorrect = db.practiceDao().getTotalCorrectAnswers();
            int totalAnswered = db.practiceDao().getTotalAnsweredQuestions();
            int accuracyPercent = totalAnswered > 0 ? (int) Math.round((totalCorrect / (double) totalAnswered) * 100.0) : 0;

            // Calculate active streak (days with >= 120 minutes of study time)
            SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String todayDate = dbDateFormat.format(new Date());
            int todaySecs = db.studySessionDao().getStudySecondsForDate(todayDate);

            int streak = 0;
            if (todaySecs >= 120 * 60) {
                streak++;
            }

            Calendar cal = Calendar.getInstance();
            while (true) {
                cal.add(Calendar.DAY_OF_YEAR, -1);
                String prevDate = dbDateFormat.format(cal.getTime());
                int secs = db.studySessionDao().getStudySecondsForDate(prevDate);
                if (secs >= 120 * 60) {
                    streak++;
                } else {
                    break;
                }
            }

            String studyTimeDisplay;
            if (totalStudySeconds >= 3600) {
                studyTimeDisplay = (totalStudySeconds / 3600) + "h " + ((totalStudySeconds % 3600) / 60) + "m";
            } else if (totalStudySeconds >= 60) {
                studyTimeDisplay = (totalStudySeconds / 60) + "m " + (totalStudySeconds % 60) + "s";
            } else {
                studyTimeDisplay = totalStudySeconds + "s";
            }

            cachedStreak = streak;
            cachedSessionsCount = quizCompletedCount;
            cachedAccuracyRate = accuracyPercent;

            final String finalStudyTime = studyTimeDisplay;
            final int finalStreak = streak;
            final int finalQuizzes = quizCompletedCount;
            final int finalAccuracy = accuracyPercent;

            mainHandler.post(() -> {
                if (tvProfileStudyTime != null) tvProfileStudyTime.setText(finalStudyTime);
                if (tvProfileStreak != null) tvProfileStreak.setText(finalStreak + (finalStreak == 1 ? " Day 🔥" : " Days 🔥"));
                if (tvProfileQuizCompleted != null) tvProfileQuizCompleted.setText(String.valueOf(finalQuizzes));
                if (tvProfileAccuracyRate != null) tvProfileAccuracyRate.setText(finalAccuracy + "%");
            });
        });
    }

    private void setupActionRows() {
        // Row 1: Edit Personal Info
        View rowPersonalInfo = findViewById(R.id.rowPersonalInfo);
        if (rowPersonalInfo != null) {
            rowPersonalInfo.setOnClickListener(v -> showEditProfileDialog());
        }

        // Row 2: Preferences & Daily Goals -> Opens Daily Goal & Analytics Screen
        View rowGoals = findViewById(R.id.rowGoals);
        if (rowGoals != null) {
            rowGoals.setOnClickListener(v -> startActivity(new Intent(this, AnalyticsActivity.class)));
        }

        // Row 3: Achievements & Badges Dialog
        View rowBadges = findViewById(R.id.rowBadges);
        if (rowBadges != null) {
            rowBadges.setOnClickListener(v -> showAchievementsDialog());
        }

        // Row 6: Help & Support -> Opens dedicated HelpSupportActivity
        View rowSupport = findViewById(R.id.rowSupport);
        if (rowSupport != null) {
            rowSupport.setOnClickListener(v -> startActivity(new Intent(this, HelpSupportActivity.class)));
        }
    }

    /**
     * Requirement 12: Displays ONLY these three metrics:
     * 1. Number of Streak
     * 2. Number of Practice/Active Recall Sessions Completed
     * 3. Accuracy Rate
     * No dummy badges or unrelated achievements.
     */
    private void showAchievementsDialog() {
        String message = String.format(
                Locale.getDefault(),
                "1. Active Study Streak: %d Day%s (120m/day qualifying rule)\n\n" +
                        "2. Practice & Active Recall Sessions Completed: %d Session%s\n\n" +
                        "3. Overall Accuracy Rate: %d%%",
                cachedStreak, cachedStreak == 1 ? "" : "s",
                cachedSessionsCount, cachedSessionsCount == 1 ? "" : "s",
                cachedAccuracyRate
        );

        new AlertDialog.Builder(this)
                .setTitle("Your Study Achievements 🏆")
                .setMessage(message)
                .setPositiveButton("Close", null)
                .show();
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Personal Info");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 16);

        final EditText editName = new EditText(this);
        editName.setHint("Student Name");
        editName.setText(UserPreferences.getUserName(this));
        layout.addView(editName);

        builder.setView(layout);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = editName.getText().toString().trim();
            if (!name.isEmpty()) {
                UserPreferences.setUserName(this, name);
                FirebaseSyncManager.syncProfileAndPerformance(this, null);
                loadProfileData();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    /**
     * Requirement 12: Confirmation dialog explaining data removal on logout.
     * Clears personal data (notes, avatar, study sessions, attempts, streak)
     * while preserving bundled curriculum content.
     */
    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out of BioPrep?")
                .setMessage("Logging out will remove your personal user data from this device:\n\n" +
                        "• Personal notes and attachments\n" +
                        "• Profile picture / avatar\n" +
                        "• Study sessions and study time records\n" +
                        "• Exam practice attempts and scores\n" +
                        "• Daily goals and streak history\n" +
                        "• Current login session\n\n" +
                        "Bundled educational content (chapters, questions, flashcards, and syllabus resources) will remain available.\n\n" +
                        "Are you sure you want to log out?")
                .setPositiveButton("Log Out", (dialog, which) -> performLogout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performLogout() {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);

            // 1. Delete user personal data
            db.noteDao().clearAllNotes();
            db.studySessionDao().clearAllSessions();
            db.practiceDao().clearAllAttempts();
            db.practiceDao().clearAllAnswers();
            db.savedItemDao().clearAll();
            db.recentActivityDao().clearAll();

            // 2. Clear avatar file
            String avatarPath = UserPreferences.getAvatarPath(this);
            if (avatarPath != null) {
                File f = new File(avatarPath);
                if (f.exists()) f.delete();
            }

            // 3. Clear user preferences session & Firebase signOut
            UserPreferences.clearSession(this);
            FirebaseAuthRepository.getInstance(this).signOut(null);

            mainHandler.post(() -> {
                Intent intent = new Intent(this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        });
    }

    private void setupNavigation() {
        View navHome = findViewById(R.id.navTabHome);
        if (navHome != null) navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        View navStudy = findViewById(R.id.navTabStudy);
        if (navStudy != null) navStudy.setOnClickListener(v ->
                startActivity(new Intent(this, ChapterActivity.class)));

        View navSaved = findViewById(R.id.navTabSaved);
        if (navSaved != null) navSaved.setOnClickListener(v ->
                startActivity(new Intent(this, SavedItemsActivity.class)));

        View navProfile = findViewById(R.id.navTabProfile);
        if (navProfile != null) navProfile.setOnClickListener(v -> {});
    }
}
