package com.example.bioprep.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bioprep.R;
import com.example.bioprep.database.AppDatabase;
import com.example.bioprep.database.NoteEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class ViewNoteActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE_ID = "extra_note_id";

    private TextView tvDetailTitle;
    private TextView tvDetailDate;
    private TextView tvDetailContent;
    private ImageView ivDetailImage;

    private long noteId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        noteId = getIntent().getLongExtra(EXTRA_NOTE_ID, -1);

        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailDate = findViewById(R.id.tvDetailDate);
        tvDetailContent = findViewById(R.id.tvDetailContent);
        ivDetailImage = findViewById(R.id.ivDetailImage);

        ImageButton btnBack = findViewById(R.id.btnViewNoteBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        ImageButton btnDelete = findViewById(R.id.btnDeleteNote);
        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> confirmDeleteNote());
        }

        loadNoteData();
    }

    private void loadNoteData() {
        if (noteId == -1) return;

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            NoteEntity note = db.noteDao().getNoteById(noteId);

            if (note != null) {
                runOnUiThread(() -> {
                    tvDetailTitle.setText(note.getTitle());
                    tvDetailContent.setText(note.getContent());

                    String dateStr = new SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
                            .format(new Date(note.getCreatedAt()));
                    tvDetailDate.setText(dateStr);

                    if (note.getImageUri() != null && !note.getImageUri().isEmpty()) {
                        try {
                            Bitmap bitmap = BitmapFactory.decodeFile(note.getImageUri());
                            if (bitmap != null) {
                                ivDetailImage.setImageBitmap(bitmap);
                                ivDetailImage.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception ignored) {}
                    }
                });
            }
        });
    }

    private void confirmDeleteNote() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to permanently delete this note?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                        db.noteDao().deleteNoteById(noteId);

                        runOnUiThread(() -> {
                            setResult(RESULT_OK);
                            finish();
                        });
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

