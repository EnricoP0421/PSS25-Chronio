package com.chronio.calendar.view;

import javafx.scene.layout.VBox;

public final class EventSidebarView {

    public VBox build() {
        final VBox box = new VBox(12);
        box.setPrefWidth(250);
        return box;
    }
}
