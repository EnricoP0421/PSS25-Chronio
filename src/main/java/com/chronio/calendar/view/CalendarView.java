package com.chronio.calendar.view;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public final class CalendarView {

    public Scene build() {
        final BorderPane root = new BorderPane();
        return new Scene(root, 1200, 700);
    }
}
