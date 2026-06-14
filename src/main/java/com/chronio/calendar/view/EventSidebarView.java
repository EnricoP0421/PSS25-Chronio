package com.chronio.calendar.view;

import com.chronio.calendar.controller.CalendarController;
import com.chronio.calendar.model.Event;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public final class EventSidebarView {

    private final CalendarController controller;

    public EventSidebarView(final CalendarController controller) {
        this.controller = controller;
    }

    public VBox build() {
        final VBox box = new VBox(8);
        box.setPrefWidth(250);
        box.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 12;");
        box.getChildren().addAll(buildTodayCard(), buildWeekCard());
        return box;
    }

    public void refresh(final VBox box) {
        box.getChildren().clear();
        box.getChildren().addAll(buildTodayCard(), buildWeekCard());
    }

    private VBox buildTodayCard() {
        final VBox card = new VBox(6);
        card.setStyle("-fx-border-color: gray; -fx-padding: 8;");
        card.getChildren().add(new Label("Oggi"));
        final java.util.LinkedList<Event> events = controller.getTodayEvents();
        if (events.isEmpty()) {
            card.getChildren().add(new Label("Nessun evento oggi"));
        } else {
            events.stream()
                .sorted((a, b) -> {
                    final String ta = a.allDay() || !a.start().contains("T") ? "00:00" : a.start().substring(11, 16);
                    final String tb = b.allDay() || !b.start().contains("T") ? "00:00" : b.start().substring(11, 16);
                    return ta.compareTo(tb);
                })
                .forEach(ev -> {
                    final String prefix = (!ev.allDay() && ev.start() != null && ev.start().contains("T"))
                        ? ev.start().substring(11, 16) + " " : "";
                    card.getChildren().add(new Label(prefix + ev.title()));
                });
        }
        return card;
    }

    private String formatDateRange(final String start, final String end) {
        final String[] s = start.split("-");
        final String[] e = end.split("-");
        return "  " + s[2] + "/" + s[1] + " - " + e[2] + "/" + e[1];
    }

    private VBox buildWeekCard() {
        final VBox card = new VBox(6);
        card.setStyle("-fx-border-color: gray; -fx-padding: 8;");
        card.getChildren().add(new Label("Questa settimana"));
        final java.util.LinkedHashMap<String, java.util.LinkedList<Event>> weekEvents = controller.getWeekEvents();
        if (weekEvents.isEmpty()) {
            card.getChildren().add(new Label("Nessun evento nei prossimi giorni"));
        } else {
            weekEvents.forEach((dateKey, evs) -> {
                final String[] parts = dateKey.split("-");
                final String formatted = parts[2] + "/" + parts[1] + "/" + parts[0];
                card.getChildren().add(new Label(formatted));
                evs.forEach(ev -> {
                    final String label = ev.allDay() && ev.end() != null && !ev.end().isBlank()
                        && !ev.end().equals(ev.start())
                        ? formatDateRange(ev.start(), ev.end()) + " " + ev.title()
                        : "  " + ev.title();
                    card.getChildren().add(new Label(label));
                });
            });
        }
        return card;
    }
}
