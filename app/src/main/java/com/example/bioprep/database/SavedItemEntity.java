package com.example.bioprep.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "saved_items")
public class SavedItemEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String type; // FLASHCARD, MCQ, ACTIVE_RECALL, NOTE
    private String itemId;
    private String title;
    private String content;
    private String extraInfo;
    private long createdAt;

    public SavedItemEntity(String type, String itemId, String title, String content, String extraInfo, long createdAt) {
        this.type = type;
        this.itemId = itemId;
        this.title = title;
        this.content = content;
        this.extraInfo = extraInfo;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}

