package com.example.bioprep.database;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseInitializer {

    private static final String TAG = "BIOPREP_DB_INIT";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface InitCallback {
        void onInitialized();
    }

    public static void initializeDatabaseAsync(final Context context, final InitCallback callback) {
        executor.execute(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(context);

                // 1. Seed Chapters if empty
                if (db.chapterDao().getCount() < 24) {
                    List<ChapterEntity> chapters = loadChaptersFromAsset(context);
                    if (!chapters.isEmpty()) {
                        db.chapterDao().insertAll(chapters);
                        Log.d(TAG, "Seeded " + chapters.size() + " chapters into database.");
                    }
                }

                // 2. Seed Questions if empty
                if (db.questionDao().getCount() < 1200) {
                    List<QuestionEntity> questions = loadQuestionsFromAsset(context);
                    if (!questions.isEmpty()) {
                        db.questionDao().insertAll(questions);
                        Log.d(TAG, "Seeded " + questions.size() + " questions into database.");
                    }
                }

                // 3. Seed Flashcards if empty
                if (db.flashcardDao().getCount() < 600) {
                    List<FlashcardEntity> flashcards = loadFlashcardsFromAsset(context);
                    if (!flashcards.isEmpty()) {
                        db.flashcardDao().insertAll(flashcards);
                        Log.d(TAG, "Seeded " + flashcards.size() + " flashcards into database.");
                    }
                }

                // 4. Seed Resources if empty
                if (db.resourceDao().getCount() < 4) {
                    List<ResourceEntity> defaultResources = new ArrayList<>();
                    defaultResources.add(new ResourceEntity(
                            "res_book_sample",
                            "Sample Biology Books",
                            "Reference Books",
                            "resources/books/sample_biology_book.pdf",
                            "",
                            "1.8 MB",
                            false,
                            0
                    ));
                    defaultResources.add(new ResourceEntity(
                            "res_syllabus_hsc",
                            "Biology Syllabus",
                            "Official Syllabus",
                            "resources/syllabus/hsc_biology_syllabus.pdf",
                            "",
                            "1.2 MB",
                            false,
                            0
                    ));
                    defaultResources.add(new ResourceEntity(
                            "res_test_2025",
                            "2025 Test Papers",
                            "Board Questions",
                            "resources/test_papers/hsc_2025_test_paper.pdf",
                            "",
                            "2.4 MB",
                            false,
                            0
                    ));
                    defaultResources.add(new ResourceEntity(
                            "res_test_2024",
                            "2024 Test Papers",
                            "Board Questions",
                            "resources/test_papers/hsc_2024_test_paper.pdf",
                            "",
                            "2.1 MB",
                            false,
                            0
                    ));
                    db.resourceDao().insertAll(defaultResources);
                    Log.d(TAG, "Seeded default resources into database.");
                }

            } catch (Exception e) {
                Log.e(TAG, "Error initializing database: " + e.getMessage(), e);
            } finally {
                if (callback != null) {
                    callback.onInitialized();
                }
            }
        });
    }

    private static List<ChapterEntity> loadChaptersFromAsset(Context context) {
        List<ChapterEntity> list = new ArrayList<>();
        try {
            String jsonStr = readAssetString(context, "seed/chapters.json");
            JSONArray array = new JSONArray(jsonStr);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                list.add(new ChapterEntity(
                        obj.getString("id"),
                        obj.getString("paper"),
                        obj.getInt("chapterNumber"),
                        obj.getString("titleEn"),
                        obj.getString("titleBn"),
                        obj.optString("icon", "🧬")
                ));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading chapters.json: " + e.getMessage());
        }
        return list;
    }

    private static List<QuestionEntity> loadQuestionsFromAsset(Context context) {
        List<QuestionEntity> list = new ArrayList<>();
        try {
            String jsonStr = readAssetString(context, "seed/questions.json");
            JSONArray array = new JSONArray(jsonStr);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                list.add(new QuestionEntity(
                        obj.getString("id"),
                        obj.getString("chapterId"),
                        obj.getString("paper"),
                        obj.getString("questionText"),
                        obj.getString("optionA"),
                        obj.getString("optionB"),
                        obj.getString("optionC"),
                        obj.getString("optionD"),
                        obj.getInt("correctOptionIndex"),
                        obj.getString("explanation")
                ));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading questions.json: " + e.getMessage());
        }
        return list;
    }

    private static List<FlashcardEntity> loadFlashcardsFromAsset(Context context) {
        List<FlashcardEntity> list = new ArrayList<>();
        try {
            String jsonStr = readAssetString(context, "seed/flashcards.json");
            JSONArray array = new JSONArray(jsonStr);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                list.add(new FlashcardEntity(
                        obj.getString("id"),
                        obj.getString("chapterId"),
                        obj.getString("paper"),
                        obj.getString("questionEn"),
                        obj.getString("questionBn"),
                        obj.getString("answerEn"),
                        obj.getString("answerBn"),
                        obj.getString("medicalTip")
                ));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading flashcards.json: " + e.getMessage());
        }
        return list;
    }

    private static String readAssetString(Context context, String assetPath) throws Exception {
        InputStream is = context.getAssets().open(assetPath);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        return new String(buffer, StandardCharsets.UTF_8);
    }
}

