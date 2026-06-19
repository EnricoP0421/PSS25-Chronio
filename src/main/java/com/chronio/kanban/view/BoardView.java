package com.chronio.kanban.view;

import com.chronio.kanban.controller.BoardController;

import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public final class BoardView {

    private static final int PADDING = 16;

    private final BoardController controller;
    private final HBox columnsBox;

    public BoardView(final BoardController controller) {
        this.controller = controller;
        this.columnsBox = new HBox(PADDING);
        this.columnsBox.setPadding(new Insets(PADDING));
    }

    public BorderPane build() {
        final BorderPane root = new BorderPane();

        HBox.setHgrow(columnsBox, Priority.ALWAYS);
        root.setCenter(columnsBox);
        refresh();
        return root;
    }

    public void refresh() {
        columnsBox.getChildren().clear();
    }
}
