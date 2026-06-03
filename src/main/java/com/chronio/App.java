package com.chronio;

import com.chronio.budget.controller.BudgetController;
import com.chronio.budget.model.BudgetService;
import com.chronio.budget.persistence.JsonBudgetRepository;
import com.chronio.budget.view.BudgetView;
import com.chronio.calendar.controller.CalendarControllerImpl;
import com.chronio.calendar.model.CalendarModelImpl;
import com.chronio.calendar.persistence.CalendarPersistence;
import com.chronio.calendar.view.MainView;
<<<<<<< HEAD
import com.chronio.kanban.controller.BoardControllerImpl;
import com.chronio.kanban.persistence.BoardPersistence;

=======
>>>>>>> a167bd8 (rename CalendarView to MainView, CalendarCenterView to CalendarView)
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Punto di ingresso dell'applicazione Chronio.
 */
public final class App extends Application {

    /**
     * Inizializza e mostra la finestra principale.
     *
     * @param stage lo stage principale fornito da JavaFX
     */
    @Override
    public void start(final Stage stage) {
        final CalendarPersistence calPersistence = new CalendarPersistence(CalendarPersistence.getDefaultPath());
        final CalendarModelImpl calModel = new CalendarModelImpl(calPersistence.load());
        final CalendarControllerImpl calController = new CalendarControllerImpl(calModel, calPersistence);

        final BoardPersistence boardPersistence = new BoardPersistence(BoardPersistence.getDefaultPath());
        final BoardControllerImpl boardController = new BoardControllerImpl(boardPersistence.load(), boardPersistence);

        final BudgetController budgetController = new BudgetController(new BudgetService(new JsonBudgetRepository()));
        final BudgetView budgetView = new BudgetView(budgetController);

        stage.setTitle("Chronio");
<<<<<<< HEAD
        stage.setScene(new MainView(calController, boardController, budgetView, stage).build());
=======
        stage.setScene(new MainView(controller, stage).build());
>>>>>>> a167bd8 (rename CalendarView to MainView, CalendarCenterView to CalendarView)
        stage.show();
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
