package com.chronio.calendar.controller;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Optional;

import com.chronio.calendar.model.Event;
import com.chronio.calendar.model.Tag;

public interface CalendarController {

    /**
     * @return mappa id -> LinkedHashMap restituita ha come chiave l'id del tag 
     *  e come valore l'oggetto Tag corrispondente (nel caso, t)
     */
    LinkedHashMap<String, Tag> getTags();

    /**
     * Crea un nuovo tag con nome e colore.
     *
     * @param name nome del tag
     * @param color colore in formato hex
     * @return il tag creato
     */
    Tag createTag(String name, String color);

    /**
     * Aggiorna nome e colore di un tag esistente.
     *
     * @param id id del tag
     * @param name  nuovo nome
     * @param color nuovo colore
     * @return il tag aggiornato, o empty se l'id non esiste
     */
    Optional<Tag> updateTag(String id, String name, String color);

    /**
     * Inverte la visibilità di un tag.
     * @param id id del tag
     */
    void toggleTagVisibility(String id);

    /**
     * Elimina un tag e dissocia gli eventi ad esso collegati.
     * @param id id del tag
     */
    void deleteTag(String id);

    /**
     * @return mappa id ->LinkedHashMap restituita ha come chiave l'id del tag 
     *  e come valore l'oggetto Tag corrispondente (nel caso, e)
     */
    LinkedHashMap<String, Event> getEvents();

    /**
     * Crea un nuovo evento.
     * @param title titolo
     * @param description descrizione
     * @param start data/ora inizio in formato ISO-8601
     * @param end data/ora fine, può essere null
     * @param tagId id del tag associato, può essere null (appare grigio come colore di default)
     * @param allDay true se l'evento dura tutto il giorno
     * @return l'evento creato
     */
    Event createEvent(String title, String description, String start, String end, String tagId, boolean allDay);

    /**
     * Aggiorna un evento esistente.
     * @param id id dell'evento
     * @param title titolo
     * @param description descrizione
     * @param start inizio
     * @param end fine, può essere null
     * @param tagId tag, può essere null
     * @param allDay valore allDay
     * @return l'evento aggiornato, o empty se l'id non esiste
     */
    Optional<Event> updateEvent(String id, String title, String description, String start, String end, String tagId, boolean allDay);

    /**
     * Elimina un evento.
     * @param id id dell'evento
     */
    void deleteEvent(String id);

    /**
     * Restituisce gli eventi di una data specifica, filtrati per tag visibili.
     * @param dateKey data in formato "yyyy-M-d"
     * @return lista di eventi visibili in quella data
     */
    LinkedList<Event> getEventsForDate(String dateKey);

    /**
     * @return lista degli eventi di oggi, filtrati per tag visibili
     */
    LinkedList<Event> getTodayEvents();

    /**
     * Restituisce gli eventi dei prossimi 6 giorni (escluso oggi), raggruppati per data.
     * @return mappa dateKey -> lista eventi
     */
    LinkedHashMap<String, LinkedList<Event>> getWeekEvents();
}
