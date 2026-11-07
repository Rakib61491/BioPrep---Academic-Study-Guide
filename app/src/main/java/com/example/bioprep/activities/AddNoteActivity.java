package com.example.bioprep.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.bioprep.R;
import com.example.bioprep.database.AppDatabase;
import com.example.bioprep.database.NoteEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.Executors;

/**
 * Add Note Activity implementing Requirement 5:
 * Title field, Description/content field, Add image (Gallery/Camera), image preview,
 * remove/replace image, validation, and persistent Room Database storage.
 */
public class AddNoteActivity extends AppCompatActivity {

    private EditText etNoteTitle;
    private EditText etNoteContent;
    private FrameLayout frameImagePreview;
    private ImageView ivNoteImagePreview;
    private Button btnReplaceImage;
    private Button btnRemoveImage;
    private Button btnPickGallery;
    private Button btnCaptureCamera;
    private Button btnSaveNoteTop;
    private Button btnSaveNoteBottom;

    private String selectedImageUriString = null;
    private Uri cameraCaptureUri = null;
    private File cameraTempFile = null;

    // Pick from device gallery
    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    copyAndSetSelectedImage(uri);
                }
            });

    // Capture photo from camera
    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && cameraTempFile != null && cameraTempFile.exists()) {
                    copyAndSetSelectedImage(Uri.fromFile(cameraTempFile));
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etNoteTitle = findViewById(R.id.etNoteTitle);
        etNoteContent = findViewById(R.id.etNoteContent);
        frameImagePreview = findViewById(R.id.frameImagePreview);
        ivNoteImagePreview = findViewById(R.id.ivNoteImagePreview);
        btnReplaceImage = findViewById(R.id.btnReplaceImage);
        btnRemoveImage = findViewById(R.id.btnRemoveImage);
        btnPickGallery = findViewById(R.id.btnPickGallery);
        btnCaptureCamera = findViewById(R.id.btnCaptureCamera);
        btnSaveNoteTop = findViewById(R.id.btnSaveNoteTop);
        btnSaveNoteBottom = findViewById(R.id.btnSaveNoteBottom);

        ImageButton btnBack = findViewById(R.id.btnAddNoteBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void setupListeners() {
        btnPickGallery.setOnClickListener(v -> galleryLauncher.launch("image/*"));

        btnCaptureCamera.setOnClickListener(v -> launchCameraCapture());

        btnReplaceImage.setOnClickListener(v -> galleryLauncher.launch("image/*"));

        btnRemoveImage.setOnClickListener(v -> {
            selectedImageUriString = null;
            frameImagePreview.setVisibility(View.GONE);
            ivNoteImagePreview.setImageDrawable(null);
        });

        View.OnClickListener saveListener = v -> saveNoteToDatabase();
        btnSaveNoteTop.setOnClickListener(saveListener);
        btnSaveNoteBottom.setOnClickListener(saveListener);
    }

    private void launchCameraCapture() {
        try {
            File imagesDir = new File(getFilesDir(), "images");
            if (!imagesDir.exists()) imagesDir.mkdirs();

            cameraTempFile = File.createTempFile("note_camera_", ".jpg", imagesDir);
            cameraCaptureUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    cameraTempFile
            );
            cameraLauncher.launch(cameraCaptureUri);
        } catch (Exception e) {
            Toast.makeText(this, "Could not open camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void copyAndSetSelectedImage(Uri sourceUri) {
        try {
            File imagesDir = new File(getFilesDir(), "images");
            if (!imagesDir.exists()) imagesDir.mkdirs();

            File destinationFile = new File(imagesDir, "note_img_" + System.currentTimeMillis() + ".jpg");

            InputStream is = getContentResolver().openInputStream(sourceUri);
            FileOutputStream fos = new FileOutputStream(destinationFile);
            byte[] buffer = new byte[4096];
            int read;
            while ((read = is.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }
            fos.close();
            is.close();

            selectedImageUriString = destinationFile.getAbsolutePath();

            // Display in preview
            Bitmap bitmap = BitmapFactory.decodeFile(selectedImageUriString);
            ivNoteImagePreview.setImageBitmap(bitmap);
            frameImagePreview.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Toast.makeText(this, "Error processing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveNoteToDatabase() {
        String title = etNoteTitle.getText().toString().trim();
        String content = etNoteContent.getText().toString().trim();

        // Validation: prevent completely empty notes
        if (title.isEmpty() && content.isEmpty()) {
            Toast.makeText(this, "Please enter a title or note content.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.isEmpty()) {
            title = "Untitled Biology Note";
        }

        final NoteEntity note = new NoteEntity(
                title,
                content,
                selectedImageUriString,
                System.currentTimeMillis()
        );

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            db.noteDao().insertNote(note);

            runOnUiThread(() -> {
                Toast.makeText(AddNoteActivity.this, "✓ Note saved to notebook!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            });
        });
    }
}

