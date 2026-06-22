package com.chronio.calendar.view;

import com.chronio.budget.view.BudgetView;
import com.chronio.calendar.controller.CalendarController;
import com.chronio.kanban.controller.BoardController;
import com.chronio.kanban.view.BoardView;
import com.chronio.shared.NavBar;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Vista principale dell'applicazione. Assembla le tre aree principali (tag, calendario, oggi/questa settimana)
 * in un BorderPane e restituisce la Scene pronta per lo stage
 */
public final class MainView {

    private static final int SCENE_WIDTH = 1200;
    private static final int SCENE_HEIGHT = 700;

    private final CalendarController calController;
    private final BoardController boardController;
    private final BudgetView budgetView;
    private final Stage stage;

    /**
     * Costruisce la vista principale
     * @param calController il controller del calendario
     * @param boardController il controller delle bacheche
     * @param budgetView la vista del budget
     * @param stage lo stage principale
     */
    public MainView(final CalendarController calController, final BoardController boardController,
                    final BudgetView budgetView, final Stage stage) {
        this.calController = calController;
        this.boardController = boardController;
        this.budgetView = budgetView;
        this.stage = stage;
    }

     /**
     * Costruisce e restituisce la scena principale dell'applicazione
     * @return Scene (1200 x 700)
     */
    public Scene build() {
        final BorderPane root = new BorderPane();

        final EventSidebarView sidebarView = new EventSidebarView(calController);
        final VBox sidebar = sidebarView.build();
        final CalendarView calendarView = new CalendarView(calController, stage, sidebarView, sidebar);
        final TagSidebarView tagSidebar = new TagSidebarView(calController, stage, () -> {
            calendarView.refresh();
            sidebarView.refresh(sidebar);
        });

        final BoardView boardView = new BoardView(boardController);
        final StackPane boardPane = boardView.build();

        final Runnable showCalendar = () -> {
            root.setStyle("");
            root.setLeft(tagSidebar.build());
            root.setCenter(calendarView.build());
            root.setRight(sidebar);
        };
        final Runnable showBoard = () -> {
            root.setStyle("");
            root.setLeft(null);
            root.setCenter(boardPane);
            root.setRight(null);
        };
        final Runnable showBudget = () -> {
            root.setStyle("");
            root.setLeft(null);
            root.setCenter(budgetView);
            root.setRight(null);
        };

        final NavBar navBar = new NavBar(showCalendar, showBoard, showBudget);
        root.setTop(navBar.build());
        showCalendar.run();

        return new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
    }
}
