package com.chronio.calendar.view;

import com.chronio.calendar.controller.CalendarController;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public final class CalendarView {

    private final CalendarController controller;
    private final Stage stage;

    public CalendarView(final CalendarController controller, final Stage stage) {
        this.controller = controller;
        this.stage = stage;
    }

    public VBox build() {
        final VBox box = new VBox(8);
        return box;
    }
}
