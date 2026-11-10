package com.example.bioprep.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bioprep.R;
import com.example.bioprep.data.ActiveRecallDataProvider;
import com.example.bioprep.data.SampleBiologyData.ActiveRecallItem;
import com.example.bioprep.database.AppDatabase;
import com.example.bioprep.database.SavedItemEntity;
import com.example.bioprep.utils.KeyboardUtils;
import com.example.bioprep.utils.RecentActivityHelper;
import com.example.bioprep.utils.UserPreferences;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * Active Recall Activity conforming to Stitch UI Design
 * Supporting 10 high-yield questions for each of the 24 Botany & Zoology chapters,
 * answer reveal, save question into Saved Items, and recent activity logging.
 */
public class ActiveRecallActivity extends AppCompatActivity {

    private static final String TAG = "ActiveRecallActivity";

    private LinearLayout layoutAnswerContainer;
    private Button btnToggleAnswer;
    private Button btnSaveRecall;
    private ProgressBar pbRecallProgress;
    private TextView tvQuestionProgress;
    private TextView tvChapterTag;
    private TextView tvRecallPrompt;
    private TextView tvRecallPromptBn;
    private TextView tvModelAnswer;
    private TextView tvRecallKeywords;
    private EditText etActiveRecallText;

    private boolean isAnswerRevealed = false;
    private int currentQuestionIndex = 0;
    private List<ActiveRecallItem> recallItems;

    private String chapterId;
    private String chapterTitle;
    private String paperId;

