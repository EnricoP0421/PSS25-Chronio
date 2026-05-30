package com.chronio.calendar.model;

import java.util.LinkedHashMap;
import java.util.Optional;

// Definisce le operazioni sul calendario: gestione tag ed eventi.
// La logica di validazione e filtraggio sta nell'implementazione.

public interface CalendarModel {

    /**
     * Restituisce tutti i tag salvati.
     * @return mappa id -> Tag
     */
    LinkedHashMap<String, Tag> getTags();

    /**
     * Crea un nuovo tag con nome e colore.
     *
     * @param name  nome del tag
     * @param color colore in formato hex
     * @return il tag creato (con eventuale colore)
     */
    Tag createTag(String name, String color);

    /**
     * Aggiorna nome e colore di un tag esistente.
     *
     * @param id    id del tag da aggiornare
     * @param name  nuovo nome
     * @param color nuovo colore
     * @return il tag aggiornato, o empty se l'id non esiste
     */
    Optional<Tag> updateTag(String id, String name, String color);

    /**
     * Inverte la visibilità di un tag (visibile -> nascosto e viceversa).
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
     * @return mappa id -> Event
     */
    LinkedHashMap<String, Event> getEvents();

    /**
     * Crea un nuovo evento. Lancia IllegalArgumentException se start è null
     * o se end è presente ma precedente a start.
     *
     * @param title       titolo dell'evento
     * @param description descrizione
     * @param start       data/ora inizio in formato ISO-8601
     * @param end         data/ora fine, può essere null
     * @param tagId       id del tag associato, può essere null
     * @param allDay      true se l'evento dura tutto il giorno
     * @return l'evento creato
     */
    Event createEvent(String title, String description, String start, String end, String tagId, boolean allDay);

    /**
     * Aggiorna un evento esistente.
     *
     * @param id          id dell'evento da aggiornare
     * @param title       nuovo titolo
     * @param description nuova descrizione
     * @param start       nuovo inizio
     * @param end         nuova fine, può essere null
     * @param tagId       nuovo tag, può essere null
     * @param allDay      nuovo valore allDay
     * @return l'evento aggiornato, o empty se l'id non esiste
     */
    Optional<Event> updateEvent(String id, String title, String description, String start, String end, String tagId, boolean allDay);

    /**
     * Elimina un evento.
     * @param id id dell'evento da eliminare
     */
    void deleteEvent(String id);

    /**
     * Restituisce gli eventi di una data specifica, filtrati per tag visibili.
     *
     * @param dateKey data in formato "yyyy-M-d"
     * @return lista di eventi visibili in quella data
     */
    java.util.LinkedList<Event> getEventsForDate(String dateKey);

    /**
     * Restituisce gli eventi di oggi, filtrati per tag visibili.
     * @return lista di eventi di oggi
     */
    java.util.LinkedList<Event> getTodayEvents();

    /**
     * Restituisce gli eventi dei prossimi 6 giorni (escluso oggi),
     * filtrati per tag visibili, raggruppati per data.
     * @return mappa dateKey -> lista eventi
     */
    LinkedHashMap<String, java.util.LinkedList<Event>> getWeekEvents();
}
