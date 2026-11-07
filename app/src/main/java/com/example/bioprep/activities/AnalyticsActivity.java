package com.example.bioprep.activities;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bioprep.R;
import com.example.bioprep.database.AppDatabase;
import com.example.bioprep.database.ChapterEntity;
import com.example.bioprep.database.PracticeAttemptEntity;
import com.example.bioprep.utils.NotificationHelper;
import com.example.bioprep.utils.UserPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Daily Goal & Analytics Activity implementing Requirement 11:
 * - Dynamic device local date display.
 * - Dynamic daily progress % based on persistent study sessions against daily goal (starts at 0% for fresh day).
 * - Actual study time from Room DB without hardcoded values.
 * - Real accuracy rate calculated across all completed practice sessions.
 * - Review needed metrics with direct launch into ReviewNeededActivity.
 * - Dynamic active streak calculation requiring >= 120 minutes of study time per day.
 * - Set custom daily study goal dialog with immediate progress update.
 * - Share analytics summary via Android native share sheet.
 * - Adjust reminder time via TimePickerDialog.
 * - Removed static "Performance Insights and Highlights" section.
 */
public class AnalyticsActivity extends AppCompatActivity {

    public static final String EXTRA_TOTAL = "extra_total";
    public static final String EXTRA_CORRECT = "extra_correct";
    public static final String EXTRA_INCORRECT = "extra_incorrect";
    public static final String EXTRA_UNANSWERED = "extra_unanswered";
    public static final String EXTRA_PERCENTAGE = "extra_percentage";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private TextView tvAnalyticsDate;
    private TextView tvTodayPercent;
    private TextView tvStudyTime;
    private TextView tvAccuracyRate;
    private TextView tvReviewNeeded;
    private TextView tvStreak;
    private View cardReviewNeeded;

    private LinearLayout layoutQuizScoreBanner;
    private TextView tvAnalyticsScorePercent;
    private TextView tvAnalyticsScoreBreakdown;

    // Cache of current stats for native share
    private String currentShareText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        initViews();
        handleLegacyQuizExtras();
        setupOtherOptions();
        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDynamicAnalytics();
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btnAnalyticsBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        View btnMore = findViewById(R.id.btnAnalyticsMore);
        if (btnMore != null) {
            btnMore.setOnClickListener(v ->
                    Toast.makeText(this, "Analytics updated based on local Room SQLite records.", Toast.LENGTH_SHORT).show());
        }

        tvAnalyticsDate = findViewById(R.id.tvAnalyticsDate);
        tvTodayPercent = findViewById(R.id.tvAnalyticsTodayPercent);
        tvStudyTime = findViewById(R.id.tvAnalyticsStudyTime);
        tvAccuracyRate = findViewById(R.id.tvAnalyticsAccuracyRate);
        tvReviewNeeded = findViewById(R.id.tvAnalyticsReviewNeeded);
        tvStreak = findViewById(R.id.tvAnalyticsStreak);
        cardReviewNeeded = findViewById(R.id.cardReviewNeeded);

        layoutQuizScoreBanner = findViewById(R.id.layoutQuizScoreBanner);
        tvAnalyticsScorePercent = findViewById(R.id.tvAnalyticsScorePercent);
        tvAnalyticsScoreBreakdown = findViewById(R.id.tvAnalyticsScoreBreakdown);

        if (cardReviewNeeded != null) {
            cardReviewNeeded.setOnClickListener(v ->
                    startActivity(new Intent(this, ReviewNeededActivity.class)));
        }

