package com.example.bioprep.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ResourceDao {

    @Query("SELECT * FROM resources")
    List<ResourceEntity> getAllResources();

    @Query("SELECT * FROM resources WHERE id = :id LIMIT 1")
    ResourceEntity getResourceById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ResourceEntity> resources);

    @Update
    void updateResource(ResourceEntity resource);

    @Query("UPDATE resources SET isDownloaded = :isDownloaded, localPath = :localPath, downloadedAt = :downloadedAt WHERE id = :id")
    void updateDownloadStatus(String id, boolean isDownloaded, String localPath, long downloadedAt);

    @Query("SELECT COUNT(*) FROM resources")
    int getCount();
}
