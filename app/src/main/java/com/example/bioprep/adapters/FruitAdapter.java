package com.example.bioprep.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bioprep.R;

/**
 * Custom ListView Adapter conforming to Lab Manual Experiment C.
 */
public class FruitAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final String[] names;
    private final String[] descriptions;

    public FruitAdapter(@NonNull Context context, @NonNull String[] names, @NonNull String[] descriptions) {
        super(context, R.layout.item_fruit, names);
        this.context = context;
        this.names = names;
        this.descriptions = descriptions;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            row = LayoutInflater.from(context).inflate(R.layout.item_fruit, parent, false);
        }

        TextView txtName = row.findViewById(R.id.txtName);
        TextView txtDescription = row.findViewById(R.id.txtDescription);

        if (txtName != null && position < names.length) {
            txtName.setText(names[position]);
        }
        if (txtDescription != null && position < descriptions.length) {
            txtDescription.setText(descriptions[position]);
        }

        return row;
    }
}

