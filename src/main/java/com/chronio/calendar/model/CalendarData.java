package com.chronio.calendar.model;

import java.util.LinkedHashMap;

// Questo è il contenitore di tutti i dati del calendario che persistono su disco
// La libreria Gson lo serializza in JSON e lo rilegge al riavvio dell'app.
// Il file è salvato in user/(nome utente)/.chronio/calendar.json.
//I contatori nextTagId e nextEventId garantiscono id univoci senza usare UUID.

public record CalendarData(
        LinkedHashMap<String, Tag> tags,
        LinkedHashMap<String, Event> events,
        int nextTagId,
        int nextEventId
) {

    /**
     * Restituisce uno stato iniziale vuoto, usato quando non esiste ancora nessun file di salvataggio.
     * @return istanza di {@code CalendarData} con mappe vuote e contatori a 1
     */
    public static CalendarData empty() {
        return new CalendarData(new LinkedHashMap<>(), new LinkedHashMap<>(), 1, 1);
    }
}
