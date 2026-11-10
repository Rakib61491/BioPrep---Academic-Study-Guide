package com.example.bioprep.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SavedItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(SavedItemEntity item);

    @Delete
    void delete(SavedItemEntity item);

    @Query("DELETE FROM saved_items WHERE type = :type AND itemId = :itemId")
    void deleteByTypeAndItemId(String type, String itemId);

    @Query("SELECT * FROM saved_items ORDER BY createdAt DESC")
    List<SavedItemEntity> getAllSavedItems();

    @Query("SELECT * FROM saved_items WHERE type = :type ORDER BY createdAt DESC")
    List<SavedItemEntity> getSavedItemsByType(String type);

    @Query("SELECT COUNT(*) FROM saved_items WHERE type = :type AND itemId = :itemId")
    int isItemSaved(String type, String itemId);

    @Query("DELETE FROM saved_items")
    void clearAll();
}

