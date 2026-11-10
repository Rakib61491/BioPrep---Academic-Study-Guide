package com.example.bioprep.utils;

import android.content.Context;
import com.example.bioprep.database.AppDatabase;
import com.example.bioprep.database.RecentActivityEntity;

import java.util.concurrent.Executors;

public class RecentActivityHelper {

    public static final String TYPE_ACTIVE_RECALL = "ACTIVE_RECALL";
    public static final String TYPE_FLASHCARD = "FLASHCARD";
    public static final String TYPE_EXAM_PRACTICE = "EXAM_PRACTICE";

    public static void recordActivity(Context context, String activityType, String chapterId, String chapterTitle, String paperId) {
        if (context == null) return;
        Context appCtx = context.getApplicationContext();
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(appCtx);
                if (chapterId != null) {
                    db.recentActivityDao().deleteExisting(activityType, chapterId);
                }
                RecentActivityEntity entity = new RecentActivityEntity(
                        activityType,
                        chapterId != null ? chapterId : "botany_ch01",
                        chapterTitle != null ? chapterTitle : "HSC Biology",
                        paperId != null ? paperId : "PAPER_1",
                        System.currentTimeMillis()
                );
                db.recentActivityDao().insert(entity);
            } catch (Exception ignored) {}
        });
    }

    /**
     * Formats elapsed time into clean relative formats:
     * "Just now", "3m ago", "5m ago", "2h ago", "Yesterday", "3d ago".
     */
    public static String formatRelativeTime(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        if (diff < 0) diff = 0;

        long mins = diff / (60 * 1000);
        long hours = mins / 60;
        long days = hours / 24;

        if (mins < 1) {
            return "Just now";
        } else if (mins < 60) {
            return mins + (mins == 1 ? " min ago" : " mins ago");
        } else if (hours < 24) {
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        } else if (days == 1) {
            return "Yesterday";
        } else {
            return days + " days ago";
        }
    }
}
