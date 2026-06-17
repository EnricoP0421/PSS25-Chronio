package com.chronio.calendar.view;

import java.time.LocalDate;
import java.time.YearMonth;

import com.chronio.calendar.controller.CalendarController;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Vista mensile del calendario con pulsante per navigazione tra mesi, settimana e giorno
 */
public final class CalendarView {

    private static final String VIEW_MONTH = "month";
    private static final String VIEW_WEEK = "week";
    private static final String VIEW_DAY = "day";
    private static final int DAYS_IN_WEEK = 7;
    private static final int NAV_SPACING = 12;
    private static final int CELL_SPACING = 2;
    private static final int PADDING = 8;
    private static final int HEADER_SPACING = 4;
    private static final int GRID_GAP = 4;
    private static final String[] DAYS_IT = {"Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom",};
    private static final int GRID_ROWS = 6;
    private static final String[] MONTHS_IT = {
        "Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno",
        "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"
    };

    private final CalendarController controller;
    private final Stage stage;
    private final EventSidebarView sidebarView;
    private final VBox sidebar;
    private YearMonth displayMonth = YearMonth.now();
    private GridPane grid;
    private VBox mainBox;
    private HBox nav;
    private String currentView = VIEW_MONTH;
    private Button toggleBtn;

    public CalendarView(final CalendarController controller, final Stage stage,
                        final EventSidebarView sidebarView, final VBox sidebar) {
        this.controller = controller;
        this.stage = stage;
        this.sidebarView = sidebarView;
        this.sidebar = sidebar;
    }

    private String monthLabel() {
        return MONTHS_IT[displayMonth.getMonthValue() - 1] + " " + displayMonth.getYear();
    }

    /**
     * Costruisce e restituisce il nodo radice della vista mensile
     * @return VBox che contiene la navigazione e la griglia del mese
     */
    public VBox build() {
        mainBox = new VBox(PADDING);
        mainBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        mainBox.setPadding(new Insets(PADDING));
        VBox.setVgrow(mainBox, Priority.ALWAYS);
        grid = buildGrid();
        nav = buildNav();
        mainBox.getChildren().addAll(nav, buildDayHeaders(), grid);
        VBox.setVgrow(grid, Priority.ALWAYS);
        return mainBox;
    }

    /**
     * Aggiorna la griglia del mese corrente, se la vista attiva è quella mensile
     */
    public void refresh() {
        if (VIEW_MONTH.equals(currentView)) {
            refreshGrid(grid);
        }
    }

    private HBox buildDayHeaders() {
        final HBox box = new HBox(HEADER_SPACING);
        for (final String day : DAYS_IT) {
            final Label lbl = new Label(day);
            lbl.setMaxWidth(Double.MAX_VALUE);
            lbl.setAlignment(Pos.CENTER);
            HBox.setHgrow(lbl, Priority.ALWAYS);
            box.getChildren().add(lbl);
        }
        return box;
    }

    private HBox buildNav() {
        final Button prev = new Button("<");
        final Button next = new Button(">");
        final Label label = new Label(monthLabel());
        if (toggleBtn == null) {
            toggleBtn = new Button("Settimana");
        }
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
        toggleBtn.setOnAction(e -> {
            switch (currentView) {
                case VIEW_MONTH -> {
                    currentView = VIEW_WEEK;
                    toggleBtn.setText("Giorno");
                    final VBox weekBox = new WeekView(controller, stage, sidebarView, sidebar).build();
                    mainBox.getChildren().setAll(nav, weekBox);
                    VBox.setVgrow(weekBox, Priority.ALWAYS);
                }
                case VIEW_WEEK -> {
                    currentView = VIEW_DAY;
                    toggleBtn.setText("Mese");
                    final VBox dayBox = new DayView(controller, stage, sidebarView, sidebar).build();
                    mainBox.getChildren().setAll(nav, dayBox);
                    VBox.setVgrow(dayBox, Priority.ALWAYS);
                }
                default -> {
                    currentView = VIEW_MONTH;
                    toggleBtn.setText("Settimana");
                    mainBox.getChildren().setAll(nav, buildDayHeaders(), grid);
                    VBox.setVgrow(grid, Priority.ALWAYS);
                    refreshGrid(grid);
                }
            }
        });
        final HBox navBar = new HBox(NAV_SPACING, prev, label, next, toggleBtn);
        navBar.setAlignment(Pos.CENTER_LEFT);
        return navBar;
    }

    private void refreshGrid(final GridPane calGrid) {
        calGrid.getChildren().clear();
        calGrid.getColumnConstraints().clear();
        calGrid.getRowConstraints().clear();

        for (int i = 0; i < DAYS_IN_WEEK; i++) {
            final ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / DAYS_IN_WEEK);
            cc.setFillWidth(true);
            calGrid.getColumnConstraints().add(cc);
        }

        for (int i = 0; i < GRID_ROWS; i++) {
            final RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(100.0 / GRID_ROWS);
            rc.setFillHeight(true);
            calGrid.getRowConstraints().add(rc);
        }

        final LocalDate today = LocalDate.now();
        final LocalDate first = displayMonth.atDay(1);
        final int startCol = first.getDayOfWeek().getValue() - 1;
        final int daysInMonth = displayMonth.lengthOfMonth();

        int col = startCol;
        int row = 0;
        for (int day = 1; day <= daysInMonth; day++) {
            final LocalDate date = displayMonth.atDay(day);
            final boolean isToday = date.equals(today);
            final VBox cell = new VBox(CELL_SPACING);
            cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            cell.setStyle(isToday
                ? "-fx-border-color: gray; -fx-background-color: lightblue; -fx-padding: 4;"
                : "-fx-border-color: gray; -fx-padding: 4;");
            cell.getChildren().add(new Label(String.valueOf(day)));
            final String dateKey = date.getYear() + "-" + date.getMonthValue() + "-" + date.getDayOfMonth();
            controller.getEventsForDate(dateKey).forEach(ev ->
                cell.getChildren().add(ViewUtils.makePill(ev, date, controller, stage, () -> {
                    refreshGrid(calGrid);
                    sidebarView.refresh(sidebar);
                }))
            );
            cell.setOnMouseClicked(e -> {
                new EventDialog(stage, controller, date).showAndWait();
                refreshGrid(calGrid);
                sidebarView.refresh(sidebar);
            });
            calGrid.add(cell, col, row);
            col++;
            if (col == DAYS_IN_WEEK) {
                col = 0;
                row++;
            }
        }
    }

    private GridPane buildGrid() {
        final GridPane g = new GridPane();
        g.setHgap(GRID_GAP);
        g.setVgap(GRID_GAP);
        g.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        refreshGrid(g);
        return g;
    }
}
