package com.example.bioprep.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bioprep.R;

/**
 * Saved Items Activity implementing Stitch UI Design (Screen ID: e888790cdd02497dba1b515e3036d0a6).
 * Manages bookmarked study notes, active recall drills, flashcard decks, and board question PDFs.
 */
public class SavedItemsActivity extends AppCompatActivity {

    private View cardSavedNote;
    private View cardSavedRecall;
    private View cardSavedFlashcard;
    private View cardSavedResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_items);

        initViews();
        setupFilterPills();
        setupCardClicks();
        setupNavigation();
    }

    private void initViews() {
        cardSavedNote = findViewById(R.id.cardSavedNote);
        cardSavedRecall = findViewById(R.id.cardSavedRecall);
        cardSavedFlashcard = findViewById(R.id.cardSavedFlashcard);
        cardSavedResource = findViewById(R.id.cardSavedResource);

        View btnBack = findViewById(R.id.btnSavedBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        View btnMore = findViewById(R.id.btnSavedMore);
        if (btnMore != null) {
            btnMore.setOnClickListener(v ->
                    Toast.makeText(this, "Saved Items options: Select all, Clear bookmarks", Toast.LENGTH_SHORT).show());
        }

        android.widget.EditText etSearchSaved = findViewById(R.id.etSearchSaved);
        if (etSearchSaved != null) {
            etSearchSaved.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterSavedItems(s.toString());
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {}
            });
        }
    }

    private void filterSavedItems(String query) {
        if (query == null || query.trim().isEmpty()) {
            cardSavedNote.setVisibility(View.VISIBLE);
            cardSavedRecall.setVisibility(View.VISIBLE);
            cardSavedFlashcard.setVisibility(View.VISIBLE);
            cardSavedResource.setVisibility(View.VISIBLE);
            return;
        }
        String lower = query.toLowerCase().trim();
        cardSavedNote.setVisibility("photosynthesis".contains(lower) || "note".contains(lower) || "calvin".contains(lower) ? View.VISIBLE : View.GONE);
        cardSavedRecall.setVisibility("calvin".contains(lower) || "recall".contains(lower) || "drill".contains(lower) ? View.VISIBLE : View.GONE);
        cardSavedFlashcard.setVisibility("powerhouse".contains(lower) || "mitochondria".contains(lower) || "flashcard".contains(lower) ? View.VISIBLE : View.GONE);
        cardSavedResource.setVisibility("nctb".contains(lower) || "resource".contains(lower) || "textbook".contains(lower) ? View.VISIBLE : View.GONE);
    }

    private void setupFilterPills() {
        TextView pillAll = findViewById(R.id.pillFilterAll);
        TextView pillNotes = findViewById(R.id.pillFilterNotes);
        TextView pillRecall = findViewById(R.id.pillFilterRecall);
        TextView pillFlashcards = findViewById(R.id.pillFilterFlashcards);
        TextView pillResources = findViewById(R.id.pillFilterResources);

        pillAll.setOnClickListener(v -> {
            cardSavedNote.setVisibility(View.VISIBLE);
            cardSavedRecall.setVisibility(View.VISIBLE);
            cardSavedFlashcard.setVisibility(View.VISIBLE);
            cardSavedResource.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Showing all saved items", Toast.LENGTH_SHORT).show();
        });

        pillNotes.setOnClickListener(v -> {
            cardSavedNote.setVisibility(View.VISIBLE);
            cardSavedRecall.setVisibility(View.GONE);
            cardSavedFlashcard.setVisibility(View.GONE);
            cardSavedResource.setVisibility(View.GONE);
            Toast.makeText(this, "Showing saved notes", Toast.LENGTH_SHORT).show();
        });

        pillRecall.setOnClickListener(v -> {
            cardSavedNote.setVisibility(View.GONE);
            cardSavedRecall.setVisibility(View.VISIBLE);
            cardSavedFlashcard.setVisibility(View.GONE);
            cardSavedResource.setVisibility(View.GONE);
            Toast.makeText(this, "Showing saved active recall", Toast.LENGTH_SHORT).show();
        });

        pillFlashcards.setOnClickListener(v -> {
            cardSavedNote.setVisibility(View.GONE);
            cardSavedRecall.setVisibility(View.GONE);
            cardSavedFlashcard.setVisibility(View.VISIBLE);
            cardSavedResource.setVisibility(View.GONE);
            Toast.makeText(this, "Showing saved flashcards", Toast.LENGTH_SHORT).show();
        });

        pillResources.setOnClickListener(v -> {
            cardSavedNote.setVisibility(View.GONE);
            cardSavedRecall.setVisibility(View.GONE);
            cardSavedFlashcard.setVisibility(View.GONE);
            cardSavedResource.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Showing saved resources", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupCardClicks() {
        // Saved Note -> NotesActivity
        View.OnClickListener noteOpener = v -> startActivity(new Intent(this, NotesActivity.class));
        cardSavedNote.setOnClickListener(noteOpener);
        findViewById(R.id.btnOpenSavedNote).setOnClickListener(noteOpener);

        // Saved Recall -> ActiveRecallActivity
        View.OnClickListener recallOpener = v -> startActivity(new Intent(this, ActiveRecallActivity.class));
        cardSavedRecall.setOnClickListener(recallOpener);
        findViewById(R.id.btnOpenSavedRecall).setOnClickListener(recallOpener);

        // Saved Flashcard -> FlashcardsActivity
        View.OnClickListener flashcardOpener = v -> startActivity(new Intent(this, FlashcardsActivity.class));
        cardSavedFlashcard.setOnClickListener(flashcardOpener);
        findViewById(R.id.btnOpenSavedFlashcard).setOnClickListener(flashcardOpener);

        // Saved Resource -> ResourcesActivity
        View.OnClickListener resourceOpener = v -> startActivity(new Intent(this, ResourcesActivity.class));
        cardSavedResource.setOnClickListener(resourceOpener);
        findViewById(R.id.btnOpenSavedResource).setOnClickListener(resourceOpener);
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
                Toast.makeText(this, "Already on Saved Items", Toast.LENGTH_SHORT).show());

        findViewById(R.id.navTabProfile).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }
}

