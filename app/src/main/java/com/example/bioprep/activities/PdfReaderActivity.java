package com.example.bioprep.activities;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bioprep.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Built-in PDF Reader Activity implementing Requirement 6:
 * - Displays actual PDF documents.
 * - Supports continuous vertical scrolling through multiple pages.
 * - Renders each page clearly using Android's native PdfRenderer.
 * - Manages memory dynamically with LruCache without loading all pages into memory simultaneously.
 * - Back navigation option.
 */
public class PdfReaderActivity extends AppCompatActivity {

    public static final String EXTRA_PDF_PATH = "extra_pdf_path";
    public static final String EXTRA_PDF_TITLE = "extra_pdf_title";
    public static final String EXTRA_PDF_ASSET = "extra_pdf_asset";

    private TextView tvPdfDocumentTitle;
    private TextView tvPdfPageBadge;
    private RecyclerView rvPdfPages;
    private ProgressBar pbPdfLoading;

    private ParcelFileDescriptor fileDescriptor;
    private PdfRenderer pdfRenderer;
    private PdfPageAdapter adapter;

    private final ExecutorService renderExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private LruCache<Integer, Bitmap> pageCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_reader);

        tvPdfDocumentTitle = findViewById(R.id.tvPdfDocumentTitle);
        tvPdfPageBadge = findViewById(R.id.tvPdfPageBadge);
        rvPdfPages = findViewById(R.id.rvPdfPages);
        pbPdfLoading = findViewById(R.id.pbPdfLoading);

        ImageButton btnBack = findViewById(R.id.btnPdfBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        String title = getIntent().getStringExtra(EXTRA_PDF_TITLE);
        if (title != null && !title.isEmpty()) {
            tvPdfDocumentTitle.setText(title);
        }

        // Cache 8 full-page bitmaps in memory
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        pageCache = new LruCache<Integer, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(Integer key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };

        rvPdfPages.setLayoutManager(new LinearLayoutManager(this));

        openPdfDocument();
    }

    private void openPdfDocument() {
        pbPdfLoading.setVisibility(View.VISIBLE);

        renderExecutor.execute(() -> {
            try {
                String localPath = getIntent().getStringExtra(EXTRA_PDF_PATH);
                String assetPath = getIntent().getStringExtra(EXTRA_PDF_ASSET);

                File targetFile = null;

                if (localPath != null && !localPath.isEmpty()) {
                    File f = new File(localPath);
                    if (f.exists()) targetFile = f;
                }

                if (targetFile == null && assetPath != null && !assetPath.isEmpty()) {
                    // Extract from assets to cache
                    File cacheFile = new File(getCacheDir(), "view_" + new File(assetPath).getName());
                    InputStream is = getAssets().open(assetPath);
                    FileOutputStream fos = new FileOutputStream(cacheFile);
                    byte[] buffer = new byte[4096];
                    int read;
                    while ((read = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, read);
                    }
                    fos.close();
                    is.close();
                    targetFile = cacheFile;
                }

                if (targetFile == null || !targetFile.exists()) {
                    mainHandler.post(() -> {
                        pbPdfLoading.setVisibility(View.GONE);
                        Toast.makeText(this, "Could not locate PDF file.", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                    return;
                }

                fileDescriptor = ParcelFileDescriptor.open(targetFile, ParcelFileDescriptor.MODE_READ_ONLY);
                pdfRenderer = new PdfRenderer(fileDescriptor);
                final int pageCount = pdfRenderer.getPageCount();

                mainHandler.post(() -> {
                    pbPdfLoading.setVisibility(View.GONE);
                    adapter = new PdfPageAdapter(pageCount);
                    rvPdfPages.setAdapter(adapter);
                    updatePageBadge(1, pageCount);

                    rvPdfPages.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                            if (lm != null) {
                                int firstVisible = lm.findFirstVisibleItemPosition();
                                updatePageBadge(firstVisible + 1, pageCount);
                            }
                        }
                    });
                });

            } catch (Exception e) {
                mainHandler.post(() -> {
                    pbPdfLoading.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to open PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void updatePageBadge(int current, int total) {
        if (tvPdfPageBadge != null) {
            tvPdfPageBadge.setText(current + " / " + total);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        renderExecutor.execute(() -> {
            try {
                if (pdfRenderer != null) {
                    pdfRenderer.close();
                }
                if (fileDescriptor != null) {
                    fileDescriptor.close();
                }
            } catch (Exception ignored) {}
        });
        renderExecutor.shutdown();
    }

    // =========================================================================
    // RecyclerView Page Adapter with On-Demand Rendering
    // =========================================================================
    private class PdfPageAdapter extends RecyclerView.Adapter<PdfPageAdapter.PageViewHolder> {

        private final int totalPages;

        public PdfPageAdapter(int totalPages) {
            this.totalPages = totalPages;
        }

        @NonNull
        @Override
        public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pdf_page, parent, false);
            return new PageViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return totalPages;
        }

        class PageViewHolder extends RecyclerView.ViewHolder {
            final ImageView ivPdfPage;
            final ProgressBar pbPageLoading;
            int boundPosition = -1;

            PageViewHolder(@NonNull View itemView) {
                super(itemView);
                ivPdfPage = itemView.findViewById(R.id.ivPdfPage);
                pbPageLoading = itemView.findViewById(R.id.pbPageLoading);
            }

            void bind(final int pageIndex) {
                boundPosition = pageIndex;

                Bitmap cached = pageCache.get(pageIndex);
                if (cached != null) {
                    ivPdfPage.setImageBitmap(cached);
                    pbPageLoading.setVisibility(View.GONE);
                    return;
                }

                ivPdfPage.setImageDrawable(null);
                pbPageLoading.setVisibility(View.VISIBLE);

                renderExecutor.execute(() -> {
                    if (pdfRenderer == null) return;

                    try {
                        PdfRenderer.Page page;
                        synchronized (pdfRenderer) {
                            page = pdfRenderer.openPage(pageIndex);
                        }

                        // Scale for sharp high-DPI rendering
                        int screenWidth = getResources().getDisplayMetrics().widthPixels - 48;
                        int width = Math.max(page.getWidth() * 2, screenWidth);
                        int height = (int) (((float) width / page.getWidth()) * page.getHeight());

                        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        bitmap.eraseColor(0xFFFFFFFF); // White background

                        synchronized (pdfRenderer) {
                            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                            page.close();
                        }

                        pageCache.put(pageIndex, bitmap);

                        mainHandler.post(() -> {
                            if (boundPosition == pageIndex) {
                                ivPdfPage.setImageBitmap(bitmap);
                                pbPageLoading.setVisibility(View.GONE);
                            }
                        });
                    } catch (Exception e) {
                        mainHandler.post(() -> pbPageLoading.setVisibility(View.GONE));
                    }
                });
            }
        }
    }
}

