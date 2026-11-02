package com.example.bioprep.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.bioprep.R;
import com.example.bioprep.models.Chapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom ListView BaseAdapter for displaying HSC Biology chapters.
 */
public class ChapterAdapter extends BaseAdapter {

    private final Context context;
    private final List<Chapter> chapters;
    private final LayoutInflater inflater;

    public ChapterAdapter(Context context, List<Chapter> chapters) {
        this.context = context;
        this.chapters = chapters != null ? new ArrayList<>(chapters) : new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return chapters.size();
    }

    @Override
    public Chapter getItem(int position) {
        if (position >= 0 && position < chapters.size()) {
            return chapters.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_chapter, parent, false);
            holder = new ViewHolder();
            holder.tvChapterNumber = convertView.findViewById(R.id.tvChapterNumber);
            holder.tvChapterTitle = convertView.findViewById(R.id.tvChapterTitle);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Chapter chapter = getItem(position);
        if (chapter != null) {
            holder.tvChapterNumber.setText(String.valueOf(chapter.getChapterNumber()));
            holder.tvChapterTitle.setText(chapter.getTitle());
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView tvChapterNumber;
        TextView tvChapterTitle;
    }
}

