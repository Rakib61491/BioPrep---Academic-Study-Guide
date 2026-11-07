package com.example.bioprep.database;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "practice_attempts", indices = {@Index("chapterId"), @Index("timestamp")})
public class PracticeAttemptEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String chapterId;
    private String paperId;
    private String chapterTitle;
    private int totalQuestions;
    private int correctCount;
    private int incorrectCount;
    private int unansweredCount;
    private int accuracyPercent;
    private int timeSpentSeconds;
    private long timestamp;

    public PracticeAttemptEntity(String chapterId, String paperId, String chapterTitle,
                                 int totalQuestions, int correctCount, int incorrectCount,
                                 int unansweredCount, int accuracyPercent,
                                 int timeSpentSeconds, long timestamp) {
        this.chapterId = chapterId;
        this.paperId = paperId;
        this.chapterTitle = chapterTitle;
        this.totalQuestions = totalQuestions;
        this.correctCount = correctCount;
        this.incorrectCount = incorrectCount;
        this.unansweredCount = unansweredCount;
        this.accuracyPercent = accuracyPercent;
        this.timeSpentSeconds = timeSpentSeconds;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getPaperId() {
        return paperId;
    }

    public void setPaperId(String paperId) {
        this.paperId = paperId;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public int getIncorrectCount() {
        return incorrectCount;
    }

    public void setIncorrectCount(int incorrectCount) {
        this.incorrectCount = incorrectCount;
    }

    public int getUnansweredCount() {
        return unansweredCount;
    }

    public void setUnansweredCount(int unansweredCount) {
        this.unansweredCount = unansweredCount;
    }

    public int getAccuracyPercent() {
        return accuracyPercent;
    }

    public void setAccuracyPercent(int accuracyPercent) {
        this.accuracyPercent = accuracyPercent;
    }

    public int getTimeSpentSeconds() {
        return timeSpentSeconds;
    }

    public void setTimeSpentSeconds(int timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

