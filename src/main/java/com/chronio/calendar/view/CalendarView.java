package com.chronio.calendar.view;

import com.chronio.calendar.controller.CalendarController;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.YearMonth;

public final class CalendarView {

    private final CalendarController controller;
    private final Stage stage;
    private YearMonth displayMonth = YearMonth.now();

    public CalendarView(final CalendarController controller, final Stage stage) {
        this.controller = controller;
        this.stage = stage;
    }

    public VBox build() {
        final VBox box = new VBox(8);
        VBox.setVgrow(box, Priority.ALWAYS);
        box.getChildren().add(buildGrid());
        return box;
    }

    private GridPane buildGrid() {
        final GridPane grid = new GridPane();
        grid.setHgap(4);
        grid.setVgap(4);

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
            final Label cell = new Label(String.valueOf(day));
            cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            cell.setAlignment(Pos.TOP_LEFT);
            cell.setStyle(isToday
                ? "-fx-border-color: gray; -fx-background-color: lightblue; -fx-padding: 4;"
                : "-fx-border-color: gray; -fx-padding: 4;");
            grid.add(cell, col, row);
            col++;
            if (col == 7) { col = 0; row++; }
        }

        return grid;
    }
}
