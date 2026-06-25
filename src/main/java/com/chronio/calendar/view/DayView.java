package com.chronio.calendar.view;

import java.time.LocalDate;

import com.chronio.calendar.controller.CalendarController;
import com.chronio.calendar.model.Event;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Vista giornaliera del calendario
 * Mostra gli eventi del giorno corrente suddivisi per ora, con navigazione avanti/indietro
 * Pulsante per tornare su Mese/Settimana
 */
public final class DayView {

    private static final String[] MONTHS_IT = {
        "Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno",
        "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre",
    };
    private static final int HOURS = 24;
    private static final double TIME_COL_W = 50;
    private static final int ALL_DAY_ROW_H = 40;
    private static final int HOUR_ROW_H = 60;
    private static final int NAV_SPACING = 12;
    private static final int CELL_SPACING = 2;
    private static final String CELL_STYLE = "-fx-border-color: lightgray; -fx-padding: 2;";
    private static final String TIME_FONT = "-fx-font-size: 10; -fx-padding: 2;";
    private static final String ALL_DAY_FONT = "-fx-font-size: 9;";

    private final CalendarController controller;
    private final Stage stage;
    private final EventSidebarView sidebarView;
    private final VBox sidebar;
    private LocalDate date = LocalDate.now();
    private Label navLabel;
    private VBox mainBox;
    private ScrollPane scrollPane;

    /**
     * Costruisce la vista giornaliera.
     *
     * @param controller il controller del calendario
     * @param stage lo stage principale
     * @param sidebarView la sidebar degli eventi
     * @param sidebar il nodo VBox della sidebar
     */
    public DayView(final CalendarController controller, final Stage stage,
                   final EventSidebarView sidebarView, final VBox sidebar) {
        this.controller = controller;
        this.stage = stage;
        this.sidebarView = sidebarView;
        this.sidebar = sidebar;
    }

    /**
     * Costruisce e restituisce il nodo radice della vista giornaliera.
     * @return VBox contenente la navigazione e la griglia degli eventi
     */
    public VBox build() {
        navLabel = new Label(dayLabel());
        mainBox = new VBox(4);
        VBox.setVgrow(mainBox, Priority.ALWAYS);
        mainBox.getChildren().addAll(buildNav(), buildScrollContent());
        return mainBox;
    }

    /**
     * Aggiorna il contenuto mantenendo la posizione di scroll.
     */
    public void refresh() {
        final double scrollPos = scrollPane != null ? scrollPane.getVvalue() : 0;
        rebuildContent();
        if (scrollPane != null) {
            scrollPane.setVvalue(scrollPos);
        }
    }

    private void rebuildContent() {
        mainBox.getChildren().set(1, buildScrollContent());
    }

    private HBox buildNav() {
        final Button prev = new Button("<");
        final Button next = new Button(">");
        prev.setOnAction(e -> {
            date = date.minusDays(1);
            navLabel.setText(dayLabel());
            rebuildContent();
        });
        next.setOnAction(e -> {
            date = date.plusDays(1);
            navLabel.setText(dayLabel());
            rebuildContent();
        });
        final HBox nav = new HBox(NAV_SPACING, prev, navLabel, next);
        nav.setAlignment(Pos.CENTER_LEFT);
        return nav;
    }

    private ScrollPane buildScrollContent() {
        final GridPane grid = new GridPane();
        grid.setHgap(0);
        grid.setVgap(0);

        final ColumnConstraints timeCol = new ColumnConstraints(TIME_COL_W, TIME_COL_W, TIME_COL_W);
        timeCol.setHgrow(Priority.NEVER);
        final ColumnConstraints eventCol = new ColumnConstraints();
        eventCol.setHgrow(Priority.ALWAYS);
        eventCol.setFillWidth(true);
        grid.getColumnConstraints().addAll(timeCol, eventCol);

        grid.getRowConstraints().add(new RowConstraints(ALL_DAY_ROW_H));
        final Label adLbl = new Label("Tutto\nil giorno");
        adLbl.setStyle(ALL_DAY_FONT);
        adLbl.setAlignment(Pos.CENTER);
        grid.add(adLbl, 0, 0);
        final VBox allDayCell = new VBox(CELL_SPACING);
        allDayCell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        allDayCell.setStyle(CELL_STYLE);
        controller.getEventsForDate(ViewUtils.toKey(date)).stream()
            .filter(Event::allDay)
            .forEach(ev -> allDayCell.getChildren().add(makePill(ev)));
        allDayCell.setOnMouseClicked(e -> {
            new EventDialog(stage, controller, date).showAndWait();
            rebuildContent();
            sidebarView.refresh(sidebar);
        });
        grid.add(allDayCell, 1, 0);

        for (int h = 0; h < HOURS; h++) {
            grid.getRowConstraints().add(new RowConstraints(HOUR_ROW_H));
            final Label timeLbl = new Label(String.format("%02d:00", h));
            timeLbl.setStyle(TIME_FONT);
            timeLbl.setAlignment(Pos.TOP_RIGHT);
            grid.add(timeLbl, 0, h + 1);
            final int hour = h;
            final VBox cell = new VBox(CELL_SPACING);
            cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            cell.setStyle(CELL_STYLE);
            controller.getEventsForDate(ViewUtils.toKey(date)).stream()
                .filter(ev -> !ev.allDay() && ViewUtils.startsAtHour(ev, hour))
                .forEach(ev -> cell.getChildren().add(makePill(ev)));
            cell.setOnMouseClicked(e -> {
                new EventDialog(stage, controller, date, hour, null).showAndWait();
                rebuildContent();
                sidebarView.refresh(sidebar);
            });
            grid.add(cell, 1, h + 1);
        }

        scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return scrollPane;
    }

    private Label makePill(final Event ev) {
        return ViewUtils.makePill(ev, date, controller, stage, () -> {
            rebuildContent();
            sidebarView.refresh(sidebar);
        });
    }

    private String dayLabel() {
        return date.getDayOfMonth() + " " + MONTHS_IT[date.getMonthValue() - 1] + " " + date.getYear();
    }
}
