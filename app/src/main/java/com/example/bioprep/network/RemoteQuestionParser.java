package com.example.bioprep.network;

import androidx.core.text.HtmlCompat;

import com.example.bioprep.models.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Parses JSON responses from the Open Trivia Database (OpenTDB) into BioPrep Question models.
 */
public final class RemoteQuestionParser {

    public static final String SOURCE_REMOTE = "OpenTDB Science & Nature";

    private RemoteQuestionParser() {
        // Utility
    }

    /**
     * Parses an OpenTDB JSON string response into a validated Question object.
     *
     * @param jsonResponse the raw JSON response from the server
     * @return a validated Question instance with 4 shuffled options
     * @throws IllegalArgumentException if JSON is missing, malformed, or has an API error code
     */
    public static Question parseQuestion(String jsonResponse) throws IllegalArgumentException {
        return parseQuestion(jsonResponse, new Random());
    }

    /**
     * Overload accepting a Random instance for deterministic shuffle testing.
     */
    public static Question parseQuestion(String jsonResponse, Random random) throws IllegalArgumentException {
        if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
            throw new IllegalArgumentException("Response JSON cannot be empty or null");
        }

        try {
            JSONObject root = new JSONObject(jsonResponse);

            if (!root.has("response_code")) {
                throw new IllegalArgumentException("Missing response_code in JSON response");
            }
            int responseCode = root.getInt("response_code");
            if (responseCode != 0) {
                throw new IllegalArgumentException("OpenTDB API returned non-zero response_code: " + responseCode);
            }

            if (!root.has("results")) {
                throw new IllegalArgumentException("Missing results array in JSON response");
            }
            JSONArray results = root.getJSONArray("results");
            if (results.length() == 0) {
                throw new IllegalArgumentException("Results array is empty");
            }

            JSONObject item = results.getJSONObject(0);

            if (!item.has("question") || !item.has("correct_answer") || !item.has("incorrect_answers")) {
                throw new IllegalArgumentException("Question item missing required fields");
            }

            String rawQuestion = item.getString("question");
            String rawCorrect = item.getString("correct_answer");
            JSONArray rawIncorrect = item.getJSONArray("incorrect_answers");

            if (rawIncorrect.length() != 3) {
                throw new IllegalArgumentException("Expected exactly 3 incorrect answers, found: " + rawIncorrect.length());
            }

            String topic = item.optString("category", "Science & Nature");
            String difficulty = item.optString("difficulty", "");

            // Decode HTML entities
            String decodedQuestion = decodeHtml(rawQuestion);
            String decodedCorrect = decodeHtml(rawCorrect);

            List<String> options = new ArrayList<>(4);
            options.add(decodedCorrect);
            for (int i = 0; i < 3; i++) {
                String wrongOpt = rawIncorrect.getString(i);
                options.add(decodeHtml(wrongOpt));
            }

            // Shuffle options
            if (random != null) {
                Collections.shuffle(options, random);
            } else {
                Collections.shuffle(options);
            }

            // Re-identify correct option index in the shuffled list
            int correctIndex = options.indexOf(decodedCorrect);
            if (correctIndex < 0 || correctIndex > 3) {
                throw new IllegalStateException("Failed to locate correct answer in shuffled options");
            }

            String questionId = "remote_" + System.currentTimeMillis();
            String topicLabel = topic;
            if (!difficulty.isEmpty()) {
                topicLabel = topic + " (" + difficulty + ")";
            }

            return new Question(
                    questionId,
                    "",
                    "",
                    topicLabel,
                    decodedQuestion,
                    options,
                    correctIndex,
                    "Correct answer: " + decodedCorrect,
                    SOURCE_REMOTE,
                    null
            );

        } catch (JSONException e) {
            throw new IllegalArgumentException("Malformed JSON response: " + e.getMessage(), e);
        }
    }

    private static String decodeHtml(String html) {
        if (html == null) return "";
        try {
            return HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY).toString().trim();
        } catch (Throwable t) {
            return html.replace("&quot;", "\"")
                    .replace("&apos;", "'")
                    .replace("&#039;", "'")
                    .replace("&amp;", "&")
                    .replace("&lt;", "<")
                    .replace("&gt;", ">")
                    .replaceAll("<[^>]*>", "")
                    .trim();
        }
    }
}
