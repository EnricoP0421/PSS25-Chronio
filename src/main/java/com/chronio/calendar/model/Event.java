package com.chronio.calendar.model;

// Funzioni di un evento: titolo, descrizione, data inizio e fine
// start e end sono stringhe in formato ISO-8601
// tagId è l'id del tag associato, può essere null se l'evento non ha tag
// allDay è true se l'evento dura tutto il giorno / no orario

public record Event(
        String id,
        String title,
        String description,
        String start,
        String end,
        String tagId,
        boolean allDay
) {

    public Event withTitle(final String title) {
        return new Event(id, title, description, start, end, tagId, allDay);
    }

    public Event withDescription(final String description) {
        return new Event(id, title, description, start, end, tagId, allDay);
    }

    public Event withStart(final String start) {
        return new Event(id, title, description, start, end, tagId, allDay);
    }

    public Event withEnd(final String end) {
        return new Event(id, title, description, start, end, tagId, allDay);
    }

    public Event withTagId(final String tagId) {
        return new Event(id, title, description, start, end, tagId, allDay);
    }

    public Event withAllDay(final boolean allDay) {
        return new Event(id, title, description, start, end, tagId, allDay);
    }
}
