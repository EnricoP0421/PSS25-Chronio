package com.chronio.calendar.view;

import java.util.List;
import java.util.Map;

import com.chronio.calendar.controller.CalendarController;
import com.chronio.calendar.model.Event;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * Barra laterale destra che mostra gli eventi di oggi e quelli dei prossimi 6 giorni.
 */
public final class EventSidebarView {

    private static final int SIDEBAR_WIDTH = 220;
    private static final int CARD_SPACING = 6;
    private static final int BOX_SPACING = 8;
    private static final String TIME_SEPARATOR = "T";
    private static final String DATE_SEPARATOR = "/";

    private final CalendarController controller;

    /**
     * Costruisce la sidebar degli eventi
     * @param controller il controller del calendario
     */
    public EventSidebarView(final CalendarController controller) {
        this.controller = controller;
    }

    /**
     * Costruisce e restituisce il nodo radice della sidebar degli eventi.
     * @return VBox con i card "Oggi" e "Questa settimana"
     */
    public VBox build() {
        final VBox box = new VBox(BOX_SPACING);
        box.setPrefWidth(SIDEBAR_WIDTH);
        box.setMaxWidth(SIDEBAR_WIDTH);
        box.getChildren().addAll(buildTodayCard(), buildWeekCard());
        return box;
    }

    /**
     * Aggiorna il contenuto della sidebar ricalcolando gli eventi correnti.
     * @param box il nodo VBox da aggiornare
     */
    public void refresh(final VBox box) {
        box.getChildren().clear();
        box.getChildren().addAll(buildTodayCard(), buildWeekCard());
    }

    private VBox buildTodayCard() {
        final VBox card = new VBox(CARD_SPACING);
        card.setStyle("-fx-border-color: gray; -fx-padding: 8;");
        card.getChildren().add(new Label("Oggi"));
        final VBox content = new VBox(CARD_SPACING);
        final List<Event> events = controller.getTodayEvents();
        if (events.isEmpty()) {
            content.getChildren().add(new Label("Nessun evento oggi"));
        } else {
            events.stream()
                .sorted((a, b) -> {
                    final String ta = a.allDay() || !a.start().contains(TIME_SEPARATOR) ? "00:00" : a.start().substring(11, 16);
                    final String tb = b.allDay() || !b.start().contains(TIME_SEPARATOR) ? "00:00" : b.start().substring(11, 16);
                    return ta.compareTo(tb);
                })
                .forEach(ev -> {
                    final String prefix = (!ev.allDay() && ev.start() != null && ev.start().contains(TIME_SEPARATOR))
                        ? ev.start().substring(11, 16) + " " : "";
                    content.getChildren().add(new Label(prefix + ev.title()));
                });
        }
        final ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setMaxHeight(150);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        card.getChildren().add(scroll);
        return card;
    }

    private String formatDateRange(final String start, final String end) {
        final String[] s = start.split("-");
        final String[] e = end.split("-");
        return "  " + s[2] + DATE_SEPARATOR + s[1] + " - " + e[2] + DATE_SEPARATOR + e[1];
    }

    private VBox buildWeekCard() {
        final VBox card = new VBox(CARD_SPACING);
        card.setStyle("-fx-border-color: gray; -fx-padding: 8;");
        card.getChildren().add(new Label("Questa settimana"));
        final VBox content = new VBox(CARD_SPACING);
        final Map<String, List<Event>> weekEvents = controller.getWeekEvents();
        if (weekEvents.isEmpty()) {
            content.getChildren().add(new Label("Nessun evento nei prossimi giorni"));
        } else {
            weekEvents.forEach((dateKey, evs) -> {
                final String[] parts = dateKey.split("-");
                final String formatted = parts[2] + DATE_SEPARATOR + parts[1] + DATE_SEPARATOR + parts[0];
                content.getChildren().add(new Label(formatted));
                evs.forEach(ev -> {
                    final String label = ev.allDay() && ev.end() != null && !ev.end().isBlank()
                        && !ev.end().equals(ev.start())
                        ? formatDateRange(ev.start(), ev.end()) + " " + ev.title()
                        : "  " + ev.title();
                    content.getChildren().add(new Label(label));
                });
            });
        }
        final ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setMaxHeight(200);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        card.getChildren().add(scroll);
        return card;
    }
}
