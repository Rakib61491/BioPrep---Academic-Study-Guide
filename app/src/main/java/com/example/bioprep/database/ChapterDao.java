package com.example.bioprep.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChapterDao {

    @Query("SELECT * FROM chapters ORDER BY chapterNumber ASC")
    List<ChapterEntity> getAllChapters();

    @Query("SELECT * FROM chapters WHERE paper = :paper ORDER BY chapterNumber ASC")
    List<ChapterEntity> getChaptersByPaper(String paper);

    @Query("SELECT * FROM chapters WHERE id = :id LIMIT 1")
    ChapterEntity getChapterById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ChapterEntity> chapters);

    @Query("SELECT COUNT(*) FROM chapters")
    int getCount();
}

