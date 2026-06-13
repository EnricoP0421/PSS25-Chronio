package com.chronio.calendar.view;

import com.chronio.calendar.controller.CalendarController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
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
        final EventSidebarView sidebarView = new EventSidebarView(controller);
        final VBox sidebar = sidebarView.build();
        final CalendarView calendarView = new CalendarView(controller, stage, sidebarView, sidebar);
        root.setLeft(new TagSidebarView(controller, stage, () -> { calendarView.refresh(); sidebarView.refresh(sidebar); }).build());
        root.setCenter(calendarView.build());
        root.setRight(sidebar);
        return new Scene(root, 1200, 700);
    }
}
