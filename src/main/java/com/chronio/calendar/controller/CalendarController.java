package com.chronio.calendar.controller;

import com.chronio.calendar.model.Event;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Optional;

public interface CalendarController {

    LinkedHashMap<String, Event> getEvents();

    Event createEvent(String title, String description, String start, String end, String tagId, boolean allDay);

    Optional<Event> updateEvent(String id, String title, String description, String start, String end, String tagId, boolean allDay);

    void deleteEvent(String id);

    LinkedList<Event> getEventsForDate(String dateKey);

    LinkedList<Event> getTodayEvents();

    LinkedHashMap<String, LinkedList<Event>> getWeekEvents();
}
