package com.example.bioprep.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bioprep.R;

import java.util.Locale;

/**
 * Result summary activity using ConstraintLayout root.
 */
public class ResultActivity extends AppCompatActivity {

    private static final String TAG = "BIOPREP_LIFECYCLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ResultActivity: onCreate");
        setContentView(R.layout.activity_result);

        int total = getIntent().getIntExtra(QuizActivity.EXTRA_TOTAL, 0);
        int correct = getIntent().getIntExtra(QuizActivity.EXTRA_CORRECT, 0);
        int incorrect = getIntent().getIntExtra(QuizActivity.EXTRA_INCORRECT, 0);
        int unanswered = getIntent().getIntExtra(QuizActivity.EXTRA_UNANSWERED, 0);
        double percentage = getIntent().getDoubleExtra(QuizActivity.EXTRA_PERCENTAGE, 0.0);

        TextView tvPercentage = findViewById(R.id.tvResultPercentage);
        TextView tvTotal = findViewById(R.id.tvResultTotal);
        TextView tvCorrect = findViewById(R.id.tvResultCorrect);
        TextView tvIncorrect = findViewById(R.id.tvResultIncorrect);
        TextView tvUnanswered = findViewById(R.id.tvResultUnanswered);
        Button btnBackHome = findViewById(R.id.btnBackHome);

        tvPercentage.setText(String.format(Locale.getDefault(), "%.1f%%", percentage));
        tvTotal.setText(String.valueOf(total));
        tvCorrect.setText(String.valueOf(correct));
        tvIncorrect.setText(String.valueOf(incorrect));
        tvUnanswered.setText(String.valueOf(unanswered));

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "ResultActivity: onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "ResultActivity: onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "ResultActivity: onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "ResultActivity: onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ResultActivity: onDestroy");
    }
}

