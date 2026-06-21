package com.chronio.calendar;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.chronio.calendar.model.CalendarData;
import com.chronio.calendar.model.CalendarModelImpl;
import com.chronio.calendar.persistence.CalendarPersistence;

class PersistenceTest {

    @TempDir
    private Path tempDir;

@Test
    void saveAndLoadShouldPreserveEvents() {
        final Path file = tempDir.resolve("calendar.json");
        final CalendarPersistence persistence = new CalendarPersistence(file);

        final CalendarModelImpl model = new CalendarModelImpl(CalendarData.empty());
        model.createEvent("Riunione", "desc", "2026-06-10T09:00", "2026-06-10T10:00", null, false);
        persistence.save(model.getData());

        final CalendarData loaded = persistence.load();
        assertEquals(1, loaded.events().size());
        assertEquals("Riunione", loaded.events().values().iterator().next().title());
    }

    @Test
    void saveAndLoadShouldPreserveTags() {
        final Path file = tempDir.resolve("calendar.json");
        final CalendarPersistence persistence = new CalendarPersistence(file);

        final CalendarModelImpl model = new CalendarModelImpl(CalendarData.empty());
        model.createTag("Lavoro", "#0d4853");
        persistence.save(model.getData());

        final CalendarData loaded = persistence.load();
        assertEquals(1, loaded.tags().size());
        assertEquals("Lavoro", loaded.tags().values().iterator().next().name());
    }

    @Test
    void missingFileShouldReturnEmpty() {
        final Path file = tempDir.resolve("nonexistent.json");
        final CalendarPersistence persistence = new CalendarPersistence(file);
        final CalendarData loaded = persistence.load();
        assertTrue(loaded.events().isEmpty());
        assertTrue(loaded.tags().isEmpty());
    }
}
