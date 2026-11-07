package com.example.bioprep.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bioprep.R;
import com.example.bioprep.providers.BioNotesProvider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Biology Notes Notebook Activity demonstrating Lab 08 Topic 10 (Content Providers).
 * Includes all listed methods: onTouchEvent, hideSoftInputFromWindow, onClickAddDetails,
 * onClickShowDetails, ContentResolver insert/query, Cursor methods, StringBuilder, etc.
 */
public class NotesActivity extends AppCompatActivity {

    private EditText etNoteTitle;
    private EditText etNoteDetails;
    private TextView tvQueryResult;
    private LinearLayout layoutNotesListContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        etNoteTitle = findViewById(R.id.etNoteTitle);
        etNoteDetails = findViewById(R.id.etNoteDetails);
        tvQueryResult = findViewById(R.id.tvQueryResult);
        layoutNotesListContainer = findViewById(R.id.layoutNotesListContainer);

        ImageButton btnNotesBack = findViewById(R.id.btnNotesBack);
        if (btnNotesBack != null) {
            btnNotesBack.setOnClickListener(v -> finish());
        }

        // "+ Add New Note" button from Notes.jpeg
        View btnAddNewNote = findViewById(R.id.btnAddNewNote);
        if (btnAddNewNote != null) {
            btnAddNewNote.setOnClickListener(v -> showAddNoteDialog());
        }

        // Category filter chips from Notes.jpeg
        setupCategoryFilters();

