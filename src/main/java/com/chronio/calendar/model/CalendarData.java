package com.chronio.calendar.model;

import java.util.LinkedHashMap;

public record CalendarData(
        LinkedHashMap<String, Tag> tags,
        LinkedHashMap<String, Event> events,
        int nextTagId,
        int nextEventId
) {

    public static CalendarData empty() {
        return new CalendarData(new LinkedHashMap<>(), new LinkedHashMap<>(), 1, 1);
    }
}
