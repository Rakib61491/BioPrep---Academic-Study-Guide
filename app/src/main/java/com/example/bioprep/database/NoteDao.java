package com.example.bioprep.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    List<NoteEntity> getAllNotes();

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    NoteEntity getNoteById(long id);

    @Insert
    long insertNote(NoteEntity note);

    @Update
    void updateNote(NoteEntity note);

    @Delete
    void deleteNote(NoteEntity note);

    @Query("DELETE FROM notes WHERE id = :id")
    void deleteNoteById(long id);

    @Query("DELETE FROM notes")
    void clearAllNotes();
}

