package com.chronio.calendar.view;

import com.chronio.calendar.controller.CalendarController;
import com.chronio.shared.NavBar;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Vista principale dell'applicazione. Assembla le tre aree principali (tag, calendario, oggi/questa settimana)
 * in un BorderPane e restituisce la Scene pronta per lo stage.
 */
public final class MainView {

    private static final int SCENE_WIDTH = 1200;
    private static final int SCENE_HEIGHT = 700;

    private final CalendarController controller;
    private final Stage stage;

    public MainView(final CalendarController controller, final Stage stage) {
        this.controller = controller;
        this.stage = stage;
    }

    /**
     * Costruisce e restituisce la scena principale dell'applicazione
     * @return Scene (1200 x 700)
     */
    public Scene build() {
        final BorderPane root = new BorderPane();
        final EventSidebarView sidebarView = new EventSidebarView(controller);
        final VBox sidebar = sidebarView.build();
        final CalendarView calendarView = new CalendarView(controller, stage, sidebarView, sidebar);

        final NavBar navBar = new NavBar(() -> { }, () -> { });
        root.setTop(navBar.build());
        root.setLeft(new TagSidebarView(controller, stage, () -> {
            calendarView.refresh();
            sidebarView.refresh(sidebar);
        }).build());
        root.setCenter(calendarView.build());
        root.setRight(sidebar);
        return new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
    }
}
