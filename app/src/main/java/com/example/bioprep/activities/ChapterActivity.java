package com.example.bioprep.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bioprep.R;
import com.example.bioprep.adapters.ChapterAdapter;
import com.example.bioprep.data.SampleBiologyData;
import com.example.bioprep.models.Chapter;

import java.util.List;

/**
 * Chapter selection activity using ListView and custom BaseAdapter.
 */
public class ChapterActivity extends AppCompatActivity {

    public static final String EXTRA_PAPER_ID = "extra_paper_id";
    public static final String EXTRA_CHAPTER_ID = "extra_chapter_id";
    public static final String EXTRA_CHAPTER_TITLE = "extra_chapter_title";
    private static final String TAG = "BIOPREP_LIFECYCLE";

    private String paperId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ChapterActivity: onCreate");
        setContentView(R.layout.activity_chapter);

        paperId = getIntent().getStringExtra(EXTRA_PAPER_ID);
        if (paperId == null || paperId.trim().isEmpty()) {
            paperId = SampleBiologyData.PAPER_BIOLOGY_1;
        }

        TextView tvPaperTitle = findViewById(R.id.tvChapterPaperTitle);
        ListView lvChapters = findViewById(R.id.lvChapters);
        TextView tvEmptyChapters = findViewById(R.id.tvEmptyChapters);

        if (SampleBiologyData.PAPER_BIOLOGY_2.equalsIgnoreCase(paperId)) {
            tvPaperTitle.setText(R.string.paper_2_title);
        } else {
            tvPaperTitle.setText(R.string.paper_1_title);
        }

        List<Chapter> chapters = SampleBiologyData.getChapters(paperId);
        if (chapters == null || chapters.isEmpty()) {
            lvChapters.setVisibility(View.GONE);
            tvEmptyChapters.setVisibility(View.VISIBLE);
        } else {
            tvEmptyChapters.setVisibility(View.GONE);
            lvChapters.setVisibility(View.VISIBLE);

            ChapterAdapter adapter = new ChapterAdapter(this, chapters);
            lvChapters.setAdapter(adapter);

            lvChapters.setOnItemClickListener((parent, view, position, id) -> {
                Chapter selectedChapter = adapter.getItem(position);
                if (selectedChapter != null) {
                    Intent intent = new Intent(ChapterActivity.this, QuizActivity.class);
                    intent.putExtra(EXTRA_PAPER_ID, paperId);
                    intent.putExtra(EXTRA_CHAPTER_ID, selectedChapter.getId());
                    intent.putExtra(EXTRA_CHAPTER_TITLE, selectedChapter.getTitle());
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "ChapterActivity: onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "ChapterActivity: onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "ChapterActivity: onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "ChapterActivity: onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ChapterActivity: onDestroy");
    }
}

