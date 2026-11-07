package com.example.bioprep.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "resources")
public class ResourceEntity {

    @PrimaryKey
    @NonNull
    private String id;
    private String title;
    private String category;
    private String assetPath;
    private String localPath;
    private String fileSize;
    private boolean isDownloaded;
    private long downloadedAt;

    public ResourceEntity(@NonNull String id, String title, String category, String assetPath,
                          String localPath, String fileSize, boolean isDownloaded, long downloadedAt) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.assetPath = assetPath;
        this.localPath = localPath;
        this.fileSize = fileSize;
        this.isDownloaded = isDownloaded;
        this.downloadedAt = downloadedAt;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAssetPath() {
        return assetPath;
    }

    public void setAssetPath(String assetPath) {
        this.assetPath = assetPath;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public long getDownloadedAt() {
        return downloadedAt;
    }

    public void setDownloadedAt(long downloadedAt) {
        this.downloadedAt = downloadedAt;
    }
}

