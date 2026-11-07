package com.example.bioprep.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bioprep.R;

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

        // Implicit Intent 1: NCTB Biology 1st Paper Web Resource
        Button btnOpenBook1 = findViewById(R.id.btnOpenBook1);
        if (btnOpenBook1 != null) {
            btnOpenBook1.setOnClickListener(v -> openWebUrl("http://www.nctb.gov.bd"));
        }

        // Implicit Intent 2: NCTB Biology 2nd Paper Web Resource
        Button btnOpenBook2 = findViewById(R.id.btnOpenBook2);
        if (btnOpenBook2 != null) {
            btnOpenBook2.setOnClickListener(v -> openWebUrl("http://www.nctb.gov.bd"));
        }

        // Implicit Intent 3: Board Syllabus Link
        Button btnDownloadSyllabus = findViewById(R.id.btnDownloadSyllabus);
        if (btnDownloadSyllabus != null) {
            btnDownloadSyllabus.setOnClickListener(v -> openWebUrl("https://dhakaeducationboard.gov.bd"));
        }

        // Implicit Intent 4: 3D Biology Animation Portal
        Button btnOpenAnimationPortal = findViewById(R.id.btnOpenAnimationPortal);
        if (btnOpenAnimationPortal != null) {
            btnOpenAnimationPortal.setOnClickListener(v -> openWebUrl("https://en.wikipedia.org/wiki/Photosynthesis"));
        }

        // Setup 4-tab bottom navigation
        setupNavigation();
    }

    private void setupNavigation() {
        android.view.View navHome = findViewById(R.id.navTabHome);
        if (navHome != null) navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        android.view.View navStudy = findViewById(R.id.navTabStudy);
        if (navStudy != null) navStudy.setOnClickListener(v ->
                startActivity(new Intent(this, ChapterActivity.class)));
        android.view.View navSaved = findViewById(R.id.navTabSaved);
        if (navSaved != null) navSaved.setOnClickListener(v ->
                startActivity(new Intent(this, SavedItemsActivity.class)));
        android.view.View navProfile = findViewById(R.id.navTabProfile);
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

