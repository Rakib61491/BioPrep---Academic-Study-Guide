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
import java.util.Locale;

/**
 * Custom ListView BaseAdapter for displaying HSC Biology chapters with Stitch styling.
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

    public void updateList(List<Chapter> newChapters) {
        chapters.clear();
        if (newChapters != null) {
            chapters.addAll(newChapters);
        }
        notifyDataSetChanged();
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
            holder.tvChapterSubtitle = convertView.findViewById(R.id.tvChapterSubtitle);
            holder.btnChapterPractice = convertView.findViewById(R.id.btnChapterPractice);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Chapter chapter = getItem(position);
        if (chapter != null) {
            holder.tvChapterNumber.setText(String.format(Locale.getDefault(), "%02d", chapter.getChapterNumber()));
            holder.tvChapterTitle.setText(chapter.getTitle());
            if (holder.tvChapterSubtitle != null) {
                holder.tvChapterSubtitle.setText("🎯 25 MCQs available");
            }
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView tvChapterNumber;
        TextView tvChapterTitle;
        TextView tvChapterSubtitle;
        TextView btnChapterPractice;
    }
}
