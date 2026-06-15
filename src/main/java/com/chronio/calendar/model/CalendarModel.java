package com.chronio.calendar.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

// Definisce le operazioni sul calendario: gestione tag ed eventi.
// La logica di validazione e filtraggio sta nell'implementazione.

public interface CalendarModel {

    /**
     * Restituisce tutti i tag salvati.
     * @return mappa id -> ritorna il tag corrispondente all'id salvato
     */
    Map<String, Tag> getTags();

    /**
     * Crea un nuovo tag con nome e colore.
     * @param name nome del tag
     * @param color colore in formato hex
     * @return il tag creato
     */
    Tag createTag(String name, String color);

    /**
     * Aggiorna nome e colore di un tag esistente.
     * @param id id del tag da aggiornare
     * @param name nome
     * @param color colore
     * @return il tag aggiornato.
     */
    Optional<Tag> updateTag(String id, String name, String color);

    /**
     * Inverte la visibilità di un tag (visibile - nascosto e viceversa).
     * @param id id del tag
     */
    void toggleTagVisibility(String id);

    /**
     * Elimina un tag e tutti gli eventi associati ad esso.
     * @param id id del tag da eliminare
     */
    void deleteTag(String id);

    /**
     * Restituisce tutti gli eventi salvati.
     * @return mappa id -> ritorna l'event corrispondente all'id salvato
     */
    Map<String, Event> getEvents();

    /**
     * Crea un nuovo evento. Lancia IllegalArgumentException se start è null
     * @param title titolo dell'evento
     * @param description descrizione
     * @param start data/ora inizio
     * @param end data/ora fine, può essere null
     * @param tagId id del tag associato, può essere null
     * @param allDay true se l'evento dura tutto il giorno
     * @return l'evento creato
     */
    Event createEvent(String title, String description, String start, String end, String tagId, boolean allDay);

    /**
     * Aggiorna un evento esistente.
     * @param id id dell'evento da aggiornare
     * @param title titolo
     * @param description descrizione
     * @param start inizio
     * @param end fine, può essere null
     * @param tagId tag, può essere null
     * @param allDay valore allDay
     * @return l'evento aggiornato
     */
    Optional<Event> updateEvent(String id, String title, String description, String start, String end, String tagId, boolean allDay);

    /**
     * Elimina un evento.
     * @param id id dell'evento da eliminare
     */
    void deleteEvent(String id);

    /**
     * Restituisce gli eventi di una data specifica, filtrati per tag visibili.
     * @param dateKey data in formato "yyyy-M-d"
     * @return lista di eventi visibili in quella data
     */
    List<Event> getEventsForDate(String dateKey);

    /**
     * Restituisce gli eventi di oggi, filtrati per tag visibili.
     * @return lista di eventi di oggi
     */
    List<Event> getTodayEvents();

    /**
     * Restituisce gli eventi dei prossimi 6 giorni (escluso oggi),
     * filtrati per tag visibili, raggruppati per data
     * @return mappa dateKey -> lista eventi che accadono in quella data
     */
    Map<String, List<Event>> getWeekEvents();
}
