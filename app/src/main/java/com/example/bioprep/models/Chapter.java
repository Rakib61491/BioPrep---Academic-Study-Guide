package com.example.bioprep.models;

import java.util.Objects;

/**
 * Model representing an HSC Biology chapter.
 */
public class Chapter {
    private final String id;
    private final String paper;
    private final String title;
    private final int chapterNumber;

    public Chapter(String id, String paper, String title, int chapterNumber) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Chapter id cannot be null or empty");
        }
        if (paper == null || paper.trim().isEmpty()) {
            throw new IllegalArgumentException("Chapter paper cannot be null or empty");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Chapter title cannot be null or empty");
        }
        if (chapterNumber <= 0) {
            throw new IllegalArgumentException("Chapter number must be positive");
        }
        this.id = id.trim();
        this.paper = paper.trim();
        this.title = title.trim();
        this.chapterNumber = chapterNumber;
    }

    public String getId() {
        return id;
    }

    public String getPaper() {
        return paper;
    }

    public String getTitle() {
        return title;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chapter chapter = (Chapter) o;
        return chapterNumber == chapter.chapterNumber &&
                Objects.equals(id, chapter.id) &&
                Objects.equals(paper, chapter.paper) &&
                Objects.equals(title, chapter.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, paper, title, chapterNumber);
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "id='" + id + '\'' +
                ", paper='" + paper + '\'' +
                ", title='" + title + '\'' +
                ", chapterNumber=" + chapterNumber +
                '}';
    }
}

