package com.example.bioprep.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bioprep.R;

/**
 * RecyclerView Adapter conforming to Lab Manual Experiment D.
 */
public class FruitRecyclerAdapter extends RecyclerView.Adapter<FruitRecyclerAdapter.FruitViewHolder> {

    private final String[] names;
    private final String[] descriptions;

    public FruitRecyclerAdapter(String[] names, String[] descriptions) {
        this.names = names != null ? names : new String[0];
        this.descriptions = descriptions != null ? descriptions : new String[0];
    }

    @NonNull
    @Override
    public FruitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fruit, parent, false);
        return new FruitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FruitViewHolder holder, int position) {
        String name = position < names.length ? names[position] : "";
        String desc = position < descriptions.length ? descriptions[position] : "";

        if (holder.txtName != null) {
            holder.txtName.setText(name);
        }
        if (holder.txtDescription != null) {
            holder.txtDescription.setText(desc);
        }

        holder.itemView.setOnClickListener(v -> {
            // Item clicked
        });
    }

    @Override
    public int getItemCount() {
        return names.length;
    }

    public static class FruitViewHolder extends RecyclerView.ViewHolder {
        public final TextView txtName;
        public final TextView txtDescription;

        public FruitViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtDescription = itemView.findViewById(R.id.txtDescription);
        }
    }
}

