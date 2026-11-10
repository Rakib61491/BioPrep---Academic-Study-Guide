package com.example.bioprep.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bioprep.R;
import com.example.bioprep.database.AppDatabase;
import com.example.bioprep.database.NoteEntity;
import com.example.bioprep.database.SavedItemEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

/**
 * Notes Activity implementing Requirement 5:
 * - Empty state initially ("No notes yet. Create your first note.").
 * - No dummy/sample notes.
 * - Categories and Topic 10 ContentProvider UI removed.
 * - "+ Add New Note" opens dedicated AddNoteActivity.
 * - Displays saved notes from Room Database with title, content, date, and image indicator.
 * - Tapping a note opens ViewNoteActivity.
 */
public class NotesActivity extends AppCompatActivity {

    private LinearLayout layoutNotesListContainer;
    private LinearLayout layoutNotesEmpty;
    private EditText etSearchNotes;

    private List<NoteEntity> allNotes = new ArrayList<>();
    private String currentSearchQuery = "";

    private final ActivityResultLauncher<Intent> addNoteLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadNotesFromDatabase();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        layoutNotesListContainer = findViewById(R.id.layoutNotesListContainer);
        layoutNotesEmpty = findViewById(R.id.layoutNotesEmpty);
        etSearchNotes = findViewById(R.id.etSearchNotes);

        ImageButton btnNotesBack = findViewById(R.id.btnNotesBack);
        if (btnNotesBack != null) {
            btnNotesBack.setOnClickListener(v -> finish());
        }

        View btnAddNewNote = findViewById(R.id.btnAddNewNote);
        if (btnAddNewNote != null) {
            btnAddNewNote.setOnClickListener(v -> {
                Intent intent = new Intent(NotesActivity.this, AddNoteActivity.class);
                addNoteLauncher.launch(intent);
            });
        }

        if (etSearchNotes != null) {
            etSearchNotes.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentSearchQuery = s.toString().trim().toLowerCase();
                    renderNotes();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        setupNavigation();
        loadNotesFromDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotesFromDatabase();
    }

    private void loadNotesFromDatabase() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<NoteEntity> notes = db.noteDao().getAllNotes();

            runOnUiThread(() -> {
                allNotes = notes != null ? notes : new ArrayList<>();
                renderNotes();
            });
        });
    }

    private void renderNotes() {
        if (layoutNotesListContainer == null) return;
        layoutNotesListContainer.removeAllViews();

        List<NoteEntity> filtered = new ArrayList<>();
        for (NoteEntity n : allNotes) {
            if (currentSearchQuery.isEmpty() ||
                    n.getTitle().toLowerCase().contains(currentSearchQuery) ||
                    (n.getContent() != null && n.getContent().toLowerCase().contains(currentSearchQuery))) {
                filtered.add(n);
            }
        }

        if (filtered.isEmpty()) {
            if (layoutNotesEmpty != null) {
                layoutNotesEmpty.setVisibility(View.VISIBLE);
            }
            return;
        }

        if (layoutNotesEmpty != null) {
            layoutNotesEmpty.setVisibility(View.GONE);
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        for (NoteEntity note : filtered) {
            View card = inflater.inflate(R.layout.item_note_card, layoutNotesListContainer, false);

            TextView tvTitle = card.findViewById(R.id.tvNoteCardTitle);
            TextView tvSnippet = card.findViewById(R.id.tvNoteCardDetails);
            TextView tvDate = card.findViewById(R.id.tvNoteCardDate);
            TextView tvCategory = card.findViewById(R.id.tvNoteCardCategory);

            if (tvTitle != null) tvTitle.setText(note.getTitle());
            if (tvSnippet != null) {
                String content = note.getContent();
                if (content != null && content.length() > 90) {
                    content = content.substring(0, 87) + "...";
                }
                tvSnippet.setText(content != null && !content.isEmpty() ? content : "No text details");
            }
            if (tvDate != null) {
                String formattedDate = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                        .format(new Date(note.getCreatedAt()));
                tvDate.setText(formattedDate);
            }

            if (tvCategory != null) {
                if (note.getImageUri() != null && !note.getImageUri().isEmpty()) {
                    tvCategory.setText("📷 Note with Diagram");
                } else {
                    tvCategory.setText("📝 Biology Note");
                }
            }

            card.setOnClickListener(v -> {
                Intent intent = new Intent(NotesActivity.this, ViewNoteActivity.class);
                intent.putExtra(ViewNoteActivity.EXTRA_NOTE_ID, note.getId());
                addNoteLauncher.launch(intent);
            });

            TextView btnSaveNoteCard = card.findViewById(R.id.btnSaveNoteCard);
            if (btnSaveNoteCard != null) {
                String noteIdStr = String.valueOf(note.getId());
                Executors.newSingleThreadExecutor().execute(() -> {
                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    int count = db.savedItemDao().isItemSaved("NOTE", noteIdStr);
                    boolean isSaved = (count > 0);
                    runOnUiThread(() -> btnSaveNoteCard.setText(isSaved ? "⭐" : "🔖"));
                });

                btnSaveNoteCard.setOnClickListener(v -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                        int count = db.savedItemDao().isItemSaved("NOTE", noteIdStr);
                        if (count > 0) {
                            db.savedItemDao().deleteByTypeAndItemId("NOTE", noteIdStr);
                            runOnUiThread(() -> btnSaveNoteCard.setText("🔖"));
                        } else {
                            SavedItemEntity saved = new SavedItemEntity(
                                    "NOTE",
                                    noteIdStr,
                                    note.getTitle(),
                                    note.getContent() != null ? note.getContent() : "",
                                    "Personal Study Note",
                                    System.currentTimeMillis()
                            );
                            db.savedItemDao().insert(saved);
                            runOnUiThread(() -> btnSaveNoteCard.setText("⭐"));
                        }
                    });
                });
            }

            layoutNotesListContainer.addView(card);
        }
    }

    private void setupNavigation() {
        View navHome = findViewById(R.id.navTabHome);
        if (navHome != null) navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        View navStudy = findViewById(R.id.navTabStudy);
        if (navStudy != null) navStudy.setOnClickListener(v ->
                startActivity(new Intent(this, ChapterActivity.class)));
        View navSaved = findViewById(R.id.navTabSaved);
        if (navSaved != null) navSaved.setOnClickListener(v ->
                startActivity(new Intent(this, SavedItemsActivity.class)));
        View navProfile = findViewById(R.id.navTabProfile);
        if (navProfile != null) navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }
}
