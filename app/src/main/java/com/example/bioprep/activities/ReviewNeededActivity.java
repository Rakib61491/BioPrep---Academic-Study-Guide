package com.example.bioprep.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bioprep.R;
import com.example.bioprep.database.AppDatabase;
import com.example.bioprep.database.ChapterEntity;
import com.example.bioprep.database.PracticeAttemptEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Review Needed Activity (Requirement 11):
 * Displays chapters whose latest exam practice score is below 70%,
 * with immediate action buttons to practice and improve.
 */
public class ReviewNeededActivity extends AppCompatActivity {

    public static class ReviewItem {
        public final ChapterEntity chapter;
        public final PracticeAttemptEntity latestAttempt;

        public ReviewItem(ChapterEntity chapter, PracticeAttemptEntity latestAttempt) {
            this.chapter = chapter;
            this.latestAttempt = latestAttempt;
        }
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private RecyclerView rvChapters;
    private View layoutEmpty;
    private final List<ReviewItem> items = new ArrayList<>();
    private ReviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_needed);

        ImageButton btnBack = findViewById(R.id.btnReviewBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        rvChapters = findViewById(R.id.rvReviewChapters);
        layoutEmpty = findViewById(R.id.layoutReviewEmpty);

        rvChapters.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReviewAdapter(items, this::launchPractice);
        rvChapters.setAdapter(adapter);

        loadReviewItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReviewItems();
    }

    private void loadReviewItems() {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<ChapterEntity> allChapters = db.chapterDao().getAllChapters();
            List<ReviewItem> result = new ArrayList<>();

            if (allChapters != null) {
                for (ChapterEntity ch : allChapters) {
                    PracticeAttemptEntity attempt = db.practiceDao().getLatestAttemptForChapter(ch.getId());
                    if (attempt != null && attempt.getAccuracyPercent() < 70) {
                        result.add(new ReviewItem(ch, attempt));
                    }
                }
            }

            mainHandler.post(() -> {
                items.clear();
                items.addAll(result);
                adapter.notifyDataSetChanged();

                if (items.isEmpty()) {
                    if (layoutEmpty != null) layoutEmpty.setVisibility(View.VISIBLE);
                    if (rvChapters != null) rvChapters.setVisibility(View.GONE);
                } else {
                    if (layoutEmpty != null) layoutEmpty.setVisibility(View.GONE);
                    if (rvChapters != null) rvChapters.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    private void launchPractice(ReviewItem item) {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra(QuizActivity.EXTRA_CHAPTER_ID, item.chapter.getId());
        intent.putExtra(QuizActivity.EXTRA_PAPER_ID, item.chapter.getPaper());
        intent.putExtra(QuizActivity.EXTRA_CHAPTER_TITLE, "Chapter " + item.chapter.getChapterNumber() + ": " + item.chapter.getTitleEn());
        startActivity(intent);
    }

    private interface OnPracticeClickListener {
        void onPractice(ReviewItem item);
    }

    private static class ReviewAdapter extends RecyclerView.Adapter<ReviewVH> {
        private final List<ReviewItem> list;
        private final OnPracticeClickListener listener;

        ReviewAdapter(List<ReviewItem> list, OnPracticeClickListener listener) {
            this.list = list;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ReviewVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_chapter, parent, false);
            return new ReviewVH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ReviewVH holder, int position) {
            ReviewItem item = list.get(position);
            holder.bind(item, listener);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private static class ReviewVH extends RecyclerView.ViewHolder {
        private final TextView tvIcon;
        private final TextView tvTitle;
        private final TextView tvScore;
        private final Button btnPractice;

        public ReviewVH(@NonNull View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.tvReviewChapterIcon);
            tvTitle = itemView.findViewById(R.id.tvReviewChapterTitle);
            tvScore = itemView.findViewById(R.id.tvReviewChapterScore);
            btnPractice = itemView.findViewById(R.id.btnPracticeNow);
        }

        public void bind(ReviewItem item, OnPracticeClickListener listener) {
            tvIcon.setText(item.chapter.getIcon() != null ? item.chapter.getIcon() : "⚠️");
            tvTitle.setText("Chapter " + item.chapter.getChapterNumber() + ": " + item.chapter.getTitleEn());
            tvScore.setText("Latest Score: " + item.latestAttempt.getAccuracyPercent() + "% (Needs Revision)");

            btnPractice.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPractice(item);
                }
            });
        }
    }
}

