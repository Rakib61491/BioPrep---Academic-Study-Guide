package com.example.bioprep.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.bioprep.R;
import com.example.bioprep.adapters.BioGalleryAdapter;
import com.example.bioprep.data.BioGalleryService;
import com.example.bioprep.models.BioGalleryItem;

import java.util.List;

public class BioGalleryActivity extends AppCompatActivity {

    private RecyclerView rvBioGallery;
    private BioGalleryAdapter adapter;
    private View layoutGalleryLoading;
    private View layoutGalleryError;
    private TextView tvGalleryErrorMessage;
    private TextView tvGallerySubtitle;
    private ImageButton btnGalleryRefresh;
    private final BioGalleryService galleryService = new BioGalleryService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio_gallery);

        initViews();
        loadRandomZoologyImages();
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btnGalleryBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        tvGallerySubtitle = findViewById(R.id.tvGallerySubtitle);
        layoutGalleryLoading = findViewById(R.id.layoutGalleryLoading);
        layoutGalleryError = findViewById(R.id.layoutGalleryError);
        tvGalleryErrorMessage = findViewById(R.id.tvGalleryErrorMessage);

        Button btnRetry = findViewById(R.id.btnGalleryRetry);
        if (btnRetry != null) {
            btnRetry.setOnClickListener(v -> loadRandomZoologyImages());
        }

        btnGalleryRefresh = findViewById(R.id.btnGalleryRefresh);
        if (btnGalleryRefresh != null) {
            btnGalleryRefresh.setOnClickListener(v -> {
                animateRefreshButton();
                loadRandomZoologyImages();
            });
        }

        rvBioGallery = findViewById(R.id.rvBioGallery);
        rvBioGallery.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new BioGalleryAdapter(this::showImageDetailDialog);
        rvBioGallery.setAdapter(adapter);
    }

    private void animateRefreshButton() {
        if (btnGalleryRefresh == null) return;
        RotateAnimation rotate = new RotateAnimation(
                0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotate.setDuration(600);
        btnGalleryRefresh.startAnimation(rotate);
    }

    private void loadRandomZoologyImages() {
        if (layoutGalleryLoading != null) layoutGalleryLoading.setVisibility(View.VISIBLE);
        if (layoutGalleryError != null) layoutGalleryError.setVisibility(View.GONE);
        if (rvBioGallery != null && adapter.getItemCount() == 0) {
            rvBioGallery.setVisibility(View.GONE);
        }

        galleryService.fetchRandomZoologyObservations(new BioGalleryService.GalleryCallback() {
            @Override
            public void onSuccess(List<BioGalleryItem> items) {
                if (isFinishing() || isDestroyed()) return;
                if (layoutGalleryLoading != null) layoutGalleryLoading.setVisibility(View.GONE);
                if (layoutGalleryError != null) layoutGalleryError.setVisibility(View.GONE);
                if (rvBioGallery != null) rvBioGallery.setVisibility(View.VISIBLE);

                adapter.setItems(items);
                if (tvGallerySubtitle != null) {
                    tvGallerySubtitle.setText("Showing " + items.size() + " Random Zoology Species");
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (isFinishing() || isDestroyed()) return;
                if (layoutGalleryLoading != null) layoutGalleryLoading.setVisibility(View.GONE);

                if (adapter.getItemCount() == 0) {
                    if (rvBioGallery != null) rvBioGallery.setVisibility(View.GONE);
                    if (layoutGalleryError != null) layoutGalleryError.setVisibility(View.VISIBLE);
                    if (tvGalleryErrorMessage != null) {
                        tvGalleryErrorMessage.setText(errorMessage);
                    }
                }
            }
        });
    }

    private void showImageDetailDialog(BioGalleryItem item) {
        if (isFinishing() || isDestroyed() || item == null) return;

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_bio_image_detail);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.92),
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        TextView tvCommon = dialog.findViewById(R.id.tvDialogCommonName);
        TextView tvScientific = dialog.findViewById(R.id.tvDialogScientificName);
        TextView tvPlace = dialog.findViewById(R.id.tvDialogPlace);
        TextView tvAttribution = dialog.findViewById(R.id.tvDialogAttribution);
        ImageView ivLargeImage = dialog.findViewById(R.id.ivDialogImage);
        ProgressBar pbLoading = dialog.findViewById(R.id.pbDialogImageLoading);
        View btnClose = dialog.findViewById(R.id.btnDialogClose);
        Button btnDismiss = dialog.findViewById(R.id.btnDialogDismiss);

        if (tvCommon != null) tvCommon.setText(item.getCommonName());
        if (tvScientific != null) tvScientific.setText(item.getScientificName());

        if (tvPlace != null) {
            String place = item.getPlaceGuess();
            if (place != null && !place.trim().isEmpty()) {
                tvPlace.setVisibility(View.VISIBLE);
                tvPlace.setText("📍 " + place);
            } else {
                tvPlace.setVisibility(View.GONE);
            }
        }

        if (tvAttribution != null) {
            String attr = item.getAttribution();
            if (attr != null && !attr.trim().isEmpty()) {
                tvAttribution.setText("📷 " + attr);
            } else {
                tvAttribution.setText("Source: iNaturalist Open Data");
            }
        }

        String targetUrl = item.getLargeImageUrl() != null && !item.getLargeImageUrl().isEmpty()
                ? item.getLargeImageUrl() : item.getImageUrl();

        if (ivLargeImage != null) {
            Glide.with(this)
                    .load(targetUrl)
                    .error(Glide.with(this).load(item.getThumbnailUrl()).fitCenter())
                    .placeholder(R.drawable.bg_card_dark_teal)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    @NonNull Target<Drawable> target, boolean isFirstResource) {
                            if (pbLoading != null) pbLoading.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model,
                                                       Target<Drawable> target, @NonNull DataSource dataSource,
                                                       boolean isFirstResource) {
                            if (pbLoading != null) pbLoading.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(ivLargeImage);
        }

        View.OnClickListener dismissListener = v -> dialog.dismiss();
        if (btnClose != null) btnClose.setOnClickListener(dismissListener);
        if (btnDismiss != null) btnDismiss.setOnClickListener(dismissListener);

        dialog.show();
    }
}
