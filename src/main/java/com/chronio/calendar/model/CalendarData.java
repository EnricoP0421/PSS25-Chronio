package com.chronio.calendar.model;

import java.util.LinkedHashMap;

// Questo è il contenitore di tutti i dati del calendario che vengono salvati su disco
// Gson lo trasforma in JSON e lo rilegge al riavvio dell'app
// I contatori nextTagId e nextEventId servono a generare id univoci senza usare UUID
public record CalendarData(
        LinkedHashMap<String, Tag> tags,    
        LinkedHashMap<String, Event> events,
        int nextTagId,
        int nextEventId
) {

    // Stato iniziale quando non esiste ancora nessun file di salvataggio
    public static CalendarData empty() {
        return new CalendarData(new LinkedHashMap<>(), new LinkedHashMap<>(), 1, 1);
    }
}
