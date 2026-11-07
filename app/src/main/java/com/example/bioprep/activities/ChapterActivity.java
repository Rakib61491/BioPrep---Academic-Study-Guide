package com.example.bioprep.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.example.bioprep.R;
import com.example.bioprep.data.SampleBiologyData;
import com.example.bioprep.models.Chapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Chapter Selection Hub for Exam Practice and Flashcards.
 * Organizes curriculum strictly into:
 * 1. Botany – Biology 1st Paper (উদ্ভিদবিজ্ঞান) [12 Chapters]
 * 2. Zoology – Biology 2nd Paper (প্রাণিবিজ্ঞান) [12 Chapters]
 *
 * Provides down arrow keys on the right side of Botany and Zoology to minimize and maximize each list.
 */
public class ChapterActivity extends AppCompatActivity {

    public static final String EXTRA_PAPER_ID = "extra_paper_id";
    public static final String EXTRA_CHAPTER_ID = "extra_chapter_id";
    public static final String EXTRA_CHAPTER_TITLE = "extra_chapter_title";
    public static final String EXTRA_MODE = "extra_mode";
    public static final String MODE_EXAM = "mode_exam";
    public static final String MODE_FLASHCARDS = "mode_flashcards";
    private static final String TAG = "BIOPREP_CHAPTER_HUB";

    private String mode = MODE_EXAM;
    private String paperId;

    // UI elements
    private TextView tvPaperTitle;
    private EditText etSearchChapters;

    // Botany Accordion
    private View cardBotanyAccordion;
    private View btnToggleBotany;
    private ImageView ivBotanyArrow;
    private LinearLayout layoutBotanyExpanded;
    private LinearLayout llBotanyChapters;
    private TextView tvNoBotanyMatches;
    private boolean isBotanyExpanded = true;

    // Zoology Accordion
    private View cardZoologyAccordion;
    private View btnToggleZoology;
    private ImageView ivZoologyArrow;
    private LinearLayout layoutZoologyExpanded;
    private LinearLayout llZoologyChapters;
    private TextView tvNoZoologyMatches;
    private boolean isZoologyExpanded = true;

    // Chapter data
    private List<Chapter> botanyChapters = new ArrayList<>();
    private List<Chapter> zoologyChapters = new ArrayList<>();
    private final List<View> botanyItemViews = new ArrayList<>();
    private final List<View> zoologyItemViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ChapterActivity: onCreate");
        setContentView(R.layout.activity_chapter);

        mode = getIntent().getStringExtra(EXTRA_MODE);
        if (mode == null || mode.trim().isEmpty()) {
            mode = MODE_EXAM;
        }

        paperId = getIntent().getStringExtra(EXTRA_PAPER_ID);
        if (paperId == null || paperId.trim().isEmpty()) {
            paperId = SampleBiologyData.PAPER_BIOLOGY_1;
        }

