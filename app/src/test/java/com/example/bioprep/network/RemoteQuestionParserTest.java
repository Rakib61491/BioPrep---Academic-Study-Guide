package com.example.bioprep.network;

import com.example.bioprep.models.Question;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class RemoteQuestionParserTest {

    private static final String VALID_PAYLOAD = "{\n" +
            "  \"response_code\": 0,\n" +
            "  \"results\": [\n" +
            "    {\n" +
            "      \"category\": \"Science & Nature\",\n" +
            "      \"type\": \"multiple\",\n" +
            "      \"difficulty\": \"easy\",\n" +
            "      \"question\": \"What is the chemical symbol for water?\",\n" +
            "      \"correct_answer\": \"H2O\",\n" +
            "      \"incorrect_answers\": [\n" +
            "        \"CO2\",\n" +
            "        \"NaCl\",\n" +
            "        \"O2\"\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    private static final String HTML_ENTITIES_PAYLOAD = "{\n" +
            "  \"response_code\": 0,\n" +
            "  \"results\": [\n" +
            "    {\n" +
            "      \"category\": \"Science &amp; Nature\",\n" +
            "      \"type\": \"multiple\",\n" +
            "      \"difficulty\": \"medium\",\n" +
            "      \"question\": \"Which animal is known as &quot;King of the Jungle&quot;?\",\n" +
            "      \"correct_answer\": \"Lion &amp; Pride\",\n" +
            "      \"incorrect_answers\": [\n" +
            "        \"Tiger &lt;b&gt;stripes&lt;/b&gt;\",\n" +
            "        \"Elephant\",\n" +
            "        \"Bear\"\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    @Test
    public void testValidPayload() {
        Question q = RemoteQuestionParser.parseQuestion(VALID_PAYLOAD, new Random(42));
        assertNotNull(q);
        assertEquals(4, q.getOptions().size());
        assertEquals("H2O", q.getOptions().get(q.getCorrectOptionIndex()));
        assertEquals(RemoteQuestionParser.SOURCE_REMOTE, q.getSource());
        assertNull(q.getYear());
        assertTrue(q.getQuestionText().contains("water"));
    }

    @Test
    public void testHtmlEntitiesDecoded() {
        Question q = RemoteQuestionParser.parseQuestion(HTML_ENTITIES_PAYLOAD, new Random(100));
        assertNotNull(q);
        assertEquals(4, q.getOptions().size());
        assertTrue(q.getOptions().contains("Lion & Pride") || q.getOptions().contains("Lion &amp; Pride"));
        assertEquals("Lion & Pride", q.getOptions().get(q.getCorrectOptionIndex()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyResultsArray() {
        String json = "{\"response_code\": 0, \"results\": []}";
        RemoteQuestionParser.parseQuestion(json);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonZeroResponseCode() {
        String json = "{\"response_code\": 1, \"results\": []}";
        RemoteQuestionParser.parseQuestion(json);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMalformedJson() {
        String json = "{ malformed json string !!";
        RemoteQuestionParser.parseQuestion(json);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingIncorrectAnswers() {
        String json = "{\"response_code\": 0, \"results\": [{\"question\":\"Q?\", \"correct_answer\":\"A\"}]}";
        RemoteQuestionParser.parseQuestion(json);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullOrEmptyPayload() {
        RemoteQuestionParser.parseQuestion(null);
    }
}

