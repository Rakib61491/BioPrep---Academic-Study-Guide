package com.example.bioprep.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bioprep.R;

/**
 * Educational Resource Destination Viewer implementing Requirement 7.
 * Shows resource details and handles external navigation to Websites or YouTube
 * with robust fallbacks.
 */
public class ResourceDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_DESC = "extra_desc";
    public static final String EXTRA_URL = "extra_url";
    public static final String EXTRA_ACTION = "extra_action";
    public static final String EXTRA_IS_YOUTUBE = "extra_is_youtube";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_detail);

        ImageButton btnBack = findViewById(R.id.btnDetailBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String desc = getIntent().getStringExtra(EXTRA_DESC);
        String url = getIntent().getStringExtra(EXTRA_URL);
        String actionLabel = getIntent().getStringExtra(EXTRA_ACTION);
        boolean isYouTube = getIntent().getBooleanExtra(EXTRA_IS_YOUTUBE, false);

        TextView tvTitle = findViewById(R.id.tvResourceTitle);
        TextView tvDesc = findViewById(R.id.tvResourceDescription);
        TextView tvUrl = findViewById(R.id.tvResourceUrlDisplay);
        TextView tvIcon = findViewById(R.id.tvResourceIcon);
        TextView tvCat = findViewById(R.id.tvDetailCategory);
        Button btnAction = findViewById(R.id.btnResourceAction);

        if (title != null) tvTitle.setText(title);
        if (desc != null) tvDesc.setText(desc);
        if (url != null) tvUrl.setText(url);

        if (isYouTube) {
            if (tvIcon != null) tvIcon.setText("▶️");
            if (tvCat != null) tvCat.setText("YouTube Video Tutorial");
            if (btnAction != null) {
                btnAction.setText(actionLabel != null ? actionLabel : "Watch on YouTube");
                btnAction.setBackgroundResource(R.drawable.bg_pill_danger);
            }
        } else {
            if (tvIcon != null) tvIcon.setText("🌐");
            if (tvCat != null) tvCat.setText("Educational Website");
            if (btnAction != null) btnAction.setText(actionLabel != null ? actionLabel : "Open Website");
        }

        if (btnAction != null && url != null) {
            btnAction.setOnClickListener(v -> {
                if (isYouTube) {
                    openYouTube(url);
                } else {
                    openBrowser(url);
                }
            });
        }
    }

    private void openYouTube(String url) {
        try {
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            appIntent.setPackage("com.google.android.youtube");
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            // Fallback to browser
            openBrowser(url);
        }
    }

    private void openBrowser(String url) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Could not open browser: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

