package com.example.bioprep.activities;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bioprep.R;
import com.example.bioprep.database.AppDatabase;
import com.example.bioprep.database.ResourceEntity;
import com.example.bioprep.resources.ResourceConfig;
import com.example.bioprep.utils.NotificationHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Resources Activity implementing Requirements 6 & 7:
 * - 4 Downloadable educational resources (Sample Books, Syllabus, 2025 Test Papers, 2024 Test Papers).
 * - Smooth 5-6s download progress simulation with percentage indicators.
 * - Persistent Room DB storage and verification so downloaded files never require re-downloading.
 * - Integration with built-in PdfReaderActivity.
 * - ResourceDetailActivity launch for useful educational websites and YouTube video tutorials.
 */
public class ResourcesActivity extends AppCompatActivity {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // UI elements for Syllabus
    private Button btnDownloadSyllabus;
    private LinearLayout layoutProgressSyllabus;
    private ProgressBar pbSyllabus;
    private TextView tvProgressSyllabus;

    // UI elements for Sample Book
    private Button btnDownloadBoardBook;
    private LinearLayout layoutProgressSampleBook;
    private ProgressBar pbSampleBook;
    private TextView tvProgressSampleBook;

    // UI elements for 2025 Test Papers
    private Button btnViewPastPaper2025;
    private LinearLayout layoutProgress2025;
    private ProgressBar pb2025;
    private TextView tvProgress2025;

    // UI elements for 2024 Test Papers
    private Button btnViewPastPaper2024;
    private LinearLayout layoutProgress2024;
    private ProgressBar pb2024;
    private TextView tvProgress2024;

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

