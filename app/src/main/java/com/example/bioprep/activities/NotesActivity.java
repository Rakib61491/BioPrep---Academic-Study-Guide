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
        View card = LayoutInflater.from(this).inflate(R.layout.item_fruit, layoutNotesListContainer, false);

        TextView txtGlyph = card.findViewById(R.id.imgFruitGlyph);
        TextView txtName = card.findViewById(R.id.txtName);
        TextView txtDescription = card.findViewById(R.id.txtDescription);

        if (txtGlyph != null) {
            txtGlyph.setText("📝");
        }
        if (txtName != null) {
            txtName.setText(title + (date.isEmpty() ? "" : " • " + date));
        }
        if (txtDescription != null) {
            txtDescription.setText(details + "\nCategory: " + category);
        }

        // Long click to delete record via ContentResolver.delete()
        card.setOnLongClickListener(v -> {
            Uri deleteUri = Uri.withAppendedPath(BioNotesProvider.CONTENT_URI, id);
            int rowsDeleted = getContentResolver().delete(deleteUri, null, null);
            if (rowsDeleted > 0) {
                Toast.makeText(this, "Deleted note #" + id, Toast.LENGTH_SHORT).show();
                displayNotesFromProvider();
            }
            return true;
        });

        layoutNotesListContainer.addView(card);
    }
}

