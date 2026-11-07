package com.example.bioprep.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bioprep.R;

/**
 * Daily Goal & Analytics Activity implementing Stitch UI Design (Screen ID: f2da6fcd1eb14abc8f2ab5e649c85f74).
 * Displays 7-day study streak, daily progress breakdown, KPI metrics, and performance insights.
 * Also receives quiz completion results from QuizActivity or notification callback.
 */
public class AnalyticsActivity extends AppCompatActivity {

    public static final String EXTRA_TOTAL = "extra_total";
    public static final String EXTRA_CORRECT = "extra_correct";
    public static final String EXTRA_INCORRECT = "extra_incorrect";
    public static final String EXTRA_UNANSWERED = "extra_unanswered";
    public static final String EXTRA_PERCENTAGE = "extra_percentage";

    private LinearLayout layoutQuizScoreBanner;
    private TextView tvAnalyticsScorePercent;
    private TextView tvAnalyticsScoreBreakdown;
    private TextView tvAnalyticsAccuracyRate;
    private TextView tvMcqBreakdownCount;
    private TextView tvMcqBreakdownPercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        initViews();
        handleQuizResultExtras();
        setupFiltersAndActions();
        setupNavigation();
    }

    private void initViews() {
        layoutQuizScoreBanner = findViewById(R.id.layoutQuizScoreBanner);
        tvAnalyticsScorePercent = findViewById(R.id.tvAnalyticsScorePercent);
        tvAnalyticsScoreBreakdown = findViewById(R.id.tvAnalyticsScoreBreakdown);
        tvAnalyticsAccuracyRate = findViewById(R.id.tvAnalyticsAccuracyRate);
        tvMcqBreakdownCount = findViewById(R.id.tvMcqBreakdownCount);
        tvMcqBreakdownPercent = findViewById(R.id.tvMcqBreakdownPercent);

        View btnBack = findViewById(R.id.btnAnalyticsBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        View btnMore = findViewById(R.id.btnAnalyticsMore);
        if (btnMore != null) {
            btnMore.setOnClickListener(v ->
                    Toast.makeText(this, "Analytics options: Export CSV, Reset Goals", Toast.LENGTH_SHORT).show());
        }
    }

    private void handleQuizResultExtras() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_PERCENTAGE)) {
            int percentage = intent.getIntExtra(EXTRA_PERCENTAGE, 0);
            int correct = intent.getIntExtra(EXTRA_CORRECT, 0);
            int incorrect = intent.getIntExtra(EXTRA_INCORRECT, 0);
            int unanswered = intent.getIntExtra(EXTRA_UNANSWERED, 0);

            if (layoutQuizScoreBanner != null) {
                layoutQuizScoreBanner.setVisibility(View.VISIBLE);
                tvAnalyticsScorePercent.setText(percentage + "%");
                tvAnalyticsScoreBreakdown.setText(correct + " Correct • " + incorrect + " Incorrect • " + unanswered + " Unanswered");
            }

            if (tvAnalyticsAccuracyRate != null) {
                tvAnalyticsAccuracyRate.setText(percentage + "%");
            }

            if (tvMcqBreakdownCount != null) {
                tvMcqBreakdownCount.setText((correct + incorrect) + "/" + (correct + incorrect + unanswered));
            }

            if (tvMcqBreakdownPercent != null) {
                tvMcqBreakdownPercent.setText(percentage + "%");
            }
        }
    }

    private void setupFiltersAndActions() {
        TextView btnFilterDaily = findViewById(R.id.btnFilterDaily);
        TextView btnFilterWeekly = findViewById(R.id.btnFilterWeekly);
        TextView btnFilterMonthly = findViewById(R.id.btnFilterMonthly);

        View.OnClickListener filterListener = v -> {
            btnFilterDaily.setBackgroundColor(getColor(R.color.bio_surface_container_high));
            btnFilterWeekly.setBackgroundColor(getColor(R.color.bio_surface_container_high));
            btnFilterMonthly.setBackgroundColor(getColor(R.color.bio_surface_container_high));
            v.setBackgroundColor(getColor(R.color.bio_primary));
            Toast.makeText(this, "Filtered view updated", Toast.LENGTH_SHORT).show();
        };

        if (btnFilterDaily != null) btnFilterDaily.setOnClickListener(filterListener);
        if (btnFilterWeekly != null) btnFilterWeekly.setOnClickListener(filterListener);
        if (btnFilterMonthly != null) btnFilterMonthly.setOnClickListener(filterListener);

        // Option 1: Custom Goals
        View btnSetCustomGoals = findViewById(R.id.btnSetCustomGoals);
        if (btnSetCustomGoals != null) {
            btnSetCustomGoals.setOnClickListener(v -> {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Set Custom Daily Goals");

                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(32, 24, 32, 16);

                final android.widget.EditText inputMcqTarget = new android.widget.EditText(this);
                inputMcqTarget.setHint("Daily MCQ Target (e.g., 25)");
                inputMcqTarget.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                layout.addView(inputMcqTarget);

                final android.widget.EditText inputFlashTarget = new android.widget.EditText(this);
                inputFlashTarget.setHint("Daily Flashcard Target (e.g., 15)");
                inputFlashTarget.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                layout.addView(inputFlashTarget);

                builder.setView(layout);
                builder.setPositiveButton("Save Goals", (dialog, which) -> {
                    String mcq = inputMcqTarget.getText().toString().trim();
                    String flash = inputFlashTarget.getText().toString().trim();
                    String goalSummary = (!mcq.isEmpty() ? mcq : "20") + " MCQs & " + (!flash.isEmpty() ? flash : "10") + " Flashcards";
                    getSharedPreferences("BioPrepGoals", MODE_PRIVATE)
                            .edit()
                            .putString("daily_goal_summary", goalSummary)
                            .apply();
                    Toast.makeText(this, "Daily Goal Updated: " + goalSummary, Toast.LENGTH_SHORT).show();
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            });
        }

        // Option 2: Share Report (Implicit Intent)
        findViewById(R.id.btnShareReport).setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "HSC Biology Study Analytics");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "I completed 85% of my HSC Biology Daily Goal today on BioVerdant HSC! 🔥 5-day streak!");
            startActivity(Intent.createChooser(shareIntent, "Share Study Report"));
        });

        // Option 3: Adjust Alerts
        View btnAdjustAlerts = findViewById(R.id.btnAdjustAlerts);
        if (btnAdjustAlerts != null) {
            btnAdjustAlerts.setOnClickListener(v -> {
                String[] times = {"7:00 PM (Evening Prep)", "8:00 PM (Prime Study)", "9:30 PM (Night Review)", "Disable Daily Reminder"};
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Select Daily Study Reminder Time")
                        .setItems(times, (dialog, which) -> {
                            Toast.makeText(this, "Reminder set: " + times[which], Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }
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
                startActivity(new Intent(this, ProfileActivity.class)));
    }
}

