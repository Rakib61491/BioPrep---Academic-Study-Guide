package com.example.bioprep.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bioprep.R;
import com.example.bioprep.database.AppDatabase;
import com.example.bioprep.database.RecentActivityEntity;
import com.example.bioprep.utils.RecentActivityHelper;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * Shows all recent study activities (Active Recall, Flashcards, Exam Practice)
 * with chapter titles, relative timestamps ("3 mins ago", "5 mins ago", etc.),
 * and allows immediate resumption when tapped.
 */
public class RecentActivitiesActivity extends AppCompatActivity {

    private LinearLayout layoutRecentActivitiesList;
    private LinearLayout layoutEmptyRecent;
    private TextView tvRecentCountSubtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_activities);

        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecentActivities();
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btnRecentBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        layoutRecentActivitiesList = findViewById(R.id.layoutRecentActivitiesList);
        layoutEmptyRecent = findViewById(R.id.layoutEmptyRecent);
        tvRecentCountSubtitle = findViewById(R.id.tvRecentCountSubtitle);

        TextView btnClearAll = findViewById(R.id.btnClearAllRecent);
        if (btnClearAll != null) {
            btnClearAll.setOnClickListener(v -> confirmClearAll());
        }
    }

    private void loadRecentActivities() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<RecentActivityEntity> activities = db.recentActivityDao().getAllRecentActivities();

            runOnUiThread(() -> populateActivities(activities));
        });
    }

    private void populateActivities(List<RecentActivityEntity> activities) {
        if (layoutRecentActivitiesList == null) return;
        layoutRecentActivitiesList.removeAllViews();

        if (activities == null || activities.isEmpty()) {
            if (layoutEmptyRecent != null) layoutEmptyRecent.setVisibility(View.VISIBLE);
            if (tvRecentCountSubtitle != null) tvRecentCountSubtitle.setText("No recent study history.");
            return;
        }

        if (layoutEmptyRecent != null) layoutEmptyRecent.setVisibility(View.GONE);
        if (tvRecentCountSubtitle != null) {
            tvRecentCountSubtitle.setText("Showing " + activities.size() + " recent activities:");
        }

        LayoutInflater inflater = LayoutInflater.from(this);

        for (RecentActivityEntity item : activities) {
            View card = inflater.inflate(R.layout.item_recent_activity, layoutRecentActivitiesList, false);

            TextView tvIcon = card.findViewById(R.id.tvItemRecentIcon);
            TextView tvType = card.findViewById(R.id.tvItemRecentType);
            TextView tvTitle = card.findViewById(R.id.tvItemRecentTitle);
            TextView tvTime = card.findViewById(R.id.tvItemRecentTime);

            String type = item.getActivityType();
            String icon;
            String typeLabel;
            Class<?> targetClass;

            if (RecentActivityHelper.TYPE_ACTIVE_RECALL.equals(type)) {
                icon = "🧠";
                typeLabel = "Active Recall Drill";
                targetClass = ActiveRecallActivity.class;
            } else if (RecentActivityHelper.TYPE_FLASHCARD.equals(type)) {
                icon = "🗂️";
                typeLabel = "Flashcard Session";
                targetClass = FlashcardsActivity.class;
            } else {
                icon = "📝";
                typeLabel = "Exam Practice";
                targetClass = QuizActivity.class;
            }

            if (tvIcon != null) tvIcon.setText(icon);
            if (tvType != null) tvType.setText(typeLabel);
            if (tvTitle != null) tvTitle.setText(item.getChapterTitle());
            if (tvTime != null) tvTime.setText(RecentActivityHelper.formatRelativeTime(item.getTimestamp()));

            card.setOnClickListener(v -> {
                Intent intent = new Intent(RecentActivitiesActivity.this, targetClass);
                intent.putExtra(ChapterActivity.EXTRA_CHAPTER_ID, item.getChapterId());
                intent.putExtra(ChapterActivity.EXTRA_CHAPTER_TITLE, item.getChapterTitle());
                intent.putExtra(ChapterActivity.EXTRA_PAPER_ID, item.getPaperId());
                startActivity(intent);
            });

            layoutRecentActivitiesList.addView(card);
        }
    }

    private void confirmClearAll() {
        new AlertDialog.Builder(this)
                .setTitle("Clear Recent History?")
                .setMessage("Are you sure you want to clear your recent activities history?")
                .setPositiveButton("Clear", (dialog, which) -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                        db.recentActivityDao().clearAll();
                        runOnUiThread(this::loadRecentActivities);
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

