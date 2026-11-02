package com.example.bioprep.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Model representing an MCQ question in BioPrep.
 */
public class Question {
    private final String id;
    private final String paper;
    private final String chapterId;
    private final String topic;
    private final String questionText;
    private final List<String> options;
    private final int correctOptionIndex;
    private final String explanation;
    private final String source;
    private final Integer year;

    public Question(String id,
                    String paper,
                    String chapterId,
                    String topic,
                    String questionText,
                    List<String> options,
                    int correctOptionIndex,
                    String explanation,
                    String source,
                    Integer year) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Question id cannot be null or empty");
        }
        if (questionText == null || questionText.trim().isEmpty()) {
            throw new IllegalArgumentException("Question text cannot be null or empty");
        }
        if (options == null || options.size() != 4) {
            throw new IllegalArgumentException("Question must have exactly four options");
        }
        for (String opt : options) {
            if (opt == null || opt.trim().isEmpty()) {
                throw new IllegalArgumentException("Question option cannot be null or empty");
            }
        }
        if (correctOptionIndex < 0 || correctOptionIndex >= 4) {
            throw new IllegalArgumentException("correctOptionIndex must be between 0 and 3");
        }

        this.id = id.trim();
        this.paper = paper != null ? paper.trim() : "";
        this.chapterId = chapterId != null ? chapterId.trim() : "";
        this.topic = topic != null ? topic.trim() : "";
        this.questionText = questionText.trim();
        this.options = Collections.unmodifiableList(new ArrayList<>(options));
        this.correctOptionIndex = correctOptionIndex;
        this.explanation = explanation != null ? explanation.trim() : "";
        this.source = source != null && !source.trim().isEmpty() ? source.trim() : "Sample";
        this.year = year;
    }

    public String getId() {
        return id;
    }

    public String getPaper() {
        return paper;
    }

    public String getChapterId() {
        return chapterId;
    }

    public String getTopic() {
        return topic;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getSource() {
        return source;
    }

    public Integer getYear() {
        return year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return correctOptionIndex == question.correctOptionIndex &&
                Objects.equals(id, question.id) &&
                Objects.equals(paper, question.paper) &&
                Objects.equals(chapterId, question.chapterId) &&
                Objects.equals(questionText, question.questionText) &&
                Objects.equals(options, question.options);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, paper, chapterId, questionText, options, correctOptionIndex);
    }
}