        // Set Real Device Local Date
        if (tvAnalyticsDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault());
            tvAnalyticsDate.setText(dateFormat.format(new Date()));
        }
    }

    private void handleLegacyQuizExtras() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_PERCENTAGE)) {
            int percentage = intent.getIntExtra(EXTRA_PERCENTAGE, 0);
            int correct = intent.getIntExtra(EXTRA_CORRECT, 0);
            int incorrect = intent.getIntExtra(EXTRA_INCORRECT, 0);
            int unanswered = intent.getIntExtra(EXTRA_UNANSWERED, 0);

            if (layoutQuizScoreBanner != null) {
                layoutQuizScoreBanner.setVisibility(View.VISIBLE);
                if (tvAnalyticsScorePercent != null) tvAnalyticsScorePercent.setText(percentage + "%");
                if (tvAnalyticsScoreBreakdown != null) {
                    tvAnalyticsScoreBreakdown.setText(correct + " Correct • " + incorrect + " Incorrect • " + unanswered + " Unanswered");
                }
            }
        }
    }

    private void loadDynamicAnalytics() {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);

            // 1. Study time today & Total study time
            SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String todayDateString = dbDateFormat.format(new Date());

            int todaySeconds = db.studySessionDao().getStudySecondsForDate(todayDateString);
            int totalStudySeconds = db.studySessionDao().getTotalStudySeconds();

            // 2. Daily Goal Progress calculation
            int goalMinutes = UserPreferences.getDailyGoalMinutes(this);
            int goalSeconds = goalMinutes * 60;
            int dailyProgress = 0;
            if (goalSeconds > 0) {
                dailyProgress = Math.min(100, (int) Math.round((todaySeconds / (double) goalSeconds) * 100.0));
            }

            // 3. Accuracy Rate calculation
            int totalCorrect = db.practiceDao().getTotalCorrectAnswers();
            int totalAnswered = db.practiceDao().getTotalAnsweredQuestions();
            int accuracyPercent = totalAnswered > 0 ? (int) Math.round((totalCorrect / (double) totalAnswered) * 100.0) : 0;

            // 4. Review Needed calculation (Chapters whose latest attempt accuracy is < 70%)
            List<ChapterEntity> allChapters = db.chapterDao().getAllChapters();
            int reviewNeededCount = 0;
            if (allChapters != null) {
                for (ChapterEntity ch : allChapters) {
                    PracticeAttemptEntity latest = db.practiceDao().getLatestAttemptForChapter(ch.getId());
                    if (latest != null && latest.getAccuracyPercent() < 70) {
                        reviewNeededCount++;
                    }
                }
            }

            // 5. Active Streak calculation (consecutive days with >= 120 minutes = 7200s study time)
            int streakDays = 0;
            Calendar cal = Calendar.getInstance();

            // Check if today qualifies
            boolean todayQualified = (todaySeconds >= 120 * 60);
            if (todayQualified) {
                streakDays++;
            }

            // Check consecutive prior days
            while (true) {
                cal.add(Calendar.DAY_OF_YEAR, -1);
                String prevDate = dbDateFormat.format(cal.getTime());
                int prevSecs = db.studySessionDao().getStudySecondsForDate(prevDate);
                if (prevSecs >= 120 * 60) {
                    streakDays++;
                } else {
                    break;
                }
            }

            // Format study time string
            String studyTimeDisplay;
            if (totalStudySeconds >= 3600) {
                int h = totalStudySeconds / 3600;
                int m = (totalStudySeconds % 3600) / 60;
                studyTimeDisplay = String.format(Locale.getDefault(), "%dh %dm", h, m);
            } else if (totalStudySeconds >= 60) {
                int m = totalStudySeconds / 60;
                int s = totalStudySeconds % 60;
                studyTimeDisplay = String.format(Locale.getDefault(), "%dm %ds", m, s);
            } else {
                studyTimeDisplay = totalStudySeconds + "s";
            }

            final int finalDailyProgress = dailyProgress;
            final String finalStudyTimeDisplay = studyTimeDisplay;
            final int finalAccuracy = accuracyPercent;
            final int finalReviewCount = reviewNeededCount;
            final int finalStreak = streakDays;

            mainHandler.post(() -> {
                if (tvTodayPercent != null) {
                    tvTodayPercent.setText(finalDailyProgress + "%");
                }
                if (tvStudyTime != null) {
                    tvStudyTime.setText(finalStudyTimeDisplay);
                }
                if (tvAccuracyRate != null) {
                    tvAccuracyRate.setText(finalAccuracy + "%");
                }
                if (tvReviewNeeded != null) {
                    tvReviewNeeded.setText(finalReviewCount + (finalReviewCount == 1 ? " Chapter" : " Chapters"));
                }
                if (tvStreak != null) {
                    tvStreak.setText(finalStreak + (finalStreak == 1 ? " Day 🔥" : " Days 🔥"));
                }

                // Prepare Share Summary text
                currentShareText = String.format(
                        Locale.getDefault(),
                        "🎓 HSC Biology Study Guide - My Progress Report\n" +
                                "📅 Date: %s\n" +
                                "⏱️ Total Study Time: %s\n" +
                                "🎯 Exam Accuracy: %d%%\n" +
                                "🔥 Active Streak: %d Days (120m daily goal)\n" +
                                "📈 Today's Goal Completed: %d%%\n\n" +
                                "Studying for HSC Biology with BioPrep!",
                        new SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(new Date()),
                        finalStudyTimeDisplay,
                        finalAccuracy,
                        finalStreak,
                        finalDailyProgress
                );
            });
        });
    }

    private void setupOtherOptions() {
        // Option 1: Set Custom Daily Study Goal
        View btnSetCustomGoals = findViewById(R.id.btnSetCustomGoals);
        if (btnSetCustomGoals != null) {
            btnSetCustomGoals.setOnClickListener(v -> showCustomGoalDialog());
        }

        // Option 2: Share Analytics Report
        View btnShare = findViewById(R.id.btnShareReport);
        if (btnShare != null) {
            btnShare.setOnClickListener(v -> {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "HSC Biology Study Analytics");
                shareIntent.putExtra(Intent.EXTRA_TEXT, currentShareText.isEmpty() ?
                        "Check out my HSC Biology study progress on BioPrep!" : currentShareText);
                startActivity(Intent.createChooser(shareIntent, "Share Study Analytics"));
            });
        }

        // Option 3: Adjust Daily Reminder
        View btnAdjustAlerts = findViewById(R.id.btnAdjustAlerts);
        if (btnAdjustAlerts != null) {
            btnAdjustAlerts.setOnClickListener(v -> showReminderTimePicker());
        }
    }

    private void showCustomGoalDialog() {
        int currentMinutes = UserPreferences.getDailyGoalMinutes(this);
        int currentHours = currentMinutes / 60;
        int remainingMins = currentMinutes % 60;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Custom Daily Study Goal");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 16);

        TextView tvHelp = new TextView(this);
        tvHelp.setText("Default is 120 minutes (2 hours). A qualifying streak day requires at least 120 minutes.");
        tvHelp.setTextSize(12f);
        tvHelp.setPadding(0, 0, 0, 16);
        layout.addView(tvHelp);

        final EditText etHours = new EditText(this);
        etHours.setHint("Hours (e.g. 2)");
        etHours.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        etHours.setText(String.valueOf(currentHours));
        layout.addView(etHours);

        final EditText etMinutes = new EditText(this);
        etMinutes.setHint("Minutes (e.g. 0)");
        etMinutes.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        etMinutes.setText(String.valueOf(remainingMins));
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
                Toast.makeText(this, "Daily goal set to " + totalMins + " minutes (" + (totalMins / 60) + "h " + (totalMins % 60) + "m)", Toast.LENGTH_SHORT).show();
                loadDynamicAnalytics();
            } catch (Exception e) {
                Toast.makeText(this, "Invalid number entered.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showReminderTimePicker() {
        int hour = UserPreferences.getReminderHour(this);
        int minute = UserPreferences.getReminderMinute(this);

        TimePickerDialog picker = new TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
            UserPreferences.setReminderTime(this, selectedHour, selectedMinute);
            NotificationHelper.sendNotification(this, false);
            String timeFormat = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
            Toast.makeText(this, "Daily study reminder set for " + timeFormat, Toast.LENGTH_SHORT).show();
        }, hour, minute, false);

        picker.setTitle("Select Daily Study Reminder Time");
        picker.show();
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
        if (navProfile != null) navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }
}
