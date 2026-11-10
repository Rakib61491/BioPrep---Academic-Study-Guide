package com.example.bioprep.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecentActivityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(RecentActivityEntity item);

    @Query("SELECT * FROM recent_activities ORDER BY timestamp DESC")
    List<RecentActivityEntity> getAllRecentActivities();

    @Query("SELECT * FROM recent_activities ORDER BY timestamp DESC LIMIT 5")
    List<RecentActivityEntity> getRecentActivities();

    @Query("SELECT * FROM recent_activities ORDER BY timestamp DESC LIMIT 1")
    RecentActivityEntity getMostRecent();

    @Query("DELETE FROM recent_activities WHERE activityType = :activityType AND chapterId = :chapterId")
    void deleteExisting(String activityType, String chapterId);

    @Query("DELETE FROM recent_activities WHERE id = :id")
    void deleteById(long id);

    @Query("DELETE FROM recent_activities")
    void clearAll();

    @Query("SELECT COUNT(*) FROM recent_activities WHERE activityType = :activityType AND chapterId = :chapterId")
    int checkActivityExists(String activityType, String chapterId);
}
