package com.chronio.calendar.model;

/**
 * L'evento nel calendario
 * I campi start e end sono stringhe in formato ISO-8601
 * tagId può essere null se l'evento non ha tag associato
 * allDay è true se l'evento dura tutto il giorno, senza orario specifico
 */
public record Event(
        String id,
        String title,
        String description,
        String start,
        String end,
        String tagId,
        boolean allDay
) {
}
