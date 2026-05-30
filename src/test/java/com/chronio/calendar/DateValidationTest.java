package com.chronio.calendar;

import com.chronio.calendar.model.CalendarData;
import com.chronio.calendar.model.CalendarModelImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DateValidationTest {

    private CalendarModelImpl model;

    @BeforeEach
    void setUp() {
        model = new CalendarModelImpl(CalendarData.empty());
    }

    @Test
    void startNullShouldThrow() {
        assertThrows(IllegalArgumentException.class, () ->
            model.createEvent("Test", "", null, null, null, false)
        );
    }

    @Test
    void endBeforeStartShouldThrow() {
        assertThrows(IllegalArgumentException.class, () ->
            model.createEvent("Test", "", "2025-06-10T10:00", "2025-06-10T09:00", null, false)
        );
    }

    @Test
    void validDatesShouldWork() {
        assertDoesNotThrow(() ->
            model.createEvent("Test", "", "2025-06-10T09:00", "2025-06-10T10:00", null, false)
        );
    }
}
