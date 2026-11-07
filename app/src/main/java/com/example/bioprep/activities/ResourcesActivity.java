package com.example.bioprep.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bioprep.R;
import com.example.bioprep.utils.NotificationHelper;

/**
 * Bangladeshi Resources Library Activity demonstrating Lab 08 Topic 3 (Implicit Intents).
 * Implements: new Intent(Intent.ACTION_VIEW), intent.setData(Uri.parse(...)), Uri.parse(), startActivity().
 */
public class ResourcesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources);

        ImageButton btnBack = findViewById(R.id.btnResourcesBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        View btnOptions = findViewById(R.id.btnResourcesOptions);
        if (btnOptions != null) {
            btnOptions.setOnClickListener(v -> {
                androidx.appcompat.widget.PopupMenu popup = new androidx.appcompat.widget.PopupMenu(ResourcesActivity.this, v);
                popup.getMenu().add("Check for Updates");
                popup.getMenu().add("Offline Downloads");
                popup.getMenu().add("Help & Support");
                popup.setOnMenuItemClickListener(item -> {
                    Toast.makeText(ResourcesActivity.this, item.getTitle() + " selected", Toast.LENGTH_SHORT).show();
                    return true;
                });
                popup.show();
            });
        }

        // 1. Download Syllabus Button
        Button btnDownloadSyllabus = findViewById(R.id.btnDownloadSyllabus);
        if (btnDownloadSyllabus != null) {
            btnDownloadSyllabus.setOnClickListener(v -> {
                NotificationHelper.sendNotification(this, false);
                Toast.makeText(this, "Downloading HSC Biology Syllabus (1.2 MB)...", Toast.LENGTH_SHORT).show();
                openWebUrl("https://dhakaeducationboard.gov.bd");
            });
        }

        // 2. Reference Books
        View cardCampbell = findViewById(R.id.cardCampbellBiology);
        if (cardCampbell != null) {
            cardCampbell.setOnClickListener(v -> {
                Toast.makeText(this, "Opening Campbell Biology Study Module (PDF)", Toast.LENGTH_SHORT).show();
                openWebUrl("https://en.wikipedia.org/wiki/Campbell_Biology");
            });
        }

        View btnDownloadBoardBook = findViewById(R.id.btnDownloadBoardBook);
        if (btnDownloadBoardBook != null) {
            btnDownloadBoardBook.setOnClickListener(v -> {
                NotificationHelper.sendNotification(this, false);
                Toast.makeText(this, "Downloading NCTB Standard Board Textbooks...", Toast.LENGTH_SHORT).show();
                openWebUrl("http://www.nctb.gov.bd");
            });
        }

        // 3. Past Exam Papers
        Button btnViewPast2025 = findViewById(R.id.btnViewPastPaper2025);
        if (btnViewPast2025 != null) {
            btnViewPast2025.setOnClickListener(v -> {
                Toast.makeText(this, "Opening 2025 HSC Biology Board Questions & Solutions...", Toast.LENGTH_SHORT).show();
                openWebUrl("https://dhakaeducationboard.gov.bd");
            });
        }

        Button btnViewPast2024 = findViewById(R.id.btnViewPastPaper2024);
        if (btnViewPast2024 != null) {
            btnViewPast2024.setOnClickListener(v -> {
                Toast.makeText(this, "Opening 2024 HSC Biology Board Questions & Solutions...", Toast.LENGTH_SHORT).show();
                openWebUrl("https://dhakaeducationboard.gov.bd");
            });
        }

        // 4. Useful Websites & Videos
        View btnOpenVisualWebsites = findViewById(R.id.btnOpenVisualWebsites);
        if (btnOpenVisualWebsites != null) {
            btnOpenVisualWebsites.setOnClickListener(v -> {
                Toast.makeText(this, "Opening 3D Cellular Animation & Visual Portal...", Toast.LENGTH_SHORT).show();
                openWebUrl("https://en.wikipedia.org/wiki/Cell_biology");
            });
        }

        Button btnOpenRecommended = findViewById(R.id.btnOpenRecommendedWebsites);
        if (btnOpenRecommended != null) {
            btnOpenRecommended.setOnClickListener(v -> {
                Toast.makeText(this, "Opening Recommended Education Resources...", Toast.LENGTH_SHORT).show();
                openWebUrl("http://www.nctb.gov.bd");
            });
        }

        // Live Search Filter
        EditText etSearch = findViewById(R.id.etSearchResources);
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        Toast.makeText(ResourcesActivity.this, "Searching: " + s, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        // Setup 4-tab bottom navigation
        setupNavigation();
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
     * Topic 3: Launches implicit intent using Intent.ACTION_VIEW and Uri.parse().
     */
    private void openWebUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No browser app available to open: " + url, Toast.LENGTH_SHORT).show();
        }
    }
}
