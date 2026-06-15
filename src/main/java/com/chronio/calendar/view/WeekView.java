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
 * Vista settimanale del calendario.
 * Mostra i 7 giorni della settimana corrente su colonne, con righe orarie e navigazione avanti/indietro
 * Pulsante per tornare a Mese/Giorno
 */
public final class WeekView {

    private static final String[] DAYS_IT = {"Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom"};
    private static final int HOURS = 24;
    private static final double TIME_COL_W = 50;
    private static final int HEADER_ROW_H = 36;
    private static final int ALL_DAY_ROW_H = 40;
    private static final int HOUR_ROW_H = 60;
    private static final int NAV_SPACING = 12;
    private static final int CELL_SPACING = 2;
    private static final int DAYS_IN_WEEK = 7;
    private static final String CELL_STYLE = "-fx-border-color: lightgray; -fx-padding: 2;";
    private static final String TIME_FONT = "-fx-font-size: 10; -fx-padding: 2;";
    private static final String ALL_DAY_FONT = "-fx-font-size: 9;";

    private final CalendarController controller;
    private final Stage stage;
    private final EventSidebarView sidebarView;
    private final VBox sidebar;
    private LocalDate weekStart = startOfWeek(LocalDate.now());
    private Label navLabel;
    private VBox mainBox;

    public WeekView(final CalendarController controller, final Stage stage,
                    final EventSidebarView sidebarView, final VBox sidebar) {
        this.controller = controller;
        this.stage = stage;
        this.sidebarView = sidebarView;
        this.sidebar = sidebar;
    }

    /**
     * Costruisce e restituisce il nodo radice della vista settimanale
     * @return VBox che contiene la navigazione e la griglia degli eventi
     */
    public VBox build() {
        navLabel = new Label(weekLabel());
        mainBox = new VBox(4);
        VBox.setVgrow(mainBox, Priority.ALWAYS);
        mainBox.getChildren().addAll(buildNav(), buildScrollContent());
        return mainBox;
    }

    private void rebuildContent() {
        mainBox.getChildren().set(1, buildScrollContent());
    }

    private HBox buildNav() {
        final Button prev = new Button("<");
        final Button next = new Button(">");
        prev.setOnAction(e -> {
            weekStart = weekStart.minusWeeks(1);
            navLabel.setText(weekLabel());
            rebuildContent();
        });
        next.setOnAction(e -> {
            weekStart = weekStart.plusWeeks(1);
            navLabel.setText(weekLabel());
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
        grid.getColumnConstraints().add(timeCol);
        for (int i = 0; i < DAYS_IN_WEEK; i++) {
            final ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            cc.setFillWidth(true);
            grid.getColumnConstraints().add(cc);
        }

        final LocalDate today = LocalDate.now();

        grid.getRowConstraints().add(new RowConstraints(HEADER_ROW_H));
        grid.add(new Label(""), 0, 0);
        for (int i = 0; i < DAYS_IN_WEEK; i++) {
            final LocalDate date = weekStart.plusDays(i);
            final Label lbl = new Label(DAYS_IT[i] + " " + date.getDayOfMonth() + "/" + date.getMonthValue());
            lbl.setMaxWidth(Double.MAX_VALUE);
            lbl.setAlignment(Pos.CENTER);
            lbl.setStyle("-fx-font-weight: bold; -fx-padding: 4; -fx-border-color: lightgray;"
                + (date.equals(today) ? " -fx-background-color: lightblue;" : ""));
            grid.add(lbl, i + 1, 0);
        }

        grid.getRowConstraints().add(new RowConstraints(ALL_DAY_ROW_H));
        final Label adLbl = new Label("Tutto\nil giorno");
        adLbl.setStyle(ALL_DAY_FONT);
        adLbl.setAlignment(Pos.CENTER);
        grid.add(adLbl, 0, 1);
        for (int i = 0; i < DAYS_IN_WEEK; i++) {
            final LocalDate date = weekStart.plusDays(i);
            final VBox cell = new VBox(CELL_SPACING);
            cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            cell.setStyle(CELL_STYLE);
            controller.getEventsForDate(ViewUtils.toKey(date)).stream()
                .filter(Event::allDay)
                .forEach(ev -> cell.getChildren().add(ViewUtils.makePill(ev, date, controller, stage, () -> {
                    rebuildContent();
                    sidebarView.refresh(sidebar);
                })));
            cell.setOnMouseClicked(e -> {
                new EventDialog(stage, controller, date).showAndWait();
                rebuildContent();
                sidebarView.refresh(sidebar);
            });
            grid.add(cell, i + 1, 1);
        }

        for (int h = 0; h < HOURS; h++) {
            grid.getRowConstraints().add(new RowConstraints(HOUR_ROW_H));
            final Label timeLbl = new Label(String.format("%02d:00", h));
            timeLbl.setStyle(TIME_FONT);
            timeLbl.setAlignment(Pos.TOP_RIGHT);
            grid.add(timeLbl, 0, h + 2);
            for (int i = 0; i < DAYS_IN_WEEK; i++) {
                final LocalDate date = weekStart.plusDays(i);
                final int hour = h;
                final VBox cell = new VBox(CELL_SPACING);
                cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                cell.setStyle(CELL_STYLE);
                controller.getEventsForDate(ViewUtils.toKey(date)).stream()
                    .filter(ev -> !ev.allDay() && ViewUtils.startsAtHour(ev, hour))
                    .forEach(ev -> cell.getChildren().add(ViewUtils.makePill(ev, date, controller, stage, () -> {
                        rebuildContent();
                        sidebarView.refresh(sidebar);
                    })));
                cell.setOnMouseClicked(e -> {
                    new EventDialog(stage, controller, date, hour, null).showAndWait();
                    rebuildContent();
                    sidebarView.refresh(sidebar);
                });
                grid.add(cell, i + 1, h + 2);
            }
        }

        final ScrollPane sp = new ScrollPane(grid);
        sp.setFitToWidth(true);
        VBox.setVgrow(sp, Priority.ALWAYS);
        return sp;
    }

    private String weekLabel() {
        final LocalDate end = weekStart.plusDays(6);
        return weekStart.getDayOfMonth() + "/" + weekStart.getMonthValue() + "/" + weekStart.getYear()
            + " - " + end.getDayOfMonth() + "/" + end.getMonthValue() + "/" + end.getYear();
    }

    private LocalDate startOfWeek(final LocalDate d) {
        return d.minusDays(d.getDayOfWeek().getValue() - 1);
    }
}
