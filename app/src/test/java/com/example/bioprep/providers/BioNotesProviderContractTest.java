package com.example.bioprep.providers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BioNotesProviderContractTest {

    @Test
    public void testProviderContractConstants() {
        assertEquals("com.example.bioprep.provider.notes", BioNotesProvider.PROVIDER_NAME);
        assertEquals("content://com.example.bioprep.provider.notes/notes", BioNotesProvider.URL);
        assertEquals("_id", BioNotesProvider._ID);
        assertEquals("title", BioNotesProvider.TITLE);
        assertEquals("details", BioNotesProvider.DETAILS);
        assertEquals("category", BioNotesProvider.CATEGORY);
        assertEquals("created_at", BioNotesProvider.CREATED_AT);
    }

    @Test
    public void testDatabaseConstants() {
        assertEquals("BioNotes.db", BioNotesProvider.DATABASE_NAME);
        assertEquals("notes", BioNotesProvider.NOTES_TABLE_NAME);
        assertEquals(1, BioNotesProvider.DATABASE_VERSION);
        assertTrue(BioNotesProvider.CREATE_DB_TABLE.contains("CREATE TABLE notes"));
    }
}
