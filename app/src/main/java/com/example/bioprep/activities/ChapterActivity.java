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
 * Exam Practice Hub Activity implementing Stitch UI Design (Screen ID: 2a7bc4f212844e18a9a7062a2d9e5377).
 * Features live practice modes (Chapter-wise, Mixed Mock, Botany, Zoology) and displays all 12 Botany & 12 Zoology chapters.
 */
public class ChapterActivity extends AppCompatActivity {

    public static final String EXTRA_PAPER_ID = "extra_paper_id";
    public static final String EXTRA_CHAPTER_ID = "extra_chapter_id";
    public static final String EXTRA_CHAPTER_TITLE = "extra_chapter_title";
    private static final String TAG = "BIOPREP_LIFECYCLE";

    private String paperId;
    private TextView tvPaperTitle;
    private TextView tvChapterCountBadge;
    private TextView tabBotany;
    private TextView tabZoology;
    private ListView lvChapters;
    private TextView tvEmptyChapters;

    private List<Chapter> allChaptersForPaper;
    private ChapterAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ChapterActivity: onCreate");
        setContentView(R.layout.activity_chapter);

        paperId = getIntent().getStringExtra(EXTRA_PAPER_ID);
        if (paperId == null || paperId.trim().isEmpty()) {
            paperId = SampleBiologyData.PAPER_BIOLOGY_1;
        }

        initViews();
        setupPracticeModes();
        loadChaptersForPaper(paperId);
        setupNavigation();
    }

    private void initViews() {
        tvPaperTitle = findViewById(R.id.tvChapterPaperTitle);
        tvChapterCountBadge = findViewById(R.id.tvChapterCountBadge);
        tabBotany = findViewById(R.id.tabBotany);
        tabZoology = findViewById(R.id.tabZoology);
        lvChapters = findViewById(R.id.lvChapters);
        tvEmptyChapters = findViewById(R.id.tvEmptyChapters);

        View btnBack = findViewById(R.id.btnChapterBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        View btnSearch = findViewById(R.id.btnChapterSearch);
        if (btnSearch != null) {
            btnSearch.setOnClickListener(v ->
                    Toast.makeText(this, "Searching exam database...", Toast.LENGTH_SHORT).show());
        }

        View btnBookmark = findViewById(R.id.btnChapterBookmark);
        if (btnBookmark != null) {
            btnBookmark.setOnClickListener(v ->
                    startActivity(new Intent(this, SavedItemsActivity.class)));
        }

        tabBotany.setOnClickListener(v -> {
            paperId = SampleBiologyData.PAPER_BIOLOGY_1;
            tabBotany.setBackgroundResource(R.drawable.bg_chip_cyan);
            tabBotany.setTextColor(getColor(R.color.bio_primary));
            tabZoology.setBackgroundColor(getColor(R.color.bio_surface_container));
            tabZoology.setTextColor(getColor(R.color.bio_on_surface_variant));
            loadChaptersForPaper(paperId);
        });

        tabZoology.setOnClickListener(v -> {
            paperId = SampleBiologyData.PAPER_BIOLOGY_2;
            tabZoology.setBackgroundResource(R.drawable.bg_chip_cyan);
            tabZoology.setTextColor(getColor(R.color.bio_primary));
            tabBotany.setBackgroundColor(getColor(R.color.bio_surface_container));
            tabBotany.setTextColor(getColor(R.color.bio_on_surface_variant));
            loadChaptersForPaper(paperId);
        });

        // Search bar filtering
        android.widget.EditText etSearchChapters = findViewById(R.id.etSearchChapters);
        if (etSearchChapters != null) {
            etSearchChapters.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterChapters(s.toString());
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {}
            });
        }
    }

    private void filterChapters(String query) {
        if (allChaptersForPaper == null || adapter == null) return;
        if (query == null || query.trim().isEmpty()) {
            adapter.updateList(allChaptersForPaper);
            return;
        }
        String lower = query.trim().toLowerCase();
        java.util.List<Chapter> filtered = new java.util.ArrayList<>();
        for (Chapter c : allChaptersForPaper) {
            if (c.getTitle().toLowerCase().contains(lower) || String.valueOf(c.getChapterNumber()).contains(lower)) {
                filtered.add(c);
            }
        }
        adapter.updateList(filtered);
    }

    private void setupPracticeModes() {
        // Mode 1: Chapter-wise MCQ
        findViewById(R.id.modeChapterMcq).setOnClickListener(v ->
                Toast.makeText(this, "Select any chapter below to launch topic-focused recall", Toast.LENGTH_SHORT).show());

        // Mode 2: Mixed Mock Speed Test
        findViewById(R.id.modeMixedMock).setOnClickListener(v -> {
            Intent intent = new Intent(ChapterActivity.this, QuizActivity.class);
            intent.putExtra(EXTRA_PAPER_ID, paperId);
            intent.putExtra(EXTRA_CHAPTER_ID, "mixed_mock");
            intent.putExtra(EXTRA_CHAPTER_TITLE, "Mixed Chapter Speed Mock");
            startActivity(intent);
        });

        // Mode 3: Botany Practice
        findViewById(R.id.modeBotanyPractice).setOnClickListener(v -> {
            paperId = SampleBiologyData.PAPER_BIOLOGY_1;
            tabBotany.performClick();
        });

        // Mode 4: Zoology Practice
        findViewById(R.id.modeZoologyPractice).setOnClickListener(v -> {
            paperId = SampleBiologyData.PAPER_BIOLOGY_2;
            tabZoology.performClick();
        });
    }

    private void loadChaptersForPaper(String targetPaper) {
        if (SampleBiologyData.PAPER_BIOLOGY_2.equalsIgnoreCase(targetPaper)) {
            tvPaperTitle.setText("Zoology — Biology 2nd Paper");
            tvChapterCountBadge.setText("12 Chapters • 300 MCQs");
        } else {
            tvPaperTitle.setText("Botany — Biology 1st Paper");
            tvChapterCountBadge.setText("12 Chapters • 300 MCQs");
        }

        allChaptersForPaper = SampleBiologyData.getChapters(targetPaper);
        if (allChaptersForPaper == null || allChaptersForPaper.isEmpty()) {
            lvChapters.setVisibility(View.GONE);
            tvEmptyChapters.setVisibility(View.VISIBLE);
        } else {
            tvEmptyChapters.setVisibility(View.GONE);
            lvChapters.setVisibility(View.VISIBLE);

            adapter = new ChapterAdapter(this, allChaptersForPaper);
            lvChapters.setAdapter(adapter);

            lvChapters.setOnItemClickListener((parent, view, position, id) -> {
                Chapter selected = adapter.getItem(position);
                if (selected != null) {
                    Intent intent = new Intent(ChapterActivity.this, QuizActivity.class);
                    intent.putExtra(EXTRA_PAPER_ID, targetPaper);
                    intent.putExtra(EXTRA_CHAPTER_ID, selected.getId());
                    intent.putExtra(EXTRA_CHAPTER_TITLE, selected.getTitle());
                    startActivity(intent);
                }
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
                Toast.makeText(this, "Already in Exam Practice Hub", Toast.LENGTH_SHORT).show());

        findViewById(R.id.navTabSaved).setOnClickListener(v ->
                startActivity(new Intent(this, SavedItemsActivity.class)));

        findViewById(R.id.navTabProfile).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }
}
