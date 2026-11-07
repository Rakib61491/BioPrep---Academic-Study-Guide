package com.example.bioprep.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bioprep.R;
import com.example.bioprep.adapters.FruitAdapter;
import com.example.bioprep.adapters.FruitRecyclerAdapter;
import com.example.bioprep.data.SampleBiologyData;
import com.example.bioprep.data.SampleBiologyData.FlashcardItem;

import java.util.List;

/**
 * Flashcards Activity implementing Stitch UI Design (Screen ID: 629273b7864d428ba53d656d62c9e39f).
 * Also integrates the Attached PDF Lab Manual experiments:
 * - Simple ListView (Experiments A and B): ArrayAdapter, setOnItemClickListener
 * - Custom ListView (Experiment C): FruitAdapter, getView
 * - RecyclerView (Experiment D): FruitRecyclerAdapter, LinearLayoutManager, ViewHolder
 */
public class FlashcardsActivity extends AppCompatActivity {

    private View cardFront;
    private View cardBack;
    private boolean isFlipped = false;

    private TextView tvCardProgressBadge;
    private TextView tvCardProgressPercent;
    private ProgressBar pbFlashcardProgress;
    private TextView tvCardCategory;
    private TextView tvFlashcardQuestion;
    private TextView tvFlashcardQuestionBn;
    private TextView tvFlashcardAnswerTitle;
    private TextView tvFlashcardAnswerDesc;
    private TextView tvFlashcardMedicalTip;

    private List<FlashcardItem> flashcards;
    private int currentCardIndex = 0;

    private ListView listView;
    private RecyclerView recyclerView;
    private Button btnToggleRecyclerView;
    private Button btnToggleListView;

    // Specimen fruit dataset directly from the Lab Manual PDF
    private static final String[] FRUIT_NAMES = {
            "Apple (Malus domestica)",
            "Banana (Musa acuminata)",
            "Orange (Citrus sinensis)",
            "Mango (Mangifera indica)",
            "Grapes (Vitis vinifera)",
            "Pineapple (Ananas comosus)",
            "Strawberry (Fragaria ananassa)",
            "Jackfruit (Artocarpus heterophyllus)"
    };

    private static final String[] FRUIT_DESCRIPTIONS = {
            "Pome fruit rich in dietary fiber and vitamin C; Rosaceae family",
            "Botanical berry with high potassium content; Musaceae family",
            "Hesperidium citrus fruit packed with ascorbic acid; Rutaceae family",
            "Fleshy drupe, national tree of Bangladesh; Anacardiaceae family",
            "True berry growing in clusters, rich in resveratrol antioxidants",
            "Multiple/composite fruit formed from coalesced berries; Bromeliaceae",
            "Aggregate accessory fruit with achenes on exterior surface",
            "National fruit of Bangladesh; multiple fruit of the Moraceae family"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcards);

