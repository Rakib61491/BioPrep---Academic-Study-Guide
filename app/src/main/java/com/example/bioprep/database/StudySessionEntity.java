package com.example.bioprep.database;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "study_sessions", indices = {@Index("timestamp"), @Index("dateString")})
public class StudySessionEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String sessionType; // "EXAM" or "FLASHCARD"
    private String chapterId;
    private int durationSeconds;
    private long timestamp;
    private String dateString; // "yyyy-MM-dd"

    public StudySessionEntity(String sessionType, String chapterId, int durationSeconds,
                              long timestamp, String dateString) {
        this.sessionType = sessionType;
        this.chapterId = chapterId;
        this.durationSeconds = durationSeconds;
        this.timestamp = timestamp;
        this.dateString = dateString;
    }

    @androidx.room.Ignore
    public StudySessionEntity(String sessionType, String chapterId, int durationSeconds, long timestamp) {
        this(sessionType, chapterId, durationSeconds, timestamp,
                new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(new java.util.Date(timestamp)));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }
}