        bindViews();
        setupWebsiteAndVideoClicks();
        setupSearchFilter();
        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAllResourceStates();
    }

    private void bindViews() {
        // Syllabus
        btnDownloadSyllabus = findViewById(R.id.btnDownloadSyllabus);
        layoutProgressSyllabus = findViewById(R.id.layoutProgressSyllabus);
        pbSyllabus = findViewById(R.id.pbSyllabus);
        tvProgressSyllabus = findViewById(R.id.tvProgressSyllabus);

        // Sample Books
        btnDownloadBoardBook = findViewById(R.id.btnDownloadBoardBook);
        layoutProgressSampleBook = findViewById(R.id.layoutProgressSampleBook);
        pbSampleBook = findViewById(R.id.pbSampleBook);
        tvProgressSampleBook = findViewById(R.id.tvProgressSampleBook);

        // 2025 Test Papers
        btnViewPastPaper2025 = findViewById(R.id.btnViewPastPaper2025);
        layoutProgress2025 = findViewById(R.id.layoutProgress2025);
        pb2025 = findViewById(R.id.pb2025);
        tvProgress2025 = findViewById(R.id.tvProgress2025);

        // 2024 Test Papers
        btnViewPastPaper2024 = findViewById(R.id.btnViewPastPaper2024);
        layoutProgress2024 = findViewById(R.id.layoutProgress2024);
        pb2024 = findViewById(R.id.pb2024);
        tvProgress2024 = findViewById(R.id.tvProgress2024);
    }

    private void refreshAllResourceStates() {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            ResourceEntity resSyllabus = db.resourceDao().getResourceById("res_syllabus_hsc");
            ResourceEntity resBook = db.resourceDao().getResourceById("res_book_sample");
            ResourceEntity res2025 = db.resourceDao().getResourceById("res_test_2025");
            ResourceEntity res2024 = db.resourceDao().getResourceById("res_test_2024");

            mainHandler.post(() -> {
                bindResourceSlot(
                        "res_syllabus_hsc",
                        resSyllabus,
                        btnDownloadSyllabus,
                        layoutProgressSyllabus,
                        pbSyllabus,
                        tvProgressSyllabus,
                        "resources/syllabus/hsc_biology_syllabus.pdf",
                        "hsc_biology_syllabus.pdf",
                        "HSC Biology Syllabus"
                );

                bindResourceSlot(
                        "res_book_sample",
                        resBook,
                        btnDownloadBoardBook,
                        layoutProgressSampleBook,
                        pbSampleBook,
                        tvProgressSampleBook,
                        "resources/books/sample_biology_book.pdf",
                        "sample_biology_book.pdf",
                        "Sample Biology Books"
                );

                bindResourceSlot(
                        "res_test_2025",
                        res2025,
                        btnViewPastPaper2025,
                        layoutProgress2025,
                        pb2025,
                        tvProgress2025,
                        "resources/test_papers/hsc_2025_test_paper.pdf",
                        "hsc_2025_test_paper.pdf",
                        "2025 Test Papers"
                );

                bindResourceSlot(
                        "res_test_2024",
                        res2024,
                        btnViewPastPaper2024,
                        layoutProgress2024,
                        pb2024,
                        tvProgress2024,
                        "resources/test_papers/hsc_2024_test_paper.pdf",
                        "hsc_2024_test_paper.pdf",
                        "2024 Test Papers"
                );
            });
        });
    }

    private void bindResourceSlot(
            String resourceId,
            ResourceEntity entity,
            Button actionButton,
            LinearLayout layoutProgress,
            ProgressBar progressBar,
            TextView tvProgress,
            String assetPath,
            String fileName,
            String title
    ) {
        if (actionButton == null) return;

        boolean downloaded = isResourceActuallyDownloaded(entity);

        if (downloaded) {
            actionButton.setText("Open PDF");
            actionButton.setEnabled(true);
            actionButton.setAlpha(1.0f);
            if (layoutProgress != null) layoutProgress.setVisibility(View.GONE);
            actionButton.setOnClickListener(v -> openPdfViewer(entity.getLocalPath(), title));
        } else {
            actionButton.setText("Download");
            actionButton.setEnabled(true);
            actionButton.setAlpha(1.0f);
            if (layoutProgress != null) layoutProgress.setVisibility(View.GONE);
            actionButton.setOnClickListener(v -> startDownloadSimulation(
                    resourceId,
                    title,
                    assetPath,
                    fileName,
                    actionButton,
                    layoutProgress,
                    progressBar,
                    tvProgress
            ));
        }
    }

    private boolean isResourceActuallyDownloaded(ResourceEntity entity) {
        if (entity == null || !entity.isDownloaded() || entity.getLocalPath() == null) {
            return false;
        }
        File file = new File(entity.getLocalPath());
        return file.exists() && file.length() > 0;
    }

    private void startDownloadSimulation(
            String resourceId,
            String title,
            String assetPath,
            String fileName,
            Button actionButton,
            LinearLayout layoutProgress,
            ProgressBar progressBar,
            TextView tvProgress
    ) {
        actionButton.setEnabled(false);
        actionButton.setText("Downloading...");
        actionButton.setAlpha(0.7f);

        if (layoutProgress != null) layoutProgress.setVisibility(View.VISIBLE);
        if (progressBar != null) progressBar.setProgress(0);
        if (tvProgress != null) tvProgress.setText("0%");

        Toast.makeText(this, "Starting download: " + title, Toast.LENGTH_SHORT).show();

        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(5200); // 5.2 seconds
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            int progress = (int) animation.getAnimatedValue();
            if (progressBar != null) progressBar.setProgress(progress);
            if (tvProgress != null) tvProgress.setText(progress + "%");
        });

        animator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                completeDownload(resourceId, title, assetPath, fileName, actionButton, layoutProgress);
            }
        });
        animator.start();
    }

    private void completeDownload(
            String resourceId,
            String title,
            String assetPath,
            String fileName,
            Button actionButton,
            LinearLayout layoutProgress
    ) {
        executor.execute(() -> {
            File targetDir = new File(getFilesDir(), "resources");
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            File destFile = new File(targetDir, fileName);

            try (InputStream in = getAssets().open(assetPath);
                 OutputStream out = new FileOutputStream(destFile)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                out.flush();

                AppDatabase db = AppDatabase.getInstance(this);
                db.resourceDao().updateDownloadStatus(
                        resourceId,
                        true,
                        destFile.getAbsolutePath(),
                        System.currentTimeMillis()
                );

                mainHandler.post(() -> {
                    if (layoutProgress != null) layoutProgress.setVisibility(View.GONE);
                    actionButton.setEnabled(true);
                    actionButton.setAlpha(1.0f);
                    actionButton.setText("Open PDF");
                    actionButton.setOnClickListener(v -> openPdfViewer(destFile.getAbsolutePath(), title));

                    NotificationHelper.sendNotification(this, false);
                    Toast.makeText(ResourcesActivity.this, title + " downloaded successfully!", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (layoutProgress != null) layoutProgress.setVisibility(View.GONE);
                    actionButton.setEnabled(true);
                    actionButton.setAlpha(1.0f);
                    actionButton.setText("Download");
                    Toast.makeText(ResourcesActivity.this, "Download failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void openPdfViewer(String localFilePath, String title) {
        Intent intent = new Intent(this, PdfReaderActivity.class);
        intent.putExtra(PdfReaderActivity.EXTRA_PDF_PATH, localFilePath);
        intent.putExtra(PdfReaderActivity.EXTRA_PDF_TITLE, title);
        startActivity(intent);
    }

    private void setupWebsiteAndVideoClicks() {
        // Visual Websites
        View btnVisual = findViewById(R.id.btnOpenVisualWebsites);
        if (btnVisual != null) {
            btnVisual.setOnClickListener(v -> launchResourceDetail(
                    "3D Cellular Biology Visual Portal",
                    "Interactive 3D microscopic visualization of organelle ultrastructure, eukaryotic membranes, cell organelles, and active transport proteins for HSC Biology.",
                    "https://en.wikipedia.org/wiki/Cell_biology",
                    "Open Visual Portal",
                    false
            ));
        }

        // Recommended Websites
        View btnRecWeb = findViewById(R.id.btnOpenRecommendedWebsites);
        if (btnRecWeb != null) {
            btnRecWeb.setOnClickListener(v -> launchResourceDetail(
                    "NCTB Official Biology Textbook Portal",
                    "Access official national curriculum textbooks, HSC curriculum revisions, syllabi, and guidelines directly from the National Curriculum and Textbook Board.",
                    "http://www.nctb.gov.bd",
                    "Open NCTB Website",
                    false
            ));
        }

        // Campbell Biology card
        View cardCampbell = findViewById(R.id.cardCampbellBiology);
        if (cardCampbell != null) {
            cardCampbell.setOnClickListener(v -> launchResourceDetail(
                    "Campbell Biology Reference Portal",
                    "Benchmark international reference modules covering cellular genetics, physiology, molecular evolution, and competitive medical admission drills.",
                    "https://en.wikipedia.org/wiki/Campbell_Biology",
                    "Explore Campbell Biology",
                    false
            ));
        }

        // Video Lectures
        View cardVideoLectures = findViewById(R.id.cardVideoLectures);
        View btnVideoLectures = findViewById(R.id.btnOpenVideoLectures);
        View.OnClickListener lectureClick = v -> launchResourceDetail(
                "HSC Biology Video Lectures & CQ Solving",
                "Comprehensive Bangla video lecture tutorials covering high-difficulty Botany & Zoology creative questions, mnemonics, and medical admission test drills.",
                "https://www.youtube.com/results?search_query=HSC+Biology+Classes+Bangla",
                "Watch on YouTube",
                true
        );
        if (cardVideoLectures != null) cardVideoLectures.setOnClickListener(lectureClick);
        if (btnVideoLectures != null) btnVideoLectures.setOnClickListener(lectureClick);

        // Diagram Drawings
        View cardVideoDiagrams = findViewById(R.id.cardVideoDiagrams);
        View btnVideoDiagrams = findViewById(R.id.btnOpenVideoDiagrams);
        View.OnClickListener diagramClick = v -> launchResourceDetail(
                "Biology Diagram Drawing & Practical Lab Guides",
                "Step-by-step masterclasses on sketching China rose floral diagrams, heart longitudinal sections, nephrons, and stomatal apparatus for HSC board exams.",
                "https://www.youtube.com/results?search_query=HSC+Biology+Diagram+Drawing+Guide",
                "Watch Drawing Guides",
                true
        );
        if (cardVideoDiagrams != null) cardVideoDiagrams.setOnClickListener(diagramClick);
        if (btnVideoDiagrams != null) btnVideoDiagrams.setOnClickListener(diagramClick);
    }

    private void launchResourceDetail(String title, String desc, String url, String actionLabel, boolean isYouTube) {
        Intent intent = new Intent(this, ResourceDetailActivity.class);
        intent.putExtra(ResourceDetailActivity.EXTRA_TITLE, title);
        intent.putExtra(ResourceDetailActivity.EXTRA_DESC, desc);
        intent.putExtra(ResourceDetailActivity.EXTRA_URL, url);
        intent.putExtra(ResourceDetailActivity.EXTRA_ACTION, actionLabel);
        intent.putExtra(ResourceDetailActivity.EXTRA_IS_YOUTUBE, isYouTube);
        startActivity(intent);
    }

    private void setupSearchFilter() {
        EditText etSearch = findViewById(R.id.etSearchResources);
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterResources(s.toString().trim().toLowerCase());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void filterResources(String query) {
        View cardSyllabus = findViewById(R.id.cardSyllabus);
        View cardCampbell = findViewById(R.id.cardCampbellBiology);
        View cardBoard = findViewById(R.id.cardBoardTextbooks);
        View card2025 = findViewById(R.id.btnViewPastPaper2025) != null ? (View) findViewById(R.id.btnViewPastPaper2025).getParent() : null;
        View card2024 = findViewById(R.id.btnViewPastPaper2024) != null ? (View) findViewById(R.id.btnViewPastPaper2024).getParent() : null;

        if (query.isEmpty()) {
            if (cardSyllabus != null) cardSyllabus.setVisibility(View.VISIBLE);
            if (cardCampbell != null) cardCampbell.setVisibility(View.VISIBLE);
            if (cardBoard != null) cardBoard.setVisibility(View.VISIBLE);
            if (card2025 != null) card2025.setVisibility(View.VISIBLE);
            if (card2024 != null) card2024.setVisibility(View.VISIBLE);
            return;
        }

        if (cardSyllabus != null) {
            cardSyllabus.setVisibility("syllabus guides curriculum".contains(query) ? View.VISIBLE : View.GONE);
        }
        if (cardCampbell != null) {
            cardCampbell.setVisibility("campbell biology reference book".contains(query) ? View.VISIBLE : View.GONE);
        }
        if (cardBoard != null) {
            cardBoard.setVisibility("board textbooks sample nctb standard".contains(query) ? View.VISIBLE : View.GONE);
        }
        if (card2025 != null) {
            card2025.setVisibility("2025 past papers test questions board".contains(query) ? View.VISIBLE : View.GONE);
        }
        if (card2024 != null) {
            card2024.setVisibility("2024 past papers test questions board".contains(query) ? View.VISIBLE : View.GONE);
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
