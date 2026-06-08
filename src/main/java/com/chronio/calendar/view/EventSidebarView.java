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
            events.forEach(ev -> card.getChildren().add(new Label(ev.title())));
        }
        return card;
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
                evs.forEach(ev -> card.getChildren().add(new Label("  " + ev.title())));
            });
        }
        return card;
    }
}
