package com.example.bioprep.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PracticeDao {

    @Insert
    long insertAttempt(PracticeAttemptEntity attempt);

    @Insert
    void insertAnswers(List<PracticeAnswerEntity> answers);

    @Query("SELECT * FROM practice_attempts WHERE chapterId = :chapterId ORDER BY timestamp DESC LIMIT 1")
    PracticeAttemptEntity getLatestAttemptForChapter(String chapterId);

    @Query("SELECT * FROM practice_attempts ORDER BY timestamp DESC")
    List<PracticeAttemptEntity> getAllAttempts();

    @Query("SELECT * FROM practice_attempts WHERE id = :id LIMIT 1")
    PracticeAttemptEntity getAttemptById(long id);

    @Query("SELECT * FROM practice_answers WHERE attemptId = :attemptId")
    List<PracticeAnswerEntity> getAnswersForAttempt(long attemptId);

    @Query("SELECT COALESCE(SUM(correctCount), 0) FROM practice_attempts")
    int getTotalCorrectAnswers();

    @Query("SELECT COALESCE(SUM(correctCount + incorrectCount), 0) FROM practice_attempts")
    int getTotalAnsweredQuestions();

    @Query("SELECT COALESCE(SUM(correctCount + incorrectCount), 0) FROM practice_attempts WHERE timestamp >= :sinceMillis")
    int getQuestionsAnsweredSince(long sinceMillis);

    @Query("SELECT COUNT(*) FROM practice_attempts WHERE timestamp >= :sinceMillis")
    int getCompletedQuizCountSince(long sinceMillis);

    @Query("SELECT COUNT(*) FROM practice_attempts")
    int getCompletedQuizCount();

    @Query("DELETE FROM practice_attempts")
    void clearAllAttempts();

    @Query("DELETE FROM practice_answers")
    void clearAllAnswers();
}

