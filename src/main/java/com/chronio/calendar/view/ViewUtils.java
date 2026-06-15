package com.chronio.calendar.view;

import java.time.LocalDate;

import com.chronio.calendar.controller.CalendarController;
import com.chronio.calendar.model.Event;

import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

/**
 * Utility condivise tra le viste del calendario.
 */
final class ViewUtils {

    private static final int TIME_START = 11;
    private static final int TIME_END_HOUR = 13;
    private static final int TIME_END_MIN = 16;
    private static final int LUMINANCE_THRESHOLD = 380;
    private static final String DEFAULT_COLOR = "#888888";

    private ViewUtils() {
    }

    /**
     * Converte una data nel formato chiave usato dal modello (yyyy-M-d).
     *
     * @param d la data
     *
     * @return stringa in formato "yyyy-M-d"
     */
    static String toKey(final LocalDate d) {
        return d.getYear() + "-" + d.getMonthValue() + "-" + d.getDayOfMonth();
    }

    /**
     * Determina se un colore esadecimale è chiaro o scuro.
     * 
     * @param hex colore in formato "#rrggbb"
     *
     * @return true se il colore è chiaro
     */
    static boolean isLight(final String hex) {
        final int r = Integer.parseInt(hex.substring(1, 3), 16);
        final int g = Integer.parseInt(hex.substring(3, 5), 16);
        final int b = Integer.parseInt(hex.substring(5, 7), 16);
        return (r + g + b) > LUMINANCE_THRESHOLD;
    }

    /**
     * Restituisce l'orario di inizio dell'evento in formato "HH:mm", o stringa vuota se assente.
     *
     * @param ev l'evento
     *
     * @return orario formattato o stringa vuota
     */
    static String formatTime(final Event ev) {
        if (ev.start() == null || !ev.start().contains("T")) {
            return "";
        }
        return ev.start().substring(TIME_START, TIME_END_MIN);
    }

    /**
     * Controlla se l'evento inizia nell'ora specificata.
     *
     * @param ev l'evento
     * @param hour l'ora da confrontare
     *
     * @return true se l'evento inizia in quell'ora
     */
    static boolean startsAtHour(final Event ev, final int hour) {
        if (ev.start() == null || !ev.start().contains("T")) {
            return false;
        }
        try {
            return Integer.parseInt(ev.start().substring(TIME_START, TIME_END_HOUR)) == hour;
        } catch (final NumberFormatException | StringIndexOutOfBoundsException e) {
            return false;
        }
    }

    /**
     * Crea un' etichetta colorata per un evento + click per aprire il dialog.
     *
     * @param ev l'evento
     * @param date la data associata alla pill
     * @param controller il controller del calendario
     * @param stage lo stage principale
     * @param onRefresh refresh eseguito dopo la chiusura del dialog
     *
     * @return Label 
     */
    static Label makePill(final Event ev, final LocalDate date, final CalendarController controller,
                          final Stage stage, final Runnable onRefresh) {
        final String color = ev.tagId() != null && controller.getTags().get(ev.tagId()) != null
            ? controller.getTags().get(ev.tagId()).color() : DEFAULT_COLOR;
        final String textColor = isLight(color) ? "black" : "white";
        final String prefix = ev.allDay() ? "" : formatTime(ev) + " ";
        final Label pill = new Label(prefix + ev.title());
        pill.setStyle("-fx-background-color: " + color + "; -fx-text-fill: " + textColor
            + "; -fx-padding: 1 4; -fx-background-radius: 3; -fx-font-size: 10;");
        pill.setMaxWidth(Double.MAX_VALUE);
        if (ev.description() != null && !ev.description().isBlank()) {
            final Tooltip tip = new Tooltip(ev.description());
            tip.setShowDelay(javafx.util.Duration.ZERO);
            pill.setTooltip(tip);
        }
        pill.setOnMouseClicked(e -> {
            e.consume();
            new EventDialog(stage, controller, date, ev).showAndWait();
            onRefresh.run();
        });
        return pill;
    }
}
