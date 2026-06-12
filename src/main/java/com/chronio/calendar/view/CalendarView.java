package com.chronio.calendar.view;

import com.chronio.calendar.controller.CalendarController;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.YearMonth;

public final class CalendarView {

    private final CalendarController controller;
    private final Stage stage;
    private final EventSidebarView sidebarView;
    private final VBox sidebar;
    private YearMonth displayMonth = YearMonth.now();

    public CalendarView(final CalendarController controller, final Stage stage,
                        final EventSidebarView sidebarView, final VBox sidebar) {
        this.controller = controller;
        this.stage = stage;
        this.sidebarView = sidebarView;
        this.sidebar = sidebar;
    }

    private static final String[] DAYS_IT = {"Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom"};

    private static final String[] MONTHS_IT = {
        "Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno",
        "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"
    };

    private String monthLabel() {
        return MONTHS_IT[displayMonth.getMonthValue() - 1] + " " + displayMonth.getYear();
    }

    private GridPane grid;

    public VBox build() {
        final VBox box = new VBox(8);
        VBox.setVgrow(box, Priority.ALWAYS);
        grid = buildGrid();
        box.getChildren().addAll(buildNav(box), buildDayHeaders(), grid);
        VBox.setVgrow(grid, Priority.ALWAYS);
        return box;
    }

    public void refresh() {
        refreshGrid(grid);
    }

    private HBox buildDayHeaders() {
        final HBox box = new HBox(4);
        for (final String day : DAYS_IT) {
            final Label lbl = new Label(day);
            lbl.setMaxWidth(Double.MAX_VALUE);
            lbl.setAlignment(Pos.CENTER);
            HBox.setHgrow(lbl, Priority.ALWAYS);
            box.getChildren().add(lbl);
        }
        return box;
    }

    private HBox buildNav(final VBox box) {
        final Button prev = new Button("<");
        final Button next = new Button(">");
        final Label label = new Label(monthLabel());

        prev.setOnAction(e -> {
            displayMonth = displayMonth.minusMonths(1);
            label.setText(monthLabel());
            refreshGrid(grid);
        });
        next.setOnAction(e -> {
            displayMonth = displayMonth.plusMonths(1);
            label.setText(monthLabel());
            refreshGrid(grid);
        });

        final HBox nav = new HBox(12, prev, label, next);
        nav.setAlignment(Pos.CENTER_LEFT);
        return nav;
    }

    private void refreshGrid(final GridPane grid) {
        grid.getChildren().clear();
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();

        for (int i = 0; i < 7; i++) {
            final ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / 7);
            cc.setFillWidth(true);
            grid.getColumnConstraints().add(cc);
        }

        final LocalDate today = LocalDate.now();
        final LocalDate first = displayMonth.atDay(1);
        final int startCol = first.getDayOfWeek().getValue() - 1;
        final int daysInMonth = displayMonth.lengthOfMonth();
        final int totalRows = (int) Math.ceil((startCol + daysInMonth) / 7.0);

        for (int i = 0; i < totalRows; i++) {
            final RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(100.0 / totalRows);
            rc.setFillHeight(true);
            grid.getRowConstraints().add(rc);
        }

        int col = startCol;
        int row = 0;
        for (int day = 1; day <= daysInMonth; day++) {
            final LocalDate date = displayMonth.atDay(day);
            final boolean isToday = date.equals(today);
            final VBox cell = new VBox(2);
            cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            cell.setStyle(isToday
                ? "-fx-border-color: gray; -fx-background-color: lightblue; -fx-padding: 4;"
                : "-fx-border-color: gray; -fx-padding: 4;");
            final Label dayNum = new Label(String.valueOf(day));
            cell.getChildren().add(dayNum);
            final String dateKey = date.getYear() + "-" + date.getMonthValue() + "-" + date.getDayOfMonth();
            controller.getEventsForDate(dateKey).forEach(ev -> {
                final String color = ev.tagId() != null && controller.getTags().get(ev.tagId()) != null
                    ? controller.getTags().get(ev.tagId()).color()
                    : "#888888";
                final Label pill = new Label(ev.title());
                pill.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-padding: 1 4; -fx-background-radius: 3; -fx-font-size: 10;");
                pill.setMaxWidth(Double.MAX_VALUE);
                pill.setOnMouseClicked(e -> {
                    e.consume();
                    new EventDialog(stage, controller, date, ev).showAndWait();
                    refreshGrid(grid);
                });
                cell.getChildren().add(pill);
            });
            cell.setOnMouseClicked(e -> {
                new EventDialog(stage, controller, date).showAndWait();
                refreshGrid(grid);
                sidebarView.refresh(sidebar);
            });
            grid.add(cell, col, row);
            col++;
            if (col == 7) { col = 0; row++; }
        }
    }

    private GridPane buildGrid() {
        final GridPane g = new GridPane();
        g.setHgap(4);
        g.setVgap(4);
        refreshGrid(g);
        return g;
    }
}
