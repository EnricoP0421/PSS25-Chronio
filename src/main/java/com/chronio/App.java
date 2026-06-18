package com.chronio;

import com.chronio.calendar.controller.CalendarControllerImpl;
import com.chronio.calendar.model.CalendarModelImpl;
import com.chronio.calendar.persistence.CalendarPersistence;
import com.chronio.calendar.view.MainView;

import javafx.application.Application;
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
        final CalendarPersistence persistence = new CalendarPersistence(CalendarPersistence.getDefaultPath());
        final CalendarModelImpl model = new CalendarModelImpl(persistence.load());
        final CalendarControllerImpl controller = new CalendarControllerImpl(model, persistence);
        stage.setTitle("Chronio");
        stage.setScene(new MainView(controller, stage).build());
        stage.show();
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
