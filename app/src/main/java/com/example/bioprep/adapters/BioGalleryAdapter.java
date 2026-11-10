package com.example.bioprep.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.bioprep.R;
import com.example.bioprep.models.BioGalleryItem;

import java.util.ArrayList;
import java.util.List;

public class BioGalleryAdapter extends RecyclerView.Adapter<BioGalleryAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(BioGalleryItem item);
    }

    private final List<BioGalleryItem> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public BioGalleryAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<BioGalleryItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bio_gallery_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BioGalleryItem item = items.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivGalleryImage;
        private final TextView tvAnimalCommonName;
        private final TextView tvAnimalScientificName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGalleryImage = itemView.findViewById(R.id.ivGalleryImage);
            tvAnimalCommonName = itemView.findViewById(R.id.tvAnimalCommonName);
            tvAnimalScientificName = itemView.findViewById(R.id.tvAnimalScientificName);
        }

        void bind(BioGalleryItem item, OnItemClickListener listener) {
            tvAnimalCommonName.setText(item.getCommonName());
            tvAnimalScientificName.setText(item.getScientificName());

            Context context = itemView.getContext();
            Glide.with(context)
                    .load(item.getImageUrl())
                    .error(Glide.with(context).load(item.getThumbnailUrl()).centerCrop())
                    .placeholder(R.drawable.bg_card_dark_teal)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(ivGalleryImage);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
