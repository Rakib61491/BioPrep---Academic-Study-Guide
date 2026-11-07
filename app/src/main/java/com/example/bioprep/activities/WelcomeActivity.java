package com.example.bioprep.activities;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bioprep.R;

/**
 * Loading Screen implementing Stitch UI Design (Screen ID: 5e7d8e45d1a747eb843fd7a0f5d2c67d).
 * Automatically animates loading progress and launches HomeActivity upon completion.
 * Covers Lab 08:
 * - Topic 1: Android Project Folder Structure (onCreate, super.onCreate, setContentView)
 * - Topic 2: Activity Lifecycle (onCreate, onStart, onResume, onPause, onStop, onDestroy, isFinishing, Toast)
 * - Topic 3: Explicit Intent with getApplicationContext()
 * - Topic 7: RelativeLayout attributes
 */
public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "BIOPREP_LIFECYCLE";
    private static final long LOADING_DURATION_MS = 1800L;

    private ProgressBar pbAutoProgress;
    private TextView tvLoadingStatus;
    private TextView tvLoadingSubStatus;
    private ValueAnimator progressAnimator;
    private Handler statusHandler;
    private boolean isNavigated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Topic 1 & 7: onCreate, super.onCreate, setContentView
        super.onCreate(savedInstanceState);
        Log.d(TAG, "WelcomeActivity (Loading Screen): onCreate");
        setContentView(R.layout.activity_welcome);

        // Topic 2: Toast.makeText(...).show() with getApplicationContext()
        Toast.makeText(getApplicationContext(), "WelcomeActivity: Loading Bio-Universe...", Toast.LENGTH_SHORT).show();

        pbAutoProgress = findViewById(R.id.pbWelcomeAutoProgress);
        tvLoadingStatus = findViewById(R.id.tvLoadingStatus);
        tvLoadingSubStatus = findViewById(R.id.tvLoadingSubStatus);

        // Pre-initialize Room Database in background
        com.example.bioprep.database.DatabaseInitializer.initializeDatabaseAsync(getApplicationContext(), null);

        View root = findViewById(R.id.layoutWelcomeRoot);
        if (root != null) {
            root.setOnClickListener(v -> navigateToHome());
        }

        ImageButton btnBack = findViewById(R.id.btnWelcomeBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                cancelLoading();
                finish();
            });
        }

        startAutoLoading();
    }

    /**
     * Smoothly animates the loading progress and transitions to HomeActivity.
     */
    private void startAutoLoading() {
        statusHandler = new Handler(Looper.getMainLooper());

        // Update microcopy dynamically during progress
        statusHandler.postDelayed(() -> {
            if (!isFinishing() && tvLoadingStatus != null) {
                tvLoadingStatus.setText("Prepping your study desk...");
            }
        }, 300);

        statusHandler.postDelayed(() -> {
            if (!isFinishing() && tvLoadingSubStatus != null) {
                tvLoadingSubStatus.setText("Initializing Botany & Zoology modules...");
            }
        }, 900);

        statusHandler.postDelayed(() -> {
            if (!isFinishing() && tvLoadingStatus != null) {
                tvLoadingStatus.setText("Ready to master HSC Biology! 🧬");
            }
        }, 1500);

        // Animate the progress bar from 0 to 100
        progressAnimator = ValueAnimator.ofInt(0, 100);
        progressAnimator.setDuration(LOADING_DURATION_MS);
        progressAnimator.setInterpolator(new DecelerateInterpolator());
        progressAnimator.addUpdateListener(animator -> {
            if (pbAutoProgress != null) {
                int progress = (int) animator.getAnimatedValue();
                pbAutoProgress.setProgress(progress);
                if (progress >= 100 && !isNavigated) {
                    navigateToHome();
                }
            }
        });
        progressAnimator.start();
    }

    private synchronized void navigateToHome() {
        if (isNavigated || isFinishing()) return;
        isNavigated = true;

        if (!com.example.bioprep.utils.UserPreferences.isAgreementAccepted(this)) {
            Intent intent = new Intent(getApplicationContext(), AgreementActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        }
        finish();
    }

    private void cancelLoading() {
        if (progressAnimator != null) {
            progressAnimator.cancel();
        }
        if (statusHandler != null) {
            statusHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "WelcomeActivity: onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "WelcomeActivity: onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "WelcomeActivity: onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "WelcomeActivity: onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "WelcomeActivity: onRestart");
    }

    @Override
    protected void onDestroy() {
        cancelLoading();
        // Topic 2: isFinishing()
        if (isFinishing()) {
            Log.d(TAG, "WelcomeActivity: onDestroy (isFinishing = true)");
        }
        super.onDestroy();
    }
}