        initViews();
        loadChaptersData();
        renderChapterLists();
        setupSearchAndFilter();
        setupAccordionToggles();
        setupNavigation();
        handleInitialIntent();
    }

    private void initViews() {
        tvPaperTitle = findViewById(R.id.tvChapterPaperTitle);
        etSearchChapters = findViewById(R.id.etSearchChapters);

        // Botany Accordion Views
        cardBotanyAccordion = findViewById(R.id.cardBotanyAccordion);
        btnToggleBotany = findViewById(R.id.btnToggleBotany);
        ivBotanyArrow = findViewById(R.id.ivBotanyArrow);
        layoutBotanyExpanded = findViewById(R.id.layoutBotanyExpanded);
        llBotanyChapters = findViewById(R.id.llBotanyChapters);
        tvNoBotanyMatches = findViewById(R.id.tvNoBotanyMatches);

        // Zoology Accordion Views
        cardZoologyAccordion = findViewById(R.id.cardZoologyAccordion);
        btnToggleZoology = findViewById(R.id.btnToggleZoology);
        ivZoologyArrow = findViewById(R.id.ivZoologyArrow);
        layoutZoologyExpanded = findViewById(R.id.layoutZoologyExpanded);
        llZoologyChapters = findViewById(R.id.llZoologyChapters);
        tvNoZoologyMatches = findViewById(R.id.tvNoZoologyMatches);

        // Configure header title based on mode
        if (MODE_FLASHCARDS.equals(mode)) {
            if (tvPaperTitle != null) {
                tvPaperTitle.setText("Flashcards");
            }
            if (etSearchChapters != null) {
                etSearchChapters.setHint("Search flashcard chapters...");
            }
        } else {
            if (tvPaperTitle != null) {
                tvPaperTitle.setText("Exam Practice");
            }
            if (etSearchChapters != null) {
                etSearchChapters.setHint("Search practice chapters...");
            }
        }

        // Back button
        View btnBack = findViewById(R.id.btnChapterBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Bookmark / Saved button
        View btnBookmark = findViewById(R.id.btnChapterBookmark);
        if (btnBookmark != null) {
            btnBookmark.setOnClickListener(v ->
                    startActivity(new Intent(this, SavedItemsActivity.class)));
        }

        // Search icon focus
        View btnSearch = findViewById(R.id.btnChapterSearch);
        if (btnSearch != null) {
            btnSearch.setOnClickListener(v -> {
                if (etSearchChapters != null) {
                    etSearchChapters.requestFocus();
                }
            });
        }
    }

    private void loadChaptersData() {
        botanyChapters = SampleBiologyData.getChapters(SampleBiologyData.PAPER_BIOLOGY_1);
        if (botanyChapters == null) botanyChapters = new ArrayList<>();

        zoologyChapters = SampleBiologyData.getChapters(SampleBiologyData.PAPER_BIOLOGY_2);
        if (zoologyChapters == null) zoologyChapters = new ArrayList<>();
    }

    private void renderChapterLists() {
        LayoutInflater inflater = LayoutInflater.from(this);
        boolean isFlashcard = MODE_FLASHCARDS.equals(mode);

        // 1. Render Botany Chapters
        llBotanyChapters.removeAllViews();
        botanyItemViews.clear();
        for (Chapter chapter : botanyChapters) {
            View itemView = inflater.inflate(R.layout.item_chapter, llBotanyChapters, false);
            bindChapterItemView(itemView, chapter, SampleBiologyData.PAPER_BIOLOGY_1, isFlashcard);
            llBotanyChapters.addView(itemView);
            botanyItemViews.add(itemView);
        }

        // 2. Render Zoology Chapters
        llZoologyChapters.removeAllViews();
        zoologyItemViews.clear();
        for (Chapter chapter : zoologyChapters) {
            View itemView = inflater.inflate(R.layout.item_chapter, llZoologyChapters, false);
            bindChapterItemView(itemView, chapter, SampleBiologyData.PAPER_BIOLOGY_2, isFlashcard);
            llZoologyChapters.addView(itemView);
            zoologyItemViews.add(itemView);
        }
    }

    private void bindChapterItemView(View itemView, Chapter chapter, String targetPaper, boolean isFlashcard) {
        TextView tvNumber = itemView.findViewById(R.id.tvChapterNumber);
        TextView tvTitle = itemView.findViewById(R.id.tvChapterTitle);
        TextView tvSubtitle = itemView.findViewById(R.id.tvChapterSubtitle);
        TextView btnAction = itemView.findViewById(R.id.btnChapterPractice);

        if (tvNumber != null) {
            tvNumber.setText(String.format(Locale.getDefault(), "%02d", chapter.getChapterNumber()));
        }

        if (tvTitle != null) {
            tvTitle.setText(chapter.getTitle());
        }

        if (tvSubtitle != null) {
            if (isFlashcard) {
                tvSubtitle.setText("⚡ 25+ Flashcards • Active Recall");
            } else {
                tvSubtitle.setText("🎯 50+ MCQs • 10 Mins Mock");
            }
        }

        if (btnAction != null) {
            if (isFlashcard) {
                btnAction.setText("Study");
            } else {
                btnAction.setText("Practice");
            }
        }

        View.OnClickListener launchChapterListener = v -> {
            Class<?> targetActivity = isFlashcard ? FlashcardsActivity.class : QuizActivity.class;
            Intent intent = new Intent(ChapterActivity.this, targetActivity);
            intent.putExtra(EXTRA_PAPER_ID, targetPaper);
            intent.putExtra(EXTRA_CHAPTER_ID, chapter.getId());
            intent.putExtra(EXTRA_CHAPTER_TITLE, chapter.getTitle());
            startActivity(intent);
        };

        itemView.setOnClickListener(launchChapterListener);
        if (btnAction != null) {
            btnAction.setOnClickListener(launchChapterListener);
        }

        // Store chapter reference in tag for search filtering
        itemView.setTag(chapter);
    }

    private void setupAccordionToggles() {
        // Botany Accordion minimize/maximize toggle
        View.OnClickListener toggleBotanyListener = v -> {
            isBotanyExpanded = !isBotanyExpanded;
            updateBotanyAccordionState();
        };
        if (cardBotanyAccordion != null) {
            cardBotanyAccordion.setOnClickListener(toggleBotanyListener);
        }
        if (btnToggleBotany != null) {
            btnToggleBotany.setOnClickListener(toggleBotanyListener);
        }

        // Zoology Accordion minimize/maximize toggle
        View.OnClickListener toggleZoologyListener = v -> {
            isZoologyExpanded = !isZoologyExpanded;
            updateZoologyAccordionState();
        };
        if (cardZoologyAccordion != null) {
            cardZoologyAccordion.setOnClickListener(toggleZoologyListener);
        }
        if (btnToggleZoology != null) {
            btnToggleZoology.setOnClickListener(toggleZoologyListener);
        }

        // Set initial states
        updateBotanyAccordionState();
        updateZoologyAccordionState();
    }

    private void updateBotanyAccordionState() {
        if (layoutBotanyExpanded != null) {
            layoutBotanyExpanded.setVisibility(isBotanyExpanded ? View.VISIBLE : View.GONE);
        }
        if (ivBotanyArrow != null) {
            ivBotanyArrow.setImageResource(isBotanyExpanded ? R.drawable.ic_chevron_up : R.drawable.ic_chevron_down);
            ivBotanyArrow.setContentDescription(isBotanyExpanded ? "Minimize Botany Chapters" : "Maximize Botany Chapters");
        }
    }

    private void updateZoologyAccordionState() {
        if (layoutZoologyExpanded != null) {
            layoutZoologyExpanded.setVisibility(isZoologyExpanded ? View.VISIBLE : View.GONE);
        }
        if (ivZoologyArrow != null) {
            ivZoologyArrow.setImageResource(isZoologyExpanded ? R.drawable.ic_chevron_up : R.drawable.ic_chevron_down);
            ivZoologyArrow.setContentDescription(isZoologyExpanded ? "Minimize Zoology Chapters" : "Maximize Zoology Chapters");
        }
    }

    private void setupSearchAndFilter() {
        if (etSearchChapters != null) {
            etSearchChapters.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    performSearch(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        View btnFilter = findViewById(R.id.btnChapterFilter);
        if (btnFilter != null) {
            btnFilter.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(this, v);
                popup.getMenu().add("Show Both Papers");
                popup.getMenu().add("Botany Only (Paper 1)");
                popup.getMenu().add("Zoology Only (Paper 2)");
                popup.setOnMenuItemClickListener(item -> {
                    String title = item.getTitle().toString();
                    if ("Botany Only (Paper 1)".equals(title)) {
                        isBotanyExpanded = true;
                        isZoologyExpanded = false;
                    } else if ("Zoology Only (Paper 2)".equals(title)) {
                        isBotanyExpanded = false;
                        isZoologyExpanded = true;
                    } else {
                        isBotanyExpanded = true;
                        isZoologyExpanded = true;
                    }
                    updateBotanyAccordionState();
                    updateZoologyAccordionState();
                    Toast.makeText(this, "Filter: " + title, Toast.LENGTH_SHORT).show();
                    return true;
                });
                popup.show();
            });
        }
    }

    private void performSearch(String query) {
        String cleanQuery = (query != null) ? query.trim().toLowerCase(Locale.getDefault()) : "";

        int visibleBotany = 0;
        for (View view : botanyItemViews) {
            Chapter chapter = (Chapter) view.getTag();
            if (chapter != null && matchesQuery(chapter, cleanQuery)) {
                view.setVisibility(View.VISIBLE);
                visibleBotany++;
            } else {
                view.setVisibility(View.GONE);
            }
        }
        if (tvNoBotanyMatches != null) {
            tvNoBotanyMatches.setVisibility(visibleBotany == 0 && !cleanQuery.isEmpty() ? View.VISIBLE : View.GONE);
        }

        int visibleZoology = 0;
        for (View view : zoologyItemViews) {
            Chapter chapter = (Chapter) view.getTag();
            if (chapter != null && matchesQuery(chapter, cleanQuery)) {
                view.setVisibility(View.VISIBLE);
                visibleZoology++;
            } else {
                view.setVisibility(View.GONE);
            }
        }
        if (tvNoZoologyMatches != null) {
            tvNoZoologyMatches.setVisibility(visibleZoology == 0 && !cleanQuery.isEmpty() ? View.VISIBLE : View.GONE);
        }

        // If actively searching and matches are found, ensure sections are expanded
        if (!cleanQuery.isEmpty()) {
            if (visibleBotany > 0 && !isBotanyExpanded) {
                isBotanyExpanded = true;
                updateBotanyAccordionState();
            }
            if (visibleZoology > 0 && !isZoologyExpanded) {
                isZoologyExpanded = true;
                updateZoologyAccordionState();
            }
        }
    }

    private boolean matchesQuery(Chapter chapter, String query) {
        if (query.isEmpty()) return true;
        if (chapter.getTitle().toLowerCase(Locale.getDefault()).contains(query)) return true;
        String chapterNumStr = String.valueOf(chapter.getChapterNumber());
        if (chapterNumStr.equals(query) || ("chapter " + chapterNumStr).contains(query)) return true;
        return false;
    }

    private void handleInitialIntent() {
        String initialChapterId = getIntent().getStringExtra(EXTRA_CHAPTER_ID);
        if (initialChapterId != null && !initialChapterId.isEmpty()) {
            if (initialChapterId.startsWith("zoo") || initialChapterId.endsWith("_p2")
                    || SampleBiologyData.PAPER_BIOLOGY_2.equals(paperId)) {
                isZoologyExpanded = true;
                updateZoologyAccordionState();
            } else {
                isBotanyExpanded = true;
                updateBotanyAccordionState();
            }
        }
    }

    private void setupNavigation() {
        View navHome = findViewById(R.id.navTabHome);
        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            });
        }

        View navStudy = findViewById(R.id.navTabStudy);
        if (navStudy != null) {
            navStudy.setOnClickListener(v ->
                    Toast.makeText(this, "Already in Chapter Selection", Toast.LENGTH_SHORT).show());
        }

        View navSaved = findViewById(R.id.navTabSaved);
        if (navSaved != null) {
            navSaved.setOnClickListener(v ->
                    startActivity(new Intent(this, SavedItemsActivity.class)));
        }

        View navProfile = findViewById(R.id.navTabProfile);
        if (navProfile != null) {
            navProfile.setOnClickListener(v ->
                    startActivity(new Intent(this, ProfileActivity.class)));
        }
    }
}
