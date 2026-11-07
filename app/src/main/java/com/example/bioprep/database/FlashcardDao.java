package com.example.bioprep.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FlashcardDao {

    @Query("SELECT * FROM flashcards WHERE chapterId = :chapterId")
    List<FlashcardEntity> getFlashcardsByChapter(String chapterId);

    @Query("SELECT * FROM flashcards WHERE paper = :paper")
    List<FlashcardEntity> getFlashcardsByPaper(String paper);

    @Query("SELECT * FROM flashcards")
    List<FlashcardEntity> getAllFlashcards();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<FlashcardEntity> flashcards);

    @Update
    void updateFlashcard(FlashcardEntity flashcard);

    @Query("SELECT COUNT(*) FROM flashcards WHERE chapterId = :chapterId")
    int getCountByChapter(String chapterId);

    @Query("SELECT COUNT(*) FROM flashcards")
    int getCount();
}