        View btnNotesOptions = findViewById(R.id.btnNotesOptions);
        if (btnNotesOptions != null) {
            btnNotesOptions.setOnClickListener(v -> {
                androidx.appcompat.widget.PopupMenu popup = new androidx.appcompat.widget.PopupMenu(NotesActivity.this, v);
                popup.getMenu().add("New Note");
                popup.getMenu().add("Sort by Date");
                popup.getMenu().add("Clear All Notes");
                popup.setOnMenuItemClickListener(item -> {
                    if ("New Note".equals(item.getTitle())) {
                        showAddNoteDialog();
                    } else if ("Clear All Notes".equals(item.getTitle())) {
                        getContentResolver().delete(BioNotesProvider.CONTENT_URI, null, null);
                        displayNotesFromProvider("");
                        Toast.makeText(NotesActivity.this, "All notes cleared", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                });
                popup.show();
            });
        }

        // Live Search Filter for Notes
        EditText etSearchNotes = findViewById(R.id.etSearchNotes);
        if (etSearchNotes != null) {
            etSearchNotes.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    displayNotesFromProvider(s.toString().trim());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        // Setup 4-tab bottom navigation
        setupNavigation();

        // Initial load of notes from provider
        displayNotesFromProvider("");
    }

    private void setupCategoryFilters() {
        View.OnClickListener filterListener = v -> {
            String cat = "";
            int id = v.getId();
            if (id == R.id.catPhotosynthesis) cat = "Photosynthesis";
            else if (id == R.id.catGenetics) cat = "Genetics";
            else if (id == R.id.catCellBiology) cat = "Cell Biology";
            else if (id == R.id.catEcology) cat = "Ecology";

            Toast.makeText(this, "Filter: " + (cat.isEmpty() ? "All" : cat), Toast.LENGTH_SHORT).show();
            displayNotesFromProvider(cat);
        };

        if (findViewById(R.id.catPhotosynthesis) != null) findViewById(R.id.catPhotosynthesis).setOnClickListener(filterListener);
        if (findViewById(R.id.catGenetics) != null) findViewById(R.id.catGenetics).setOnClickListener(filterListener);
        if (findViewById(R.id.catCellBiology) != null) findViewById(R.id.catCellBiology).setOnClickListener(filterListener);
        if (findViewById(R.id.catEcology) != null) findViewById(R.id.catEcology).setOnClickListener(filterListener);
    }

    private void showAddNoteDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Create Biology Note");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 24, 32, 16);

        final EditText inputTitle = new EditText(this);
        inputTitle.setHint("Note Title (e.g. Calvin Cycle Steps)");
        layout.addView(inputTitle);

        final EditText inputDetails = new EditText(this);
        inputDetails.setHint("Details / Summary...");
        inputDetails.setMinLines(3);
        layout.addView(inputDetails);

        builder.setView(layout);
        builder.setPositiveButton("Save Note", (dialog, which) -> {
            String title = inputTitle.getText().toString().trim();
            String details = inputDetails.getText().toString().trim();
            if (!title.isEmpty() && !details.isEmpty()) {
                String currentDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());
                ContentValues values = new ContentValues();
                values.put(BioNotesProvider.TITLE, title);
                values.put(BioNotesProvider.DETAILS, details);
                values.put(BioNotesProvider.CATEGORY, "Biology");
                values.put(BioNotesProvider.CREATED_AT, currentDate);
                getContentResolver().insert(BioNotesProvider.CONTENT_URI, values);
                displayNotesFromProvider("");
                Toast.makeText(this, "Note saved to BioNotesProvider!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
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

    /**
     * Topic 10: Touch event to hide soft keyboard when clicking outside input fields.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return true;
    }

    /**
     * Topic 10: Adds new record via ContentResolver.insert().
     * Bound to Button via android:onClick="onClickAddDetails" in XML.
     */
    public void onClickAddDetails(View view) {
        String title = etNoteTitle.getText().toString().trim();
        String details = etNoteDetails.getText().toString().trim();

        if (title.isEmpty() || details.isEmpty()) {
            Toast.makeText(this, "Please enter both title and details", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());

        ContentValues values = new ContentValues();
        values.put(BioNotesProvider.TITLE, title);
        values.put(BioNotesProvider.DETAILS, details);
        values.put(BioNotesProvider.CATEGORY, "HSC Biology");
        values.put(BioNotesProvider.CREATED_AT, currentDate);

        Uri uri = getContentResolver().insert(BioNotesProvider.CONTENT_URI, values);

        if (uri != null) {
            Toast.makeText(getBaseContext(), "Note Saved: " + uri.toString(), Toast.LENGTH_SHORT).show();
            etNoteTitle.setText("");
            etNoteDetails.setText("");

            // Hide keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

            // Refresh cards
            displayNotesFromProvider();
        }
    }

    /**
     * Topic 10: Queries notes using ContentResolver.query(), iterates Cursor, builds text, and displays.
     * Bound to Button via android:onClick="onClickShowDetails" in XML.
     */
    public void onClickShowDetails(View view) {
        displayNotesFromProvider();
        Toast.makeText(this, "Notes queried from BioNotesProvider", Toast.LENGTH_SHORT).show();
    }

    private void seedDefaultNotes() {
        ContentValues v1 = new ContentValues();
        v1.put(BioNotesProvider.TITLE, "Photosynthesis & Respiration");
        v1.put(BioNotesProvider.DETAILS, "Features key stages like the Calvin cycle, light-dependent reactions, and photolysis.");
        v1.put(BioNotesProvider.CATEGORY, "Biology");
        v1.put(BioNotesProvider.CREATED_AT, "Oct 25, 2026");
        getContentResolver().insert(BioNotesProvider.CONTENT_URI, v1);

        ContentValues v2 = new ContentValues();
        v2.put(BioNotesProvider.TITLE, "Mitosis & Meiosis");
        v2.put(BioNotesProvider.DETAILS, "Stages of cell division (Prophase, Metaphase, Anaphase, Telophase) and crossing over.");
        v2.put(BioNotesProvider.CATEGORY, "Genetics");
        v2.put(BioNotesProvider.CREATED_AT, "Oct 18, 2026");
        getContentResolver().insert(BioNotesProvider.CONTENT_URI, v2);

        ContentValues v3 = new ContentValues();
        v3.put(BioNotesProvider.TITLE, "Structure of DNA");
        v3.put(BioNotesProvider.DETAILS, "Understanding the double helix, nucleotide bonds, base pairings (A-T, G-C).");
        v3.put(BioNotesProvider.CATEGORY, "Biochemistry");
        v3.put(BioNotesProvider.CREATED_AT, "Oct 12, 2026");
        getContentResolver().insert(BioNotesProvider.CONTENT_URI, v3);
    }

    private void displayNotesFromProvider() {
        displayNotesFromProvider("");
    }

    /**
     * Topic 10: Full Cursor traversal using moveToFirst(), isAfterLast(), getColumnIndex(), getString(), moveToNext().
     */
    private void displayNotesFromProvider(String filterQuery) {
        String selection = null;
        String[] selectionArgs = null;
        if (filterQuery != null && !filterQuery.isEmpty()) {
            selection = BioNotesProvider.TITLE + " LIKE ? OR " + BioNotesProvider.DETAILS + " LIKE ?";
            selectionArgs = new String[]{"%" + filterQuery + "%", "%" + filterQuery + "%"};
        }

        Cursor cursor = getContentResolver().query(
                BioNotesProvider.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                BioNotesProvider._ID + " DESC"
        );

        if (cursor == null || cursor.getCount() == 0) {
            if (cursor != null) cursor.close();
            if (filterQuery == null || filterQuery.isEmpty()) {
                seedDefaultNotes();
                cursor = getContentResolver().query(
                        BioNotesProvider.CONTENT_URI,
                        null,
                        null,
                        null,
                        BioNotesProvider._ID + " DESC"
                );
            }
        }

        if (cursor == null) return;

        layoutNotesListContainer.removeAllViews();
        StringBuilder stringBuilder = new StringBuilder();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int idIndex = cursor.getColumnIndex(BioNotesProvider._ID);
                int titleIndex = cursor.getColumnIndex(BioNotesProvider.TITLE);
                int detailsIndex = cursor.getColumnIndex(BioNotesProvider.DETAILS);
                int categoryIndex = cursor.getColumnIndex(BioNotesProvider.CATEGORY);
                int dateIndex = cursor.getColumnIndex(BioNotesProvider.CREATED_AT);

                String id = idIndex != -1 ? cursor.getString(idIndex) : "";
                String title = titleIndex != -1 ? cursor.getString(titleIndex) : "Untitled";
                String details = detailsIndex != -1 ? cursor.getString(detailsIndex) : "";
                String category = categoryIndex != -1 ? cursor.getString(categoryIndex) : "General";
                String date = dateIndex != -1 ? cursor.getString(dateIndex) : "";

                // Append to raw StringBuilder (as listed in Topic 10)
                stringBuilder.append("ID: ").append(id)
                        .append("\nTitle: ").append(title)
                        .append("\nDetails: ").append(details)
                        .append("\nDate: ").append(date)
                        .append("\n\n");

                // Render clinical white card matching Stitch UI Design
                renderNoteCard(id, title, details, category, date);

                cursor.moveToNext();
            }
        } else {
            stringBuilder.append("No notes found in BioNotesProvider database.");
        }
        cursor.close();

        if (tvQueryResult != null) {
            tvQueryResult.setText(stringBuilder.toString());
        }
    }

    private void renderNoteCard(String id, String title, String details, String category, String date) {
        View card = LayoutInflater.from(this).inflate(R.layout.item_note_card, layoutNotesListContainer, false);

        TextView txtTitle = card.findViewById(R.id.tvNoteCardTitle);
        TextView txtDate = card.findViewById(R.id.tvNoteCardDate);
        TextView txtCategory = card.findViewById(R.id.tvNoteCardCategory);
        TextView txtDetails = card.findViewById(R.id.tvNoteCardDetails);
        View btnEdit = card.findViewById(R.id.btnEditNoteCard);

        if (txtTitle != null) txtTitle.setText(title);
        if (txtDate != null) txtDate.setText(date);
        if (txtCategory != null) txtCategory.setText(category);
        if (txtDetails != null) txtDetails.setText(details);

        if (btnEdit != null) {
            btnEdit.setOnClickListener(v -> {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Edit Note");

                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(32, 24, 32, 16);

                final EditText editTitle = new EditText(this);
                editTitle.setText(title);
                layout.addView(editTitle);

                final EditText editDetails = new EditText(this);
                editDetails.setText(details);
                editDetails.setMinLines(3);
                layout.addView(editDetails);

                builder.setView(layout);
                builder.setPositiveButton("Update", (dialog, which) -> {
                    ContentValues cv = new ContentValues();
                    cv.put(BioNotesProvider.TITLE, editTitle.getText().toString());
                    cv.put(BioNotesProvider.DETAILS, editDetails.getText().toString());
                    Uri updateUri = Uri.withAppendedPath(BioNotesProvider.CONTENT_URI, id);
                    getContentResolver().update(updateUri, cv, null, null);
                    displayNotesFromProvider("");
                    Toast.makeText(this, "Note updated!", Toast.LENGTH_SHORT).show();
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            });
        }

        // Long click to delete record via ContentResolver.delete()
        card.setOnLongClickListener(v -> {
            Uri deleteUri = Uri.withAppendedPath(BioNotesProvider.CONTENT_URI, id);
            int rowsDeleted = getContentResolver().delete(deleteUri, null, null);
            if (rowsDeleted > 0) {
                Toast.makeText(this, "Deleted note #" + id, Toast.LENGTH_SHORT).show();
                displayNotesFromProvider("");
            }
            return true;
        });

        layoutNotesListContainer.addView(card);
    }
}