    private AppDatabase db;
    private boolean isCurrentQuestionSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_recall);

        db = AppDatabase.getInstance(this);

        // Retrieve Chapter info from Intent
        chapterId = getIntent().getStringExtra(ChapterActivity.EXTRA_CHAPTER_ID);
        chapterTitle = getIntent().getStringExtra(ChapterActivity.EXTRA_CHAPTER_TITLE);
        paperId = getIntent().getStringExtra(ChapterActivity.EXTRA_PAPER_ID);

        if (chapterId == null || chapterId.trim().isEmpty()) {
            chapterId = "botany_ch01";
        }
        if (chapterTitle == null || chapterTitle.trim().isEmpty()) {
            chapterTitle = "Chapter 1: Cell and Its Structure";
        }
        if (paperId == null || paperId.trim().isEmpty()) {
            paperId = "PAPER_1";
        }

        // Record recent activity
        RecentActivityHelper.recordActivity(
                this,
                RecentActivityHelper.TYPE_ACTIVE_RECALL,
                chapterId,
                chapterTitle,
                paperId
        );

        // Load 10 chapter-specific questions
        recallItems = ActiveRecallDataProvider.getQuestionsForChapter(chapterId, chapterTitle);

        layoutAnswerContainer = findViewById(R.id.layoutAnswerContainer);
        btnToggleAnswer = findViewById(R.id.btnToggleAnswer);
        btnSaveRecall = findViewById(R.id.btnSaveRecall);
        pbRecallProgress = findViewById(R.id.pbRecallProgress);
        tvQuestionProgress = findViewById(R.id.tvQuestionProgress);
        tvChapterTag = findViewById(R.id.tvChapterTag);
        tvRecallPrompt = findViewById(R.id.tvRecallPrompt);
        tvRecallPromptBn = findViewById(R.id.tvRecallPromptBn);
        tvModelAnswer = findViewById(R.id.tvModelAnswer);
        tvRecallKeywords = findViewById(R.id.tvRecallKeywords);
        etActiveRecallText = findViewById(R.id.etActiveRecallText);

        // Auto-scroll above keyboard and hide bottom nav when keyboard appears
        KeyboardUtils.autoScrollOnFocus(findViewById(R.id.scrollActiveRecall), etActiveRecallText);
        KeyboardUtils.attachKeyboardListener(this, findViewById(R.id.layoutActiveRecallRoot), isVisible -> {
            View nav = findViewById(R.id.bottomNavBar);
            if (nav != null) {
                nav.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            }
        });

        ImageButton btnRecallBack = findViewById(R.id.btnRecallBack);
        if (btnRecallBack != null) {
            btnRecallBack.setOnClickListener(v -> finish());
        }

        if (btnToggleAnswer != null) {
            btnToggleAnswer.setOnClickListener(v -> toggleAnswer());
        }

        // Bottom Save button (Requirement 9)
        if (btnSaveRecall != null) {
            btnSaveRecall.setOnClickListener(v -> toggleSaveCurrentQuestion());
        }

        // Bottom Navigation Bar items
        findViewById(R.id.navTabHome).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
        findViewById(R.id.navTabStudy).setOnClickListener(v -> {
            startActivity(new Intent(this, ChapterActivity.class));
            finish();
        });
        findViewById(R.id.navTabSaved).setOnClickListener(v -> {
            startActivity(new Intent(this, SavedItemsActivity.class));
            finish();
        });
        findViewById(R.id.navTabProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        });

        Button btnRecallNext = findViewById(R.id.btnRecallNext);
        if (btnRecallNext != null) {
            btnRecallNext.setOnClickListener(v -> advanceToNextCard());
        }

        Button btnRecallPrev = findViewById(R.id.btnRecallPrev);
        if (btnRecallPrev != null) {
            btnRecallPrev.setOnClickListener(v -> {
                if (currentQuestionIndex > 0) {
                    currentQuestionIndex--;
                    updateQuestionDisplay();
                }
            });
        }

        // Self assessment feedback buttons
        Button btnRecallAgain = findViewById(R.id.btnRecallAgain);
        if (btnRecallAgain != null) {
            btnRecallAgain.setOnClickListener(v -> advanceToNextCard());
        }
        Button btnRecallGood = findViewById(R.id.btnRecallGood);
        if (btnRecallGood != null) {
            btnRecallGood.setOnClickListener(v -> advanceToNextCard());
        }
        Button btnRecallEasy = findViewById(R.id.btnRecallEasy);
        if (btnRecallEasy != null) {
            btnRecallEasy.setOnClickListener(v -> advanceToNextCard());
        }

        // Initialize question display
        updateQuestionDisplay();
    }

    private void advanceToNextCard() {
        UserPreferences.incrementTodayActiveRecallCount(this);
        if (recallItems != null && currentQuestionIndex < recallItems.size() - 1) {
            currentQuestionIndex++;
            updateQuestionDisplay();
        }
    }

    private void toggleAnswer() {
        isAnswerRevealed = !isAnswerRevealed;
        if (isAnswerRevealed) {
            layoutAnswerContainer.setVisibility(View.VISIBLE);
            btnToggleAnswer.setText("Hide Model Answer");
        } else {
            layoutAnswerContainer.setVisibility(View.GONE);
            btnToggleAnswer.setText("Reveal Answer");
        }
    }

    private void updateQuestionDisplay() {
        if (recallItems == null || recallItems.isEmpty()) return;
        ActiveRecallItem item = recallItems.get(currentQuestionIndex);

        pbRecallProgress.setMax(recallItems.size());
        pbRecallProgress.setProgress(currentQuestionIndex + 1);
        tvQuestionProgress.setText("Question " + (currentQuestionIndex + 1) + " of " + recallItems.size());

        if (tvChapterTag != null) {
            tvChapterTag.setText(chapterTitle);
        }
        if (tvRecallPrompt != null) {
            tvRecallPrompt.setText(item.promptEn);
        }
        if (tvRecallPromptBn != null) {
            tvRecallPromptBn.setText(item.promptBn);
        }
        if (tvModelAnswer != null) {
            tvModelAnswer.setText(item.answerEn + "\n\n" + item.answerBn);
        }
        if (tvRecallKeywords != null) {
            tvRecallKeywords.setText("Key Terms: " + item.keywords);
        }

        isAnswerRevealed = false;
        layoutAnswerContainer.setVisibility(View.GONE);
        btnToggleAnswer.setText("Reveal Answer");
        if (etActiveRecallText != null) {
            etActiveRecallText.setText("");
        }

        checkIfCurrentQuestionSaved(item.id);
    }

    private void checkIfCurrentQuestionSaved(String questionId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            int count = db.savedItemDao().isItemSaved("ACTIVE_RECALL", questionId);
            isCurrentQuestionSaved = (count > 0);
            runOnUiThread(() -> updateSaveButtonUI());
        });
    }

    private void updateSaveButtonUI() {
        if (btnSaveRecall == null) return;
        if (isCurrentQuestionSaved) {
            btnSaveRecall.setText("✓ Saved");
            btnSaveRecall.setBackgroundResource(R.drawable.bg_pill_emerald);
        } else {
            btnSaveRecall.setText("🔖 Save");
            btnSaveRecall.setBackgroundResource(R.drawable.bg_pill_dark_teal);
        }
    }

    private void toggleSaveCurrentQuestion() {
        if (recallItems == null || currentQuestionIndex >= recallItems.size()) return;
        ActiveRecallItem item = recallItems.get(currentQuestionIndex);

        Executors.newSingleThreadExecutor().execute(() -> {
            if (isCurrentQuestionSaved) {
                db.savedItemDao().deleteByTypeAndItemId("ACTIVE_RECALL", item.id);
                isCurrentQuestionSaved = false;
                runOnUiThread(this::updateSaveButtonUI);
            } else {
                SavedItemEntity savedItem = new SavedItemEntity(
                        "ACTIVE_RECALL",
                        item.id,
                        chapterTitle + " - Q" + (currentQuestionIndex + 1),
                        item.promptEn + "\n\n" + item.promptBn + "\n\nAnswer: " + item.answerEn,
                        item.keywords,
                        System.currentTimeMillis()
                );
                db.savedItemDao().insert(savedItem);
                isCurrentQuestionSaved = true;
                runOnUiThread(this::updateSaveButtonUI);
            }
        });
    }
}
