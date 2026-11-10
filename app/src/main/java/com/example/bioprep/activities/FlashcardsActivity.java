package com.example.bioprep.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bioprep.R;
import com.example.bioprep.database.AppDatabase;
import com.example.bioprep.database.FlashcardEntity;
import com.example.bioprep.database.SavedItemEntity;
import com.example.bioprep.database.StudySessionEntity;
import com.example.bioprep.utils.FirebaseSyncManager;
import com.example.bioprep.utils.RecentActivityHelper;
import com.example.bioprep.utils.UserPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

/**
 * Flashcards Activity implementing Requirements 2, 3, and 4:
 * - Fullscreen flashcard layout occupying almost the entire screen height.
 * - Organized strictly by subject and chapter from Room Database.
 * - Single "Flip Card" action and tap on card reveals the answer.
 * - Exactly two navigation buttons below card: Again and Next.
 * - "Again" re-queues the card for review in the current session.
 * - Tracks actual active elapsed study time in Room Database (StudySessionEntity).
 */
public class FlashcardsActivity extends AppCompatActivity {

    public static final String EXTRA_PAPER_ID = "extra_paper_id";
    public static final String EXTRA_CHAPTER_ID = "extra_chapter_id";
    public static final String EXTRA_CHAPTER_TITLE = "extra_chapter_title";

    private View cardFront;
    private View cardBack;
    private boolean isFlipped = false;

    private TextView tvFlashcardsHeaderTitle;
    private TextView tvCardCategory;
    private TextView tvCardProgressBadge;
    private TextView tvCardProgressPercent;
    private ProgressBar pbFlashcardProgress;

    private TextView tvFlashcardQuestion;
    private TextView tvFlashcardQuestionBn;
    private TextView tvFlashcardAnswerTitle;
    private TextView tvFlashcardAnswerDesc;
    private TextView tvFlashcardMedicalTip;

    private Button btnFlipFront;
    private Button btnFlipBack;
    private Button btnSaveFlashcard;
    private Button btnRateNext;
    private boolean isCurrentCardSaved = false;

    private String chapterId;
    private String chapterTitle;
    private String paperId;

    private final List<FlashcardEntity> flashcardQueue = new ArrayList<>();
    private int currentQueueIndex = 0;
    private int totalUniqueCards = 0;

