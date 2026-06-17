package com.chronio.calendar;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.chronio.calendar.model.CalendarData;
import com.chronio.calendar.model.CalendarModelImpl;

class DateValidationTest {

    private static final String EVENT_TITLE = "Test";

    private CalendarModelImpl newModel() {
        return new CalendarModelImpl(CalendarData.empty());
    }

    @Test
    void startNullShouldThrow() {
        final Exception ex = assertThrows(IllegalArgumentException.class, () ->
            newModel().createEvent(EVENT_TITLE, "", null, null, null, false)
        );
        assertNotNull(ex);
    }

    @Test
    void endBeforeStartShouldThrow() {
        final Exception ex = assertThrows(IllegalArgumentException.class, () ->
            newModel().createEvent(EVENT_TITLE, "", "2026-06-10T10:00", "2026-06-10T09:00", null, false)
        );
        assertNotNull(ex);
    }

    @Test
    void validDatesShouldWork() {
        assertDoesNotThrow(() ->
            newModel().createEvent(EVENT_TITLE, "", "2026-06-10T09:00", "2026-06-10T10:00", null, false)
        );
    }
}
