package com.example.bioprep.models;

import java.util.Objects;

/**
 * Model representing the summary result of a completed quiz.
 */
public class QuizResult {
    private final int totalQuestions;
    private final int correctCount;
    private final int incorrectCount;
    private final int unansweredCount;
    private final double scorePercentage;

    public QuizResult(int totalQuestions, int correctCount, int incorrectCount, int unansweredCount, double scorePercentage) {
        if (totalQuestions < 0 || correctCount < 0 || incorrectCount < 0 || unansweredCount < 0) {
            throw new IllegalArgumentException("Question counts cannot be negative");
        }
        if (totalQuestions != correctCount + incorrectCount + unansweredCount) {
            throw new IllegalArgumentException("totalQuestions must equal correct + incorrect + unanswered");
        }
        this.totalQuestions = totalQuestions;
        this.correctCount = correctCount;
        this.incorrectCount = incorrectCount;
        this.unansweredCount = unansweredCount;
        this.scorePercentage = scorePercentage;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public int getIncorrectCount() {
        return incorrectCount;
    }

    public int getUnansweredCount() {
        return unansweredCount;
    }

    public double getScorePercentage() {
        return scorePercentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuizResult that = (QuizResult) o;
        return totalQuestions == that.totalQuestions &&
                correctCount == that.correctCount &&
                incorrectCount == that.incorrectCount &&
                unansweredCount == that.unansweredCount &&
                Double.compare(that.scorePercentage, scorePercentage) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalQuestions, correctCount, incorrectCount, unansweredCount, scorePercentage);
    }

    @Override
    public String toString() {
        return "QuizResult{" +
                "totalQuestions=" + totalQuestions +
                ", correctCount=" + correctCount +
                ", incorrectCount=" + incorrectCount +
                ", unansweredCount=" + unansweredCount +
                ", scorePercentage=" + scorePercentage +
                '}';
    }
}

