package com.chronio.calendar.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementazione di CalendarModel.
 * Delega la logica a CalendarData e persiste lo stato dopo ogni operazione di scrittura.
 */
public final class CalendarModelImpl implements CalendarModel {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final DateTimeFormatter D_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final int WEEK_LOOKAHEAD = 6;

    private CalendarData data;

    /**
     * Costruisce il modello a partire da dati esistenti.
     * @param data i dati iniziali del calendario
     */
    public CalendarModelImpl(final CalendarData data) {
        this.data = data;
    }

    @Override
    public Map<String, Tag> getTags() {
        return data.tags();
    }

    @Override
    public Tag createTag(final String name, final String color) {
        final String id = "t" + data.nextTagId();
        final Tag tag = new Tag(id, name, color, true);
        final Map<String, Tag> tags = new LinkedHashMap<>(data.tags());
        tags.put(id, tag);
        data = new CalendarData(new LinkedHashMap<>(tags), data.events(), data.nextTagId() + 1, data.nextEventId());
        return tag;
    }

    @Override
    public Optional<Tag> updateTag(final String id, final String name, final String color) {
        final Tag existing = data.tags().get(id);
        if (existing == null) {
            return Optional.empty();
        }
        final Tag updated = new Tag(id, name, color, existing.visible());
        final Map<String, Tag> tags = new LinkedHashMap<>(data.tags());
        tags.put(id, updated);
        data = new CalendarData(new LinkedHashMap<>(tags), data.events(), data.nextTagId(), data.nextEventId());
        return Optional.of(updated);
    }

    @Override
    public void toggleTagVisibility(final String id) {
        final Tag existing = data.tags().get(id);
        if (existing == null) {
            return;
        }
        final Map<String, Tag> tags = new LinkedHashMap<>(data.tags());
        tags.put(id, new Tag(id, existing.name(), existing.color(), !existing.visible()));
        data = new CalendarData(new LinkedHashMap<>(tags), data.events(), data.nextTagId(), data.nextEventId());
    }

    @Override
    public void deleteTag(final String id) {
        final Map<String, Tag> tags = new LinkedHashMap<>(data.tags());
        tags.remove(id);
        final Map<String, Event> events = new LinkedHashMap<>();
        data.events().forEach((eid, ev) -> {
            final Event updated = id.equals(ev.tagId())
                ? new Event(ev.id(), ev.title(), ev.description(), ev.start(), ev.end(), null, ev.allDay())
                : ev;
            events.put(eid, updated);
        });
        data = new CalendarData(new LinkedHashMap<>(tags), new LinkedHashMap<>(events), data.nextTagId(), data.nextEventId());
    }

    @Override
    public Map<String, Event> getEvents() {
        return data.events();
    }

    @Override
    public Event createEvent(final String title, final String description,
                             final String start, final String end,
                             final String tagId, final boolean allDay) {
        validate(start, end);
        final String id = "e" + data.nextEventId();
        final Event event = new Event(id, title, description, start, end, tagId, allDay);
        final Map<String, Event> events = new LinkedHashMap<>(data.events());
        events.put(id, event);
        data = new CalendarData(data.tags(), new LinkedHashMap<>(events), data.nextTagId(), data.nextEventId() + 1);
        return event;
    }

    @Override
    public Optional<Event> updateEvent(final String id, final String title, final String description,
                                       final String start, final String end,
                                       final String tagId, final boolean allDay) {
        if (!data.events().containsKey(id)) {
            return Optional.empty();
        }
        validate(start, end);
        final Event updated = new Event(id, title, description, start, end, tagId, allDay);
        final Map<String, Event> events = new LinkedHashMap<>(data.events());
        events.put(id, updated);
        data = new CalendarData(data.tags(), new LinkedHashMap<>(events), data.nextTagId(), data.nextEventId());
        return Optional.of(updated);
    }

    @Override
    public void deleteEvent(final String id) {
        final Map<String, Event> events = new LinkedHashMap<>(data.events());
        events.remove(id);
        data = new CalendarData(data.tags(), new LinkedHashMap<>(events), data.nextTagId(), data.nextEventId());
    }

    @Override
    public List<Event> getEventsForDate(final String dateKey) {
        final List<Event> result = new ArrayList<>();
        data.events().forEach((id, ev) -> {
            if (isVisible(ev) && fallsOn(ev, dateKey)) {
                result.add(ev);
            }
        });
        return result;
    }

    @Override
    public List<Event> getTodayEvents() {
        final LocalDate today = LocalDate.now();
        return getEventsForDate(today.getYear() + "-" + today.getMonthValue() + "-" + today.getDayOfMonth());
    }

    @Override
    public Map<String, List<Event>> getWeekEvents() {
        final Map<String, List<Event>> result = new LinkedHashMap<>();
        final LocalDate today = LocalDate.now();
        for (int i = 1; i <= WEEK_LOOKAHEAD; i++) {
            final LocalDate d = today.plusDays(i);
            final String key = d.getYear() + "-" + d.getMonthValue() + "-" + d.getDayOfMonth();
            final List<Event> evs = getEventsForDate(key);
            if (!evs.isEmpty()) {
                result.put(key, evs);
            }
        }
        return result;
    }

    /**
     * Restituisce i dati correnti del calendario, usato dalla persistenza per il salvataggio.
     * @return lo stato corrente del calendario
     */
    public CalendarData getData() {
        return data;
    }

    private void validate(final String start, final String end) {
        if (start == null || start.isBlank()) {
            throw new IllegalArgumentException("La data di inizio è obbligatoria");
        }
        if (end != null && !end.isBlank()) {
            final LocalDateTime s = parseDateTime(start);
            final LocalDateTime e = parseDateTime(end);
            if (s != null && e != null && e.isBefore(s)) {
                throw new IllegalArgumentException("La data di fine non può essere prima di quella di inizio");
            }
        }
    }

    private boolean isVisible(final Event ev) {
        if (ev.tagId() == null) {
            return true;
        }
        final Tag tag = data.tags().get(ev.tagId());
        return tag == null || tag.visible();
    }

    private boolean fallsOn(final Event ev, final String dateKey) {
        final LocalDate target = parseKey(dateKey);
        if (target == null) {
            return false;
        }
        final LocalDate start = parseDate(ev.start());
        if (start == null) {
            return false;
        }
        final LocalDate end = ev.end() != null ? parseDate(ev.end()) : start;
        return !target.isBefore(start) && !target.isAfter(end != null ? end : start);
    }

    private LocalDateTime parseDateTime(final String s) {
        try {
            return LocalDateTime.parse(s, DT_FMT);
        } catch (final DateTimeParseException e) {
            return null;
        }
    }

    private LocalDate parseDate(final String s) {
        if (s == null) {
            return null;
        }
        try {
            return s.contains("T") ? LocalDateTime.parse(s, DT_FMT).toLocalDate() : LocalDate.parse(s, D_FMT);
        } catch (final DateTimeParseException e) {
            return null;
        }
    }

    private LocalDate parseKey(final String key) {
        if (key == null) {
            return null;
        }
        final String[] p = key.split("-");
        if (p.length != 3) {
            return null;
        }
        try {
            return LocalDate.of(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]));
        } catch (final NumberFormatException | DateTimeParseException e) {
            return null;
        }
    }
}