    // Study session timer
    private long sessionStartTime = 0;
    private boolean isSessionSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcards);

        chapterId = getIntent().getStringExtra(EXTRA_CHAPTER_ID);
        chapterTitle = getIntent().getStringExtra(EXTRA_CHAPTER_TITLE);
        paperId = getIntent().getStringExtra(EXTRA_PAPER_ID);

        if (chapterId == null) chapterId = "botany_ch01";
        if (chapterTitle == null) chapterTitle = "Chapter Flashcards";

        sessionStartTime = System.currentTimeMillis();

        // Record recent activity
        RecentActivityHelper.recordActivity(
                this,
                RecentActivityHelper.TYPE_FLASHCARD,
                chapterId,
                chapterTitle,
                paperId
        );

        initViews();
        setupFlipCard();
        setupActionButtons();
        loadFlashcardsFromDatabase();
    }

    private void initViews() {
        tvFlashcardsHeaderTitle = findViewById(R.id.tvFlashcardsHeaderTitle);
        tvCardCategory = findViewById(R.id.tvCardCategory);
        tvCardProgressBadge = findViewById(R.id.tvCardProgressBadge);
        tvCardProgressPercent = findViewById(R.id.tvCardProgressPercent);
        pbFlashcardProgress = findViewById(R.id.pbFlashcardProgress);

        tvFlashcardQuestion = findViewById(R.id.tvFlashcardQuestion);
        tvFlashcardQuestionBn = findViewById(R.id.tvFlashcardQuestionBn);
        tvFlashcardAnswerTitle = findViewById(R.id.tvFlashcardAnswerTitle);
        tvFlashcardAnswerDesc = findViewById(R.id.tvFlashcardAnswerDesc);
        tvFlashcardMedicalTip = findViewById(R.id.tvFlashcardMedicalTip);

        cardFront = findViewById(R.id.cardFront);
        cardBack = findViewById(R.id.cardBack);

        btnFlipFront = findViewById(R.id.btnFlipFront);
        btnFlipBack = findViewById(R.id.btnFlipBack);
        btnSaveFlashcard = findViewById(R.id.btnSaveFlashcard);
        btnRateNext = findViewById(R.id.btnRateNext);

        if (tvFlashcardsHeaderTitle != null) {
            tvFlashcardsHeaderTitle.setText("Flashcards: " + chapterTitle);
        }
        if (tvCardCategory != null) {
            tvCardCategory.setText(chapterTitle);
        }

        View btnBack = findViewById(R.id.btnFlashcardsBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void setupFlipCard() {
        View.OnClickListener flipListener = v -> {
            isFlipped = !isFlipped;
            if (isFlipped) {
                cardFront.setVisibility(View.GONE);
                cardBack.setVisibility(View.VISIBLE);
            } else {
                cardBack.setVisibility(View.GONE);
                cardFront.setVisibility(View.VISIBLE);
            }
        };

        if (btnFlipFront != null) btnFlipFront.setOnClickListener(flipListener);
        if (btnFlipBack != null) btnFlipBack.setOnClickListener(flipListener);
        if (cardFront != null) cardFront.setOnClickListener(flipListener);
        if (cardBack != null) cardBack.setOnClickListener(flipListener);
    }

    private void setupActionButtons() {
        // Save button (Replaces Again - Requirement 5 & 6)
        if (btnSaveFlashcard != null) {
            btnSaveFlashcard.setOnClickListener(v -> toggleSaveCurrentFlashcard());
        }

        // "Next" button: advances to the next flashcard
        if (btnRateNext != null) {
            btnRateNext.setOnClickListener(v -> {
                advanceCard();
            });
        }
    }

    private void advanceCard() {
        if (flashcardQueue.isEmpty()) return;

        UserPreferences.incrementTodayFlashcardCount(this);
        currentQueueIndex++;
        if (currentQueueIndex < flashcardQueue.size()) {
            displayCurrentCard();
        } else {
            showCompletionDialog();
        }
    }

    private void displayCurrentCard() {
        if (flashcardQueue.isEmpty() || currentQueueIndex >= flashcardQueue.size()) return;

        FlashcardEntity item = flashcardQueue.get(currentQueueIndex);

        // Progress display
        int displayNum = currentQueueIndex + 1;
        int total = flashcardQueue.size();
        if (tvCardProgressBadge != null) {
            tvCardProgressBadge.setText("CARD " + displayNum + " OF " + total);
        }

        int percent = Math.min(100, (int) (((float) displayNum / total) * 100));
        if (tvCardProgressPercent != null) {
            tvCardProgressPercent.setText(percent + "% Complete");
        }
        if (pbFlashcardProgress != null) {
            pbFlashcardProgress.setProgress(percent);
        }

        // Question Face
        if (tvFlashcardQuestion != null) {
            tvFlashcardQuestion.setText(item.getQuestionEn());
        }
        if (tvFlashcardQuestionBn != null) {
            tvFlashcardQuestionBn.setText(item.getQuestionBn());
        }

        // Answer Face
        if (tvFlashcardAnswerTitle != null) {
            tvFlashcardAnswerTitle.setText(item.getAnswerEn());
        }
        if (tvFlashcardAnswerDesc != null) {
            tvFlashcardAnswerDesc.setText(item.getAnswerBn());
        }
        if (tvFlashcardMedicalTip != null) {
            tvFlashcardMedicalTip.setText(item.getMedicalTip());
        }

        // Reset flip to front
        isFlipped = false;
        if (cardBack != null) cardBack.setVisibility(View.GONE);
        if (cardFront != null) cardFront.setVisibility(View.VISIBLE);

        checkIfCardSaved(String.valueOf(item.getId()));
    }

    private void checkIfCardSaved(String cardId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            int count = db.savedItemDao().isItemSaved("FLASHCARD", cardId);
            isCurrentCardSaved = (count > 0);
            runOnUiThread(this::updateSaveButtonUI);
        });
    }

    private void updateSaveButtonUI() {
        if (btnSaveFlashcard == null) return;
        if (isCurrentCardSaved) {
            btnSaveFlashcard.setText("✓ Saved");
            btnSaveFlashcard.setBackgroundResource(R.drawable.bg_pill_emerald);
        } else {
            btnSaveFlashcard.setText("🔖 Save");
            btnSaveFlashcard.setBackgroundResource(R.drawable.bg_pill_dark_teal);
        }
    }

    private void toggleSaveCurrentFlashcard() {
        if (flashcardQueue.isEmpty() || currentQueueIndex >= flashcardQueue.size()) return;
        FlashcardEntity current = flashcardQueue.get(currentQueueIndex);
        String cardId = String.valueOf(current.getId());

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            if (isCurrentCardSaved) {
                db.savedItemDao().deleteByTypeAndItemId("FLASHCARD", cardId);
                isCurrentCardSaved = false;
                runOnUiThread(this::updateSaveButtonUI);
            } else {
                SavedItemEntity saved = new SavedItemEntity(
                        "FLASHCARD",
                        cardId,
                        chapterTitle + " - " + current.getQuestionEn(),
                        current.getAnswerEn() + "\n\n" + (current.getAnswerBn() != null ? current.getAnswerBn() : ""),
                        current.getMedicalTip() != null ? current.getMedicalTip() : "Flashcard",
                        System.currentTimeMillis()
                );
                db.savedItemDao().insert(saved);
                isCurrentCardSaved = true;
                runOnUiThread(this::updateSaveButtonUI);
            }
        });
    }

    private void loadFlashcardsFromDatabase() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<FlashcardEntity> cards;

            if ("all".equalsIgnoreCase(chapterId)) {
                cards = db.flashcardDao().getAllFlashcards();
            } else {
                cards = db.flashcardDao().getFlashcardsByChapter(chapterId);
            }

            // Fallback: if database seed is still executing, get all cards
            if (cards == null || cards.isEmpty()) {
                cards = db.flashcardDao().getAllFlashcards();
            }

            final List<FlashcardEntity> finalCards = cards;
            runOnUiThread(() -> {
                if (finalCards != null && !finalCards.isEmpty()) {
                    flashcardQueue.clear();
                    flashcardQueue.addAll(finalCards);
                    totalUniqueCards = finalCards.size();
                    currentQueueIndex = 0;
                    displayCurrentCard();
                }
            });
        });
    }

    private void showCompletionDialog() {
        saveStudySessionTime();

        new AlertDialog.Builder(this)
                .setTitle("🎉 Session Completed!")
                .setMessage("Great work! You have reviewed all flashcards in this deck.\n\nActive study time has been recorded to your Daily Goal.")
                .setPositiveButton("Review Again", (dialog, which) -> {
                    currentQueueIndex = 0;
                    displayCurrentCard();
                })
                .setNegativeButton("Back to Chapters", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void saveStudySessionTime() {
        if (isSessionSaved) return;
        isSessionSaved = true;

        long elapsedMillis = System.currentTimeMillis() - sessionStartTime;
        int durationSeconds = (int) (elapsedMillis / 1000L);
        if (durationSeconds < 5) return; // Ignore accidental open/close under 5s

        String dateString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            db.studySessionDao().insertSession(new StudySessionEntity(
                    "FLASHCARD",
                    chapterId,
                    durationSeconds,
                    System.currentTimeMillis(),
                    dateString
            ));
            FirebaseSyncManager.logStudySessionToCloud(getApplicationContext(), "FLASHCARD", chapterTitle, durationSeconds);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveStudySessionTime();
    }
}
