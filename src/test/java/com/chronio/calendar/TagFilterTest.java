package com.chronio.calendar;

import com.chronio.calendar.model.CalendarData;
import com.chronio.calendar.model.CalendarModelImpl;
import com.chronio.calendar.model.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TagFilterTest {

    private CalendarModelImpl model;

    @BeforeEach
    void setUp() {
        model = new CalendarModelImpl(CalendarData.empty());
    }

    @Test
    void eventWithVisibleTagShouldAppear() {
        final Tag tag = model.createTag("Lavoro", "#0d4853");
        model.createEvent("Riunione", "", "2025-06-10T09:00", "2025-06-10T10:00", tag.id(), false);
        assertFalse(model.getEventsForDate("2025-6-10").isEmpty());
    }

    @Test
    void eventWithHiddenTagShouldNotAppear() {
        final Tag tag = model.createTag("Lavoro", "#0d4853");
        model.toggleTagVisibility(tag.id());
        model.createEvent("Riunione", "", "2025-06-10T09:00", "2025-06-10T10:00", tag.id(), false);
        assertTrue(model.getEventsForDate("2025-6-10").isEmpty());
    }

    @Test
    void eventWithNoTagShouldAlwaysAppear() {
        model.createEvent("Promemoria", "", "2025-06-10T09:00", null, null, false);
        assertFalse(model.getEventsForDate("2025-6-10").isEmpty());
    }
}
