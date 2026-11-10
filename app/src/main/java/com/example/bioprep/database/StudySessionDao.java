package com.example.bioprep.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StudySessionDao {

    @Insert
    long insertSession(StudySessionEntity session);

    @Insert
    long insert(StudySessionEntity session);

    @Query("SELECT COALESCE(SUM(durationSeconds), 0) FROM study_sessions")
    int getTotalStudySeconds();

    @Query("SELECT COALESCE(SUM(durationSeconds), 0) FROM study_sessions WHERE dateString = :dateString")
    int getStudySecondsForDate(String dateString);

    @Query("SELECT DISTINCT dateString FROM study_sessions ORDER BY dateString ASC")
    List<String> getAllSessionDates();

    @Query("SELECT * FROM study_sessions ORDER BY timestamp DESC")
    List<StudySessionEntity> getAllSessions();

    @Query("SELECT COUNT(*) FROM study_sessions WHERE timestamp = :timestamp AND durationSeconds = :durationSeconds")
    int checkSessionExists(long timestamp, int durationSeconds);

    @Query("DELETE FROM study_sessions")
    void clearAllSessions();
}
