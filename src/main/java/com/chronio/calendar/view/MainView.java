package com.chronio.calendar.view;

import com.chronio.calendar.controller.CalendarController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public final class MainView {

    private final CalendarController controller;
    private final Stage stage;

    public MainView(final CalendarController controller, final Stage stage) {
        this.controller = controller;
        this.stage = stage;
    }

    public Scene build() {
        final BorderPane root = new BorderPane();
        root.setLeft(new TagSidebarView().build());
        root.setCenter(new CalendarView(controller, stage).build());
        root.setRight(new EventSidebarView().build());
        return new Scene(root, 1200, 700);
    }
}
