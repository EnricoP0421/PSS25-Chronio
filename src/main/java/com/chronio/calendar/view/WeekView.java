package com.chronio.calendar.view;

import java.time.LocalDate;

import com.chronio.calendar.controller.CalendarController;
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

    private final CalendarController controller;
    private final Stage stage;
    private final EventSidebarView sidebarView;
    private final VBox sidebar;
    private LocalDate weekStart = startOfWeek(LocalDate.now());
    private Label navLabel;

    public WeekView(final CalendarController controller, final Stage stage,
                    final EventSidebarView sidebarView, final VBox sidebar) {
        this.controller = controller;
        this.stage = stage;
        this.sidebarView = sidebarView;
        this.sidebar = sidebar;
    }

    private VBox mainBox;

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
        prev.setOnAction(e -> { weekStart = weekStart.minusWeeks(1); navLabel.setText(weekLabel()); rebuildContent(); });
        next.setOnAction(e -> { weekStart = weekStart.plusWeeks(1); navLabel.setText(weekLabel()); rebuildContent(); });
        final HBox nav = new HBox(12, prev, navLabel, next);
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
        for (int i = 0; i < 7; i++) {
            final ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            cc.setFillWidth(true);
            grid.getColumnConstraints().add(cc);
        }

        final LocalDate today = LocalDate.now();

        grid.getRowConstraints().add(new RowConstraints(36));
        grid.add(new Label(""), 0, 0);
        for (int i = 0; i < 7; i++) {
            final LocalDate date = weekStart.plusDays(i);
            final Label lbl = new Label(DAYS_IT[i] + " " + date.getDayOfMonth() + "/" + date.getMonthValue());
            lbl.setMaxWidth(Double.MAX_VALUE);
            lbl.setAlignment(Pos.CENTER);
            lbl.setStyle("-fx-font-weight: bold; -fx-padding: 4; -fx-border-color: lightgray;"
                + (date.equals(today) ? " -fx-background-color: lightblue;" : ""));
            grid.add(lbl, i + 1, 0);
        }

        grid.getRowConstraints().add(new RowConstraints(40));
        final Label adLbl = new Label("Tutto\nil giorno");
        adLbl.setStyle("-fx-font-size: 9;");
        adLbl.setAlignment(Pos.CENTER);
        grid.add(adLbl, 0, 1);
        for (int i = 0; i < 7; i++) {
            final LocalDate date = weekStart.plusDays(i);
            final VBox cell = new VBox(2);
            cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            cell.setStyle("-fx-border-color: lightgray; -fx-padding: 2;");
            controller.getEventsForDate(ViewUtils.toKey(date)).stream()
                .filter(ev -> ev.allDay())
                .forEach(ev -> cell.getChildren().add(ViewUtils.makePill(ev, date, controller, stage, () -> {
                    rebuildContent();
                    sidebarView.refresh(sidebar);
                })));
            cell.setOnMouseClicked(e -> { new EventDialog(stage, controller, date).showAndWait(); rebuildContent(); sidebarView.refresh(sidebar); });
            grid.add(cell, i + 1, 1);
        }

        for (int h = 0; h < HOURS; h++) {
            grid.getRowConstraints().add(new RowConstraints(60));
            final Label timeLbl = new Label(String.format("%02d:00", h));
            timeLbl.setStyle("-fx-font-size: 10; -fx-padding: 2;");
            timeLbl.setAlignment(Pos.TOP_RIGHT);
            grid.add(timeLbl, 0, h + 2);
            for (int i = 0; i < 7; i++) {
                final LocalDate date = weekStart.plusDays(i);
                final int hour = h;
                final VBox cell = new VBox(2);
                cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                cell.setStyle("-fx-border-color: lightgray; -fx-padding: 2;");
                controller.getEventsForDate(ViewUtils.toKey(date)).stream()
                    .filter(ev -> !ev.allDay() && ViewUtils.startsAtHour(ev, hour))
                    .forEach(ev -> cell.getChildren().add(ViewUtils.makePill(ev, date, controller, stage, () -> {
                        rebuildContent();
                        sidebarView.refresh(sidebar);
                    })));
                cell.setOnMouseClicked(e -> { new EventDialog(stage, controller, date, hour, null).showAndWait(); rebuildContent(); sidebarView.refresh(sidebar); });
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
