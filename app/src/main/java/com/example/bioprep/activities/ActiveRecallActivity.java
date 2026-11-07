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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bioprep.R;
import com.example.bioprep.data.SampleBiologyData;
import com.example.bioprep.data.SampleBiologyData.ActiveRecallItem;

import java.util.List;

/**
 * Active Recall Activity conforming to Stitch UI Design (Screen 8e0b6be08b3540ec9378b2c2e92d7abc)
 * and demonstrating Lab 08 Topic 5 (UI Layouts & dynamic code-level View addition).
 */
public class ActiveRecallActivity extends AppCompatActivity {

    private static final String TAG = "ActiveRecallActivity";

    private LinearLayout layoutAnswerContainer;
    private Button btnToggleAnswer;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_recall);

        recallItems = SampleBiologyData.getActiveRecallItems();

        layoutAnswerContainer = findViewById(R.id.layoutAnswerContainer);
        btnToggleAnswer = findViewById(R.id.btnToggleAnswer);
        pbRecallProgress = findViewById(R.id.pbRecallProgress);
        tvQuestionProgress = findViewById(R.id.tvQuestionProgress);
        tvChapterTag = findViewById(R.id.tvChapterTag);
        tvRecallPrompt = findViewById(R.id.tvRecallPrompt);
        tvRecallPromptBn = findViewById(R.id.tvRecallPromptBn);
        tvModelAnswer = findViewById(R.id.tvModelAnswer);
        tvRecallKeywords = findViewById(R.id.tvRecallKeywords);
        etActiveRecallText = findViewById(R.id.etActiveRecallText);

        ImageButton btnRecallBack = findViewById(R.id.btnRecallBack);
        if (btnRecallBack != null) {
            btnRecallBack.setOnClickListener(v -> finish());
        }

        View btnRecallOptions = findViewById(R.id.btnRecallOptions);
        if (btnRecallOptions != null) {
            btnRecallOptions.setOnClickListener(v -> {
                androidx.appcompat.widget.PopupMenu popup = new androidx.appcompat.widget.PopupMenu(ActiveRecallActivity.this, v);
                popup.getMenu().add("Bookmark this Drill");
                popup.getMenu().add("Reset All Drills");
                popup.getMenu().add("Switch to Botany");
                popup.setOnMenuItemClickListener(item -> {
                    Toast.makeText(ActiveRecallActivity.this, item.getTitle() + " selected", Toast.LENGTH_SHORT).show();
                    if ("Reset All Drills".equals(item.getTitle())) {
                        currentQuestionIndex = 0;
                        updateQuestionDisplay();
                    }
                    return true;
                });
                popup.show();
            });
        }

        if (btnToggleAnswer != null) {
            btnToggleAnswer.setOnClickListener(v -> toggleAnswer());
        }

        Button btnShowAnswerBottom = findViewById(R.id.btnShowAnswerBottom);
        if (btnShowAnswerBottom != null) {
            btnShowAnswerBottom.setOnClickListener(v -> toggleAnswer());
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
            btnRecallNext.setOnClickListener(v -> {
                if (recallItems != null && currentQuestionIndex < recallItems.size() - 1) {
                    currentQuestionIndex++;
                    updateQuestionDisplay();
                } else {
                    Toast.makeText(this, "🎉 Awesome! You completed all active recall drills for today!", Toast.LENGTH_SHORT).show();
                }
            });
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
            btnRecallAgain.setOnClickListener(v -> {
                Toast.makeText(this, "Marked for review in < 1 min", Toast.LENGTH_SHORT).show();
                advanceToNextCard();
            });
        }
        Button btnRecallGood = findViewById(R.id.btnRecallGood);
        if (btnRecallGood != null) {
            btnRecallGood.setOnClickListener(v -> {
                Toast.makeText(this, "✓ Good recall! Scheduled for tomorrow", Toast.LENGTH_SHORT).show();
                advanceToNextCard();
            });
        }
        Button btnRecallEasy = findViewById(R.id.btnRecallEasy);
        if (btnRecallEasy != null) {
            btnRecallEasy.setOnClickListener(v -> {
                Toast.makeText(this, "⭐ Mastered! Scheduled in 4 days", Toast.LENGTH_SHORT).show();
                advanceToNextCard();
            });
        }

        // Initialize question display
        updateQuestionDisplay();

        // =========================================================================
        // Lab 08 Topic 5: Dynamic View Addition Demonstration
        // Demonstrates: new EditText(this), new Button(this), addView(), setOnClickListener
        // =========================================================================
        setupDynamicViewsDemo();
    }

    private void advanceToNextCard() {
        if (recallItems != null && currentQuestionIndex < recallItems.size() - 1) {
            currentQuestionIndex++;
            updateQuestionDisplay();
        } else {
            Toast.makeText(this, "Session Complete! Well done!", Toast.LENGTH_SHORT).show();
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
            tvChapterTag.setText(item.chapterName);
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
    }

    /**
     * Programmatic dynamic creation of views as outlined in Topic 5:
     * findViewById<LinearLayout>(R.id.main), new EditText(this), new Button(this),
     * addView(editText), addView(showButton), setOnClickListener, Toast.makeText.
     */
    private void setupDynamicViewsDemo() {
        LinearLayout dynamicContainer = findViewById(R.id.main);
        if (dynamicContainer == null) return;

        // 1. new EditText(this)
        EditText dynamicEditText = new EditText(this);
        dynamicEditText.setHint("Type custom bio keyword (dynamic EditText)...");
        dynamicEditText.setTextColor(getColor(R.color.bio_on_surface));
        dynamicEditText.setHintTextColor(getColor(R.color.bio_outline));
        dynamicEditText.setBackgroundResource(R.drawable.bg_card_glow);
        dynamicEditText.setPadding(24, 20, 24, 20);

        // 2. new Button(this)
        Button dynamicButton = new Button(this);
        dynamicButton.setText("Submit Dynamic Keyword");
        dynamicButton.setBackgroundTintList(getColorStateList(R.color.bio_primary_container));
        dynamicButton.setTextColor(getColor(R.color.bio_on_primary_container));

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        btnParams.setMargins(0, 16, 0, 0);
        dynamicButton.setLayoutParams(btnParams);

        // 3. setOnClickListener { ... }
        dynamicButton.setOnClickListener(v -> {
            String userInput = dynamicEditText.getText().toString();
            if (userInput.trim().isEmpty()) {
                Toast.makeText(ActiveRecallActivity.this, "Please enter a keyword first!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ActiveRecallActivity.this, "Added to Recall Queue: " + userInput, Toast.LENGTH_LONG).show();
                dynamicEditText.setText("");
            }
        });

        // 4. addView(editText), addView(showButton)
        dynamicContainer.addView(dynamicEditText);
        dynamicContainer.addView(dynamicButton);
    }
}

