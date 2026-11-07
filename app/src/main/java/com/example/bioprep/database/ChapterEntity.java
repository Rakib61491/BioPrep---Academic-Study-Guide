package com.example.bioprep.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chapters")
public class ChapterEntity {

    @PrimaryKey
    @NonNull
    private String id;
    private String paper; // "BOTANY" or "ZOOLOGY"
    private int chapterNumber;
    private String titleEn;
    private String titleBn;
    private String icon;

    public ChapterEntity(@NonNull String id, String paper, int chapterNumber, String titleEn, String titleBn, String icon) {
        this.id = id;
        this.paper = paper;
        this.chapterNumber = chapterNumber;
        this.titleEn = titleEn;
        this.titleBn = titleBn;
        this.icon = icon;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getPaper() {
        return paper;
    }

    public void setPaper(String paper) {
        this.paper = paper;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getTitleBn() {
        return titleBn;
    }

    public void setTitleBn(String titleBn) {
        this.titleBn = titleBn;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}

