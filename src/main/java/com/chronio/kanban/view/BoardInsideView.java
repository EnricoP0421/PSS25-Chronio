package com.chronio.kanban.view;

import com.chronio.kanban.controller.BoardController;
import com.chronio.kanban.model.Board;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public final class BoardInsideView {

    private static final int PADDING = 16;
    private static final int SPACING = 12;

    private final BoardController controller;
    private final String boardId;
    private final Runnable onBack;

    public BoardInsideView(final BoardController controller, final String boardId, final Runnable onBack) {
        this.controller = controller;
        this.boardId = boardId;
        this.onBack = onBack;
    }

    public BorderPane build() {
        final Board board = controller.getBoards().get(boardId);
        final BorderPane root = new BorderPane();
        root.setPadding(new Insets(PADDING));

        final Button backBtn = new Button("← Bacheche");
        backBtn.setOnAction(e -> onBack.run());

        final Label title = new Label(board != null ? board.title() : "");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        final HBox header = new HBox(SPACING, backBtn, title);
        header.setPadding(new Insets(0, 0, PADDING, 0));

        root.setTop(header);
        return root;
    }
}
