package com.example.bioprep.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bioprep.R;
import com.example.bioprep.data.SampleBiologyData;

/**
 * Home dashboard activity using ConstraintLayout root.
 */
public class HomeActivity extends AppCompatActivity {

    public static final String EXTRA_PAPER_ID = "extra_paper_id";
    private static final String TAG = "BIOPREP_LIFECYCLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "HomeActivity: onCreate");
        setContentView(R.layout.activity_home);

        Button btnPaper1 = findViewById(R.id.btnPaper1);
        findViewById(R.id.cardPaper1).setOnClickListener(v -> openChapters(SampleBiologyData.PAPER_BIOLOGY_1));
        if (btnPaper1 != null) {
            btnPaper1.setOnClickListener(v -> openChapters(SampleBiologyData.PAPER_BIOLOGY_1));
        }

        Button btnPaper2 = findViewById(R.id.btnPaper2);
        findViewById(R.id.cardPaper2).setOnClickListener(v -> openChapters(SampleBiologyData.PAPER_BIOLOGY_2));
        if (btnPaper2 != null) {
            btnPaper2.setOnClickListener(v -> openChapters(SampleBiologyData.PAPER_BIOLOGY_2));
        }

        Button btnOnlineChallenge = findViewById(R.id.btnOnlineChallenge);
        View cardOnlineChallenge = findViewById(R.id.cardOnlineChallenge);
        View.OnClickListener onlineClick = v -> {
            Intent intent = new Intent(HomeActivity.this, OnlineQuizActivity.class);
            startActivity(intent);
        };
        if (cardOnlineChallenge != null) {
            cardOnlineChallenge.setOnClickListener(onlineClick);
        }
        if (btnOnlineChallenge != null) {
            btnOnlineChallenge.setOnClickListener(onlineClick);
        }
    }

    private void openChapters(String paperId) {
        Intent intent = new Intent(HomeActivity.this, ChapterActivity.class);
        intent.putExtra(EXTRA_PAPER_ID, paperId);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "HomeActivity: onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "HomeActivity: onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "HomeActivity: onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "HomeActivity: onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "HomeActivity: onDestroy");
    }
}

