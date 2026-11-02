package com.example.bioprep.utils;

import com.example.bioprep.models.Question;
import com.example.bioprep.models.QuizResult;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class QuizScorerTest {

    private List<Question> createSampleQuestions() {
        List<Question> list = new ArrayList<>();
        list.add(new Question("q1", "BIOLOGY_1", "c1", "Topic A", "What is cell powerhouse?",
                Arrays.asList("Mitochondria", "Ribosome", "Nucleus", "Chloroplast"), 0, "Mitochondria generates ATP", "Sample", null));
        list.add(new Question("q2", "BIOLOGY_1", "c1", "Topic B", "Which organelle synthesizes protein?",
                Arrays.asList("Lysosome", "Ribosome", "Golgi apparatus", "Vacuole"), 1, "Ribosomes synthesize protein", "Sample", null));
        list.add(new Question("q3", "BIOLOGY_1", "c1", "Topic C", "Which organelle carries genetic info?",
                Arrays.asList("Cytoplasm", "Endoplasmic Reticulum", "Nucleus", "Centrosome"), 2, "Nucleus contains DNA", "Sample", null));
        list.add(new Question("q4", "BIOLOGY_1", "c1", "Topic D", "Plant cell wall is made of?",
                Arrays.asList("Lipid", "Protein", "Glycogen", "Cellulose"), 3, "Cellulose forms the wall", "Sample", null));
        return list;
    }

    @Test
    public void testAllCorrect() {
        List<Question> questions = createSampleQuestions();
        Map<String, Integer> answers = new HashMap<>();
        answers.put("q1", 0);
        answers.put("q2", 1);
        answers.put("q3", 2);
        answers.put("q4", 3);

        QuizResult result = QuizScorer.score(questions, answers);
        assertEquals(4, result.getTotalQuestions());
        assertEquals(4, result.getCorrectCount());
        assertEquals(0, result.getIncorrectCount());
        assertEquals(0, result.getUnansweredCount());
        assertEquals(100.0, result.getScorePercentage(), 0.001);
    }

    @Test
    public void testAllWrong() {
        List<Question> questions = createSampleQuestions();
        Map<String, Integer> answers = new HashMap<>();
        answers.put("q1", 1);
        answers.put("q2", 0);
        answers.put("q3", 0);
        answers.put("q4", 0);

        QuizResult result = QuizScorer.score(questions, answers);
        assertEquals(4, result.getTotalQuestions());
        assertEquals(0, result.getCorrectCount());
        assertEquals(4, result.getIncorrectCount());
        assertEquals(0, result.getUnansweredCount());
        assertEquals(0.0, result.getScorePercentage(), 0.001);
    }

    @Test
    public void testMixedAnswers() {
        List<Question> questions = createSampleQuestions();
        Map<String, Integer> answers = new HashMap<>();
        answers.put("q1", 0); // correct
        answers.put("q2", 3); // incorrect
        // q3 unanswered
        answers.put("q4", 3); // correct

        QuizResult result = QuizScorer.score(questions, answers);
        assertEquals(4, result.getTotalQuestions());
        assertEquals(2, result.getCorrectCount());
        assertEquals(1, result.getIncorrectCount());
        assertEquals(1, result.getUnansweredCount());
        assertEquals(50.0, result.getScorePercentage(), 0.001);
    }

    @Test
    public void testAllUnanswered() {
        List<Question> questions = createSampleQuestions();
        Map<String, Integer> answers = new HashMap<>();

        QuizResult result = QuizScorer.score(questions, answers);
        assertEquals(4, result.getTotalQuestions());
        assertEquals(0, result.getCorrectCount());
        assertEquals(0, result.getIncorrectCount());
        assertEquals(4, result.getUnansweredCount());
        assertEquals(0.0, result.getScorePercentage(), 0.001);
    }

    @Test
    public void testEmptyAndNullQuestions() {
        QuizResult resultNull = QuizScorer.score(null, new HashMap<>());
        assertNotNull(resultNull);
        assertEquals(0, resultNull.getTotalQuestions());
        assertEquals(0.0, resultNull.getScorePercentage(), 0.001);

        QuizResult resultEmpty = QuizScorer.score(new ArrayList<>(), new HashMap<>());
        assertNotNull(resultEmpty);
        assertEquals(0, resultEmpty.getTotalQuestions());
        assertEquals(0.0, resultEmpty.getScorePercentage(), 0.001);
    }

    @Test
    public void testInvalidAnswerIndexHandledAsUnanswered() {
        List<Question> questions = createSampleQuestions();
        Map<String, Integer> answers = new HashMap<>();
        answers.put("q1", -1); // invalid
        answers.put("q2", 5);  // out of range (0-3)

        QuizResult result = QuizScorer.score(questions, answers);
        assertEquals(4, result.getTotalQuestions());
        assertEquals(0, result.getCorrectCount());
        assertEquals(0, result.getIncorrectCount());
        assertEquals(4, result.getUnansweredCount());
    }
}

