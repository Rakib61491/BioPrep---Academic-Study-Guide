package com.example.bioprep.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface QuestionDao {

    @Query("SELECT * FROM questions WHERE chapterId = :chapterId")
    List<QuestionEntity> getQuestionsByChapter(String chapterId);

    @Query("SELECT * FROM questions WHERE paper = :paper")
    List<QuestionEntity> getQuestionsByPaper(String paper);

    @Query("SELECT * FROM questions")
    List<QuestionEntity> getAllQuestions();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<QuestionEntity> questions);

    @Query("SELECT COUNT(*) FROM questions WHERE chapterId = :chapterId")
    int getCountByChapter(String chapterId);

    @Query("SELECT COUNT(*) FROM questions")
    int getCount();
}

