package com.chronio;

import com.chronio.calendar.controller.CalendarControllerImpl;
import com.chronio.calendar.model.CalendarModelImpl;
import com.chronio.calendar.persistence.CalendarPersistence;
import com.chronio.calendar.view.MainView;
import com.chronio.kanban.controller.BoardControllerImpl;
import com.chronio.kanban.persistence.BoardPersistence;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Punto di ingresso dell'applicazione Chronio.
 */
public final class App extends Application {

    /**
     * Inizializza e mostra la finestra principale.
     * @param stage lo stage principale fornito da JavaFX
     */
    @Override
    public void start(final Stage stage) {
        final CalendarPersistence calPersistence = new CalendarPersistence(CalendarPersistence.getDefaultPath());
        final CalendarModelImpl model = new CalendarModelImpl(calPersistence.load());
        final CalendarControllerImpl calController = new CalendarControllerImpl(model, calPersistence);

        final BoardPersistence boardPersistence = new BoardPersistence(BoardPersistence.getDefaultPath());
        final BoardControllerImpl boardController = new BoardControllerImpl(boardPersistence.load(), boardPersistence);

        stage.setTitle("Chronio");
        stage.setScene(new MainView(calController, boardController, stage).build());
        stage.show();
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
