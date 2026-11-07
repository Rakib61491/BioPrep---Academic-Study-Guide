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

    @Test
    public void testExactTwelveBotanyAndZoologyChapters() {
        String[] expectedBotany = {
                "Chapter 1: Cell and Its Structure (কোষ ও এর গঠন)",
                "Chapter 2: Cell Division (কোষ বিভাজন)",
                "Chapter 3: Cell Chemistry (কোষ রসায়ন)",
                "Chapter 4: Microorganisms (অণুজীব)",
                "Chapter 5: Algae and Fungi (শৈবাল ও ছত্রাক)",
                "Chapter 6: Bryophyta and Pteridophyta (ব্রায়োফাইটা ও টেরিডোফাইটা)",
                "Chapter 7: Gymnosperms and Angiosperms (নগ্নবীজী ও আবৃতবীজী উদ্ভিদ)",
                "Chapter 8: Tissue and Tissue System (টিস্যু ও টিস্যুতন্ত্র)",
                "Chapter 9: Plant Physiology (উদ্ভিদ শারীরতত্ত্ব)",
                "Chapter 10: Plant Reproduction (উদ্ভিদ প্রজনন)",
                "Chapter 11: Biotechnology (জীবপ্রযুক্তি)",
                "Chapter 12: Environment, Distribution and Conservation of Organisms (জীবের পরিবেশ, বিস্তার ও সংরক্ষণ)"
        };

        String[] expectedZoology = {
                "Chapter 1: Animal Diversity and Classification (প্রাণীর বিভিন্নতা ও শ্রেণিবিন্যাস)",
                "Chapter 2: Introduction to Animals (প্রাণীর পরিচিতি)",
                "Chapter 3: Digestion and Absorption (পরিপাক ও শোষণ)",
                "Chapter 4: Blood Circulation (রক্ত সঞ্চালন)",
                "Chapter 5: Respiration and Breathing (শ্বাসক্রিয়া ও শ্বসন)",
                "Chapter 6: Waste and Excretion (বর্জ্য ও নিষ্কাশন)",
                "Chapter 7: Locomotion and Movement (চলন ও অঙ্গচালনা)",
                "Chapter 8: Coordination and Control (সমন্বয় ও নিয়ন্ত্রণ)",
                "Chapter 9: Continuation of Human Life (মানব জীবনের ধারাবাহিকতা)",
                "Chapter 10: Defence of the Human Body (মানবদেহের প্রতিরক্ষা)",
                "Chapter 11: Genetics and Evolution (জিনতত্ত্ব ও বিবর্তন)",
                "Chapter 12: Animal Behaviour (প্রাণীর আচরণ)"
        };

        List<Chapter> botChapters = SampleBiologyData.getChapters(SampleBiologyData.PAPER_BIOLOGY_1);
        assertNotNull(botChapters);
        assertEquals(12, botChapters.size());
        for (int i = 0; i < 12; i++) {
            assertEquals(i + 1, botChapters.get(i).getChapterNumber());
            assertEquals(expectedBotany[i], botChapters.get(i).getTitle());
        }

        List<Chapter> zooChapters = SampleBiologyData.getChapters(SampleBiologyData.PAPER_BIOLOGY_2);
        assertNotNull(zooChapters);
        assertEquals(12, zooChapters.size());
        for (int i = 0; i < 12; i++) {
            assertEquals(i + 1, zooChapters.get(i).getChapterNumber());
            assertEquals(expectedZoology[i], zooChapters.get(i).getTitle());
        }
    }
}

