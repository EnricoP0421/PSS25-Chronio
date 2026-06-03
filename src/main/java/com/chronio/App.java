package com.chronio;

import com.chronio.calendar.controller.CalendarControllerImpl;
import com.chronio.calendar.model.CalendarData;
import com.chronio.calendar.model.CalendarModelImpl;
import com.chronio.calendar.persistence.CalendarPersistence;
import com.chronio.calendar.view.CalendarView;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(final Stage stage) {
        final CalendarPersistence persistence = new CalendarPersistence(CalendarPersistence.getDefaultPath());
        final CalendarModelImpl model = new CalendarModelImpl(persistence.load());
        final CalendarControllerImpl controller = new CalendarControllerImpl(model, persistence);
        stage.setTitle("Chronio");
        stage.setScene(new CalendarView(controller, stage).build());
        stage.show();
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
