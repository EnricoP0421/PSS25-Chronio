package com.chronio.calendar.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.chronio.calendar.model.CalendarModel;
import com.chronio.calendar.model.CalendarModelImpl;
import com.chronio.calendar.model.Event;
import com.chronio.calendar.model.Tag;
import com.chronio.calendar.persistence.CalendarPersistence;

import java.util.Objects;

/**
 * Delega la logica a CalendarModel e persiste lo stato dopo ogni operazione di scrittura,
 * chiamando save() per salvare su disco.
 */
public final class CalendarControllerImpl implements CalendarController {

    private final CalendarModel model;
    private final CalendarPersistence persistence;

    /**
     * Costruisce il controller con il modello e la persistenza specificati.
     *
     * @param model       il modello del calendario
     * @param persistence la persistenza su disco
     */
    public CalendarControllerImpl(final CalendarModel model, final CalendarPersistence persistence) {
        this.model = Objects.requireNonNull(model, "il model non può essere null");
        this.persistence = Objects.requireNonNull(persistence, "la persistenza non può essere null");
    }

    private void save() {
        persistence.save(((CalendarModelImpl) model).getData());
    }

    @Override
    public Map<String, Tag> getTags() {
        return model.getTags();
    }

    @Override
    public Tag createTag(final String name, final String color) {
        final Tag tag = model.createTag(name, color);
        save();
        return tag;
    }

    @Override
    public Optional<Tag> updateTag(final String id, final String name, final String color) {
        final Optional<Tag> tag = model.updateTag(id, name, color);
        save();
        return tag;
    }

    @Override
    public void toggleTagVisibility(final String id) {
        model.toggleTagVisibility(id);
        save();
    }

    @Override
    public void deleteTag(final String id) {
        model.deleteTag(id);
        save();
    }

    @Override
    public Map<String, Event> getEvents() {
        return model.getEvents();
    }

    @Override
    public Event createEvent(final String title, final String description,
                             final String start, final String end,
                             final String tagId, final boolean allDay) {
        final Event event = model.createEvent(title, description, start, end, tagId, allDay);
        save();
        return event;
    }

    @Override
    public Optional<Event> updateEvent(final String id, final String title, final String description,
                                       final String start, final String end,
                                       final String tagId, final boolean allDay) {
        final Optional<Event> event = model.updateEvent(id, title, description, start, end, tagId, allDay);
        save();
        return event;
    }

    @Override
    public void deleteEvent(final String id) {
        model.deleteEvent(id);
        save();
    }

    @Override
    public List<Event> getEventsForDate(final String dateKey) {
        return model.getEventsForDate(dateKey);
    }

    @Override
    public List<Event> getTodayEvents() {
        return model.getTodayEvents();
    }

    @Override
    public Map<String, List<Event>> getWeekEvents() {
        return model.getWeekEvents();
    }
}
