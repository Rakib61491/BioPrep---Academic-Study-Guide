package com.example.bioprep.data;

import com.example.bioprep.models.Chapter;
import com.example.bioprep.models.Question;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SampleBiologyDataTest {

    @Test
    public void testBiology1ChaptersAndQuestions() {
        List<Chapter> chapters = SampleBiologyData.getChapters(SampleBiologyData.PAPER_BIOLOGY_1);
        assertNotNull(chapters);
        assertTrue("Paper 1 should have at least 2 chapters", chapters.size() >= 2);

        for (Chapter chapter : chapters) {
            assertEquals(SampleBiologyData.PAPER_BIOLOGY_1, chapter.getPaper());
            List<Question> questions = SampleBiologyData.getQuestions(SampleBiologyData.PAPER_BIOLOGY_1, chapter.getId());
            assertNotNull(questions);
            assertTrue("Each chapter must have at least 3 questions", questions.size() >= 3);
            for (Question q : questions) {
                assertEquals(4, q.getOptions().size());
                assertTrue(q.getCorrectOptionIndex() >= 0 && q.getCorrectOptionIndex() <= 3);
                assertEquals("Sample", q.getSource());
                assertNull(q.getYear());
            }
        }
    }

    @Test
    public void testBiology2ChaptersAndQuestions() {
        List<Chapter> chapters = SampleBiologyData.getChapters(SampleBiologyData.PAPER_BIOLOGY_2);
        assertNotNull(chapters);
        assertTrue("Paper 2 should have at least 2 chapters", chapters.size() >= 2);

        for (Chapter chapter : chapters) {
            assertEquals(SampleBiologyData.PAPER_BIOLOGY_2, chapter.getPaper());
            List<Question> questions = SampleBiologyData.getQuestions(SampleBiologyData.PAPER_BIOLOGY_2, chapter.getId());
            assertNotNull(questions);
            assertTrue("Each chapter must have at least 3 questions", questions.size() >= 3);
            for (Question q : questions) {
                assertEquals(4, q.getOptions().size());
                assertTrue(q.getCorrectOptionIndex() >= 0 && q.getCorrectOptionIndex() <= 3);
                assertEquals("Sample", q.getSource());
                assertNull(q.getYear());
            }
        }
    }

    @Test
    public void testInvalidPaperOrChapterReturnsEmptyList() {
        List<Chapter> emptyChapters = SampleBiologyData.getChapters("INVALID_PAPER");
        assertNotNull(emptyChapters);
        assertTrue(emptyChapters.isEmpty());

        List<Question> emptyQuestions = SampleBiologyData.getQuestions("INVALID_PAPER", "INVALID_CHAPTER");
        assertNotNull(emptyQuestions);
        assertTrue(emptyQuestions.isEmpty());

        List<Chapter> nullChapters = SampleBiologyData.getChapters(null);
        assertNotNull(nullChapters);
        assertTrue(nullChapters.isEmpty());
    }
}

