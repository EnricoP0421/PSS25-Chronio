package com.chronio.calendar.controller;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Optional;

import com.chronio.calendar.model.Event;
import com.chronio.calendar.model.Tag;

public interface CalendarController {

    LinkedHashMap<String, Tag> getTags();
    Tag createTag(String name, String color);
    Optional<Tag> updateTag(String id, String name, String color);
    void toggleTagVisibility(String id);
    void deleteTag(String id);
    LinkedHashMap<String, Event> getEvents();
    Event createEvent(String title, String description, String start, String end, String tagId, boolean allDay);
    Optional<Event> updateEvent(String id, String title, String description, String start, String end, String tagId, boolean allDay);
    void deleteEvent(String id);
    LinkedList<Event> getEventsForDate(String dateKey);
    LinkedList<Event> getTodayEvents();
    LinkedHashMap<String, LinkedList<Event>> getWeekEvents();
}
