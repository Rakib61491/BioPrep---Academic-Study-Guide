package com.example.bioprep.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {
        ChapterEntity.class,
        QuestionEntity.class,
        FlashcardEntity.class,
        PracticeAttemptEntity.class,
        PracticeAnswerEntity.class,
        StudySessionEntity.class,
        NoteEntity.class,
        ResourceEntity.class,
        UserAccountEntity.class
}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "bioprep_database.db";
    private static volatile AppDatabase INSTANCE;

    public abstract ChapterDao chapterDao();
    public abstract QuestionDao questionDao();
    public abstract FlashcardDao flashcardDao();
    public abstract PracticeDao practiceDao();
    public abstract StudySessionDao studySessionDao();
    public abstract NoteDao noteDao();
    public abstract ResourceDao resourceDao();
    public abstract UserDao userDao();

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

