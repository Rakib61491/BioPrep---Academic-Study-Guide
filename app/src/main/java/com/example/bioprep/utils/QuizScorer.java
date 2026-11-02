package com.example.bioprep.utils;

import com.example.bioprep.models.Question;
import com.example.bioprep.models.QuizResult;

import java.util.List;
import java.util.Map;

/**
 * Pure Java utility for calculating quiz scores.
 */
public final class QuizScorer {

    private QuizScorer() {
        // Utility class
    }

    /**
     * Calculates the QuizResult given a list of questions and the user's selected answers map.
     *
     * @param questions       the list of questions
     * @param selectedAnswers map from questionId -> selectedOptionIndex (0-3)
     * @return the calculated QuizResult
     */
    public static QuizResult score(List<Question> questions, Map<String, Integer> selectedAnswers) {
        if (questions == null || questions.isEmpty()) {
            return new QuizResult(0, 0, 0, 0, 0.0);
        }

        int total = questions.size();
        int correct = 0;
        int incorrect = 0;
        int unanswered = 0;

        for (Question q : questions) {
            if (q == null) {
                unanswered++;
                continue;
            }
            Integer selectedIndex = selectedAnswers != null ? selectedAnswers.get(q.getId()) : null;
            if (selectedIndex == null || selectedIndex < 0 || selectedIndex > 3) {
                unanswered++;
            } else if (selectedIndex == q.getCorrectOptionIndex()) {
                correct++;
            } else {
                incorrect++;
            }
        }

        double percentage = total > 0 ? ((double) correct / (double) total) * 100.0 : 0.0;
        return new QuizResult(total, correct, incorrect, unanswered, percentage);
    }
}

