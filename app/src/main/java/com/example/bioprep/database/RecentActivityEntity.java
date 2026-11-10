package com.example.bioprep.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recent_activities")
public class RecentActivityEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String activityType; // ACTIVE_RECALL, FLASHCARD, EXAM_PRACTICE
    private String chapterId;
    private String chapterTitle;
    private String paperId;
    private long timestamp;

    public RecentActivityEntity(String activityType, String chapterId, String chapterTitle, String paperId, long timestamp) {
        this.activityType = activityType;
        this.chapterId = chapterId;
        this.chapterTitle = chapterTitle;
        this.paperId = paperId;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public String getPaperId() {
        return paperId;
    }

    public void setPaperId(String paperId) {
        this.paperId = paperId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