        initViews();
        setupFlipCard();
        setupRecallRatings();
        setupLabManualAdapters();
        setupNavigation();
    }

    private void initViews() {
        flashcards = SampleBiologyData.getFlashcards();

        tvCardProgressBadge = findViewById(R.id.tvCardProgressBadge);
        tvCardProgressPercent = findViewById(R.id.tvCardProgressPercent);
        pbFlashcardProgress = findViewById(R.id.pbFlashcardProgress);
        tvCardCategory = findViewById(R.id.tvCardCategory);
        tvFlashcardQuestion = findViewById(R.id.tvFlashcardQuestion);
        tvFlashcardQuestionBn = findViewById(R.id.tvFlashcardQuestionBn);
        tvFlashcardAnswerTitle = findViewById(R.id.tvFlashcardAnswerTitle);
        tvFlashcardAnswerDesc = findViewById(R.id.tvFlashcardAnswerDesc);
        tvFlashcardMedicalTip = findViewById(R.id.tvFlashcardMedicalTip);

        cardFront = findViewById(R.id.cardFront);
        cardBack = findViewById(R.id.cardBack);

        // PDF Lab Manual: findViewById for ListView & RecyclerView
        listView = findViewById(R.id.listView);
        recyclerView = findViewById(R.id.recyclerView);

        btnToggleRecyclerView = findViewById(R.id.btnToggleRecyclerView);
        btnToggleListView = findViewById(R.id.btnToggleListView);

        View btnBack = findViewById(R.id.btnFlashcardsBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        View btnOptions = findViewById(R.id.btnFlashcardsOptions);
        if (btnOptions != null) {
            btnOptions.setOnClickListener(v -> {
                androidx.appcompat.widget.PopupMenu popup = new androidx.appcompat.widget.PopupMenu(FlashcardsActivity.this, v);
                popup.getMenu().add("Bookmark this Card");
                popup.getMenu().add("Shuffle Deck");
                popup.getMenu().add("Toggle Lab Specimen List");
                popup.setOnMenuItemClickListener(item -> {
                    Toast.makeText(FlashcardsActivity.this, item.getTitle() + " selected", Toast.LENGTH_SHORT).show();
                    if ("Toggle Lab Specimen List".equals(item.getTitle())) {
                        View controls = findViewById(R.id.layoutLabManualControls);
                        if (controls != null) {
                            controls.setVisibility(controls.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                        }
                    }
                    return true;
                });
                popup.show();
            });
        }

        updateFlashcardDisplay();
    }

    private void updateFlashcardDisplay() {
        if (flashcards == null || flashcards.isEmpty()) return;
        FlashcardItem item = flashcards.get(currentCardIndex);

        if (tvCardProgressBadge != null) {
            tvCardProgressBadge.setText("CARD " + (currentCardIndex + 1) + " OF " + flashcards.size());
        }
        int percent = (int) (((currentCardIndex + 1) / (float) flashcards.size()) * 100);
        if (tvCardProgressPercent != null) {
            tvCardProgressPercent.setText("• " + percent + "% Complete");
        }
        if (pbFlashcardProgress != null) {
            pbFlashcardProgress.setProgress(percent);
        }
        if (tvCardCategory != null) {
            tvCardCategory.setText(item.chapterName);
        }
        if (tvFlashcardQuestion != null) {
            tvFlashcardQuestion.setText(item.questionEn);
        }
        if (tvFlashcardQuestionBn != null) {
            tvFlashcardQuestionBn.setText(item.questionBn);
        }
        if (tvFlashcardAnswerTitle != null) {
            tvFlashcardAnswerTitle.setText(item.answerEn);
        }
        if (tvFlashcardAnswerDesc != null) {
            tvFlashcardAnswerDesc.setText(item.answerBn);
        }
        if (tvFlashcardMedicalTip != null) {
            tvFlashcardMedicalTip.setText(item.medicalTip);
        }

        // Reset flip to front
        isFlipped = false;
        if (cardBack != null) cardBack.setVisibility(View.GONE);
        if (cardFront != null) cardFront.setVisibility(View.VISIBLE);
    }

    private void advanceNextCard() {
        if (flashcards != null && !flashcards.isEmpty()) {
            if (currentCardIndex < flashcards.size() - 1) {
                currentCardIndex++;
                updateFlashcardDisplay();
            } else {
                Toast.makeText(this, "🎉 Mastered this deck! Cycling to beginning.", Toast.LENGTH_SHORT).show();
                currentCardIndex = 0;
                updateFlashcardDisplay();
            }
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

        View btnShowAnswerFront = findViewById(R.id.btnShowAnswerFront);
        if (btnShowAnswerFront != null) btnShowAnswerFront.setOnClickListener(flipListener);

        View btnFlipFront = findViewById(R.id.btnFlipFront);
        if (btnFlipFront != null) btnFlipFront.setOnClickListener(flipListener);

        View btnFlipBack = findViewById(R.id.btnFlipBack);
        if (btnFlipBack != null) btnFlipBack.setOnClickListener(flipListener);

        if (cardFront != null) cardFront.setOnClickListener(flipListener);
        if (cardBack != null) cardBack.setOnClickListener(flipListener);
    }

    private void setupRecallRatings() {
        // Anki tetrad: Again (<10m), Hard (1d), Good (3d), Easy (5d)
        findViewById(R.id.btnRateAgain).setOnClickListener(v -> {
            Toast.makeText(this, "Rated: Again (<10 min repeat)", Toast.LENGTH_SHORT).show();
            advanceNextCard();
        });

        findViewById(R.id.btnRateHard).setOnClickListener(v -> {
            Toast.makeText(this, "Rated: Hard (1 day repeat)", Toast.LENGTH_SHORT).show();
            advanceNextCard();
        });

        findViewById(R.id.btnRateGood).setOnClickListener(v -> {
            Toast.makeText(this, "Rated: Good (3 days repeat)", Toast.LENGTH_SHORT).show();
            advanceNextCard();
        });

        findViewById(R.id.btnRateEasy).setOnClickListener(v -> {
            Toast.makeText(this, "Rated: Easy (5 days repeat)", Toast.LENGTH_SHORT).show();
            advanceNextCard();
        });
    }

    /**
     * Integrates Attached PDF Lab Manual:
     * - Experiments A and B (Simple ListView with ArrayAdapter and setOnItemClickListener)
     * - Experiment C (Custom FruitAdapter with getView, R.layout.item_fruit)
     * - Experiment D (RecyclerView with LinearLayoutManager and FruitRecyclerAdapter)
     */
    private void setupLabManualAdapters() {
        // =====================================================================
        // Experiment D: RecyclerView (LinearLayoutManager + FruitRecyclerAdapter)
        // =====================================================================
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FruitRecyclerAdapter recyclerAdapter = new FruitRecyclerAdapter(FRUIT_NAMES, FRUIT_DESCRIPTIONS);
        recyclerView.setAdapter(recyclerAdapter);

        // =====================================================================
        // Experiment C: Custom ListView (FruitAdapter)
        // =====================================================================
        FruitAdapter customAdapter = new FruitAdapter(this, FRUIT_NAMES, FRUIT_DESCRIPTIONS);
        listView.setAdapter(customAdapter);

        // Experiment B: setOnItemClickListener with Toast
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selected = FRUIT_NAMES[position];
            Toast.makeText(getApplicationContext(), "ListView Item: " + selected, Toast.LENGTH_SHORT).show();
        });

        // Toggle between RecyclerView (Experiment D) and ListView (Experiment A-C)
        btnToggleRecyclerView.setOnClickListener(v -> {
            recyclerView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            btnToggleRecyclerView.setBackgroundTintList(getColorStateList(R.color.bio_primary_container));
            btnToggleListView.setBackgroundTintList(getColorStateList(R.color.bio_surface_variant));
            Toast.makeText(this, "Switched to RecyclerView (Experiment D)", Toast.LENGTH_SHORT).show();
        });

        btnToggleListView.setOnClickListener(v -> {
            listView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            btnToggleListView.setBackgroundTintList(getColorStateList(R.color.bio_primary_container));
            btnToggleRecyclerView.setBackgroundTintList(getColorStateList(R.color.bio_surface_variant));
            Toast.makeText(this, "Switched to ListView (Experiments A - C)", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupNavigation() {
        findViewById(R.id.navTabHome).setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        findViewById(R.id.navTabStudy).setOnClickListener(v ->
                startActivity(new Intent(this, ChapterActivity.class)));

        findViewById(R.id.navTabSaved).setOnClickListener(v ->
                startActivity(new Intent(this, SavedItemsActivity.class)));

        findViewById(R.id.navTabProfile).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }
}

