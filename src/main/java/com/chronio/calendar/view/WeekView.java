package com.chronio.calendar.view;

import com.chronio.calendar.controller.CalendarController;
import com.chronio.calendar.model.Event;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

public final class WeekView {

    private final CalendarController controller;
    private final Stage stage;
    private final EventSidebarView sidebarView;
    private final VBox sidebar;
    private LocalDate weekStart = startOfWeek(LocalDate.now());

    private static final String[] DAYS_IT = {"Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom"};

    public WeekView(final CalendarController controller, final Stage stage,
                    final EventSidebarView sidebarView, final VBox sidebar) {
        this.controller = controller;
        this.stage = stage;
        this.sidebarView = sidebarView;
        this.sidebar = sidebar;
    }

    private GridPane grid;
    private Label navLabel;

    public VBox build() {
        final VBox box = new VBox(8);
        VBox.setVgrow(box, Priority.ALWAYS);
        grid = new GridPane();
        grid.setHgap(4);
        grid.setVgap(4);
        VBox.setVgrow(grid, Priority.ALWAYS);
        navLabel = new Label(weekLabel());
        box.getChildren().addAll(buildNav(), grid);
        refreshGrid();
        return box;
    }

    public void refresh() {
        refreshGrid();
    }

    private HBox buildNav() {
        final javafx.scene.control.Button prev = new javafx.scene.control.Button("<");
        final javafx.scene.control.Button next = new javafx.scene.control.Button(">");
        prev.setOnAction(e -> { weekStart = weekStart.minusWeeks(1); navLabel.setText(weekLabel()); refreshGrid(); });
        next.setOnAction(e -> { weekStart = weekStart.plusWeeks(1); navLabel.setText(weekLabel()); refreshGrid(); });
        final HBox nav = new HBox(12, prev, navLabel, next);
        nav.setAlignment(Pos.CENTER_LEFT);
        return nav;
    }

    private void refreshGrid() {
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
        for (int i = 0; i < 7; i++) {
            final LocalDate date = weekStart.plusDays(i);
            final boolean isToday = date.equals(today);
            final VBox cell = new VBox(2);
            cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            cell.setStyle(isToday
                ? "-fx-border-color: gray; -fx-background-color: lightblue; -fx-padding: 4;"
                : "-fx-border-color: gray; -fx-padding: 4;");
            final Label header = new Label(DAYS_IT[i] + " " + date.getDayOfMonth());
            header.setStyle("-fx-font-weight: bold;");
            cell.getChildren().add(header);
            final String dateKey = date.getYear() + "-" + date.getMonthValue() + "-" + date.getDayOfMonth();
            controller.getEventsForDate(dateKey).forEach(ev -> {
                final String color = ev.tagId() != null && controller.getTags().get(ev.tagId()) != null
                    ? controller.getTags().get(ev.tagId()).color() : "#888888";
                final String textColor = isLight(color) ? "black" : "white";
                final String timePrefix = ev.allDay() ? "" : formatTime(ev) + " ";
                final String rangePrefix = isMultiDay(ev) ? formatRange(ev) + " " : timePrefix;
                final Label pill = new Label(rangePrefix + ev.title());
                pill.setStyle("-fx-background-color: " + color + "; -fx-text-fill: " + textColor + "; -fx-padding: 1 4; -fx-background-radius: 3; -fx-font-size: 10;");
                pill.setMaxWidth(Double.MAX_VALUE);
                if (ev.description() != null && !ev.description().isBlank()) {
                    final Tooltip tip = new Tooltip(ev.description());
                    tip.setShowDelay(javafx.util.Duration.ZERO);
                    pill.setTooltip(tip);
                }
                pill.setOnMouseClicked(e -> {
                    e.consume();
                    new EventDialog(stage, controller, date, ev).showAndWait();
                    refreshGrid();
                    sidebarView.refresh(sidebar);
                });
                cell.getChildren().add(pill);
            });
            cell.setOnMouseClicked(e -> {
                new EventDialog(stage, controller, date).showAndWait();
                refreshGrid();
                sidebarView.refresh(sidebar);
            });
            grid.add(cell, i, 0);
        }
    }

    private String weekLabel() {
        final LocalDate end = weekStart.plusDays(6);
        return weekStart.getDayOfMonth() + "/" + weekStart.getMonthValue()
            + " - " + end.getDayOfMonth() + "/" + end.getMonthValue() + "/" + end.getYear();
    }

    private boolean isMultiDay(final Event ev) {
        return ev.end() != null && !ev.end().isBlank() && !ev.start().equals(ev.end())
            && !ev.end().startsWith(ev.start().substring(0, 10));
    }

    private String formatTime(final Event ev) {
        if (ev.start() == null || !ev.start().contains("T")) return "";
        return ev.start().substring(11, 16);
    }

    private String formatRange(final Event ev) {
        final String s = ev.start().substring(0, 10).replace("-", "/").substring(5).replace("/", "/");
        final String e = ev.end().substring(0, 10).replace("-", "/").substring(5).replace("/", "/");
        return s + "→" + e;
    }

    private boolean isLight(final String hex) {
        final int r = Integer.parseInt(hex.substring(1, 3), 16);
        final int g = Integer.parseInt(hex.substring(3, 5), 16);
        final int b = Integer.parseInt(hex.substring(5, 7), 16);
        return (r * 299 + g * 587 + b * 114) / 1000 > 128;
    }

    private LocalDate startOfWeek(final LocalDate d) {
        return d.minusDays(d.getDayOfWeek().getValue() - 1);
    }
}
