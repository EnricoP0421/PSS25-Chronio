package com.chronio.kanban.view;

import com.chronio.kanban.controller.BoardController;
import com.chronio.kanban.model.Board;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public final class BoardView {

    private static final int PADDING = 24;
    private static final int CARD_WIDTH = 200;
    private static final int CARD_HEIGHT = 100;
    private static final int GRID_SPACING = 16;
    private static final String CARD_STYLE =
        "-fx-border-color: gray; -fx-padding: 4; -fx-cursor: hand;";
    private static final String ADD_CARD_STYLE =
        "-fx-border-color: gray; -fx-border-style: dashed; -fx-padding: 4; -fx-cursor: hand;";

    private final BoardController controller;
    private final StackPane root;

    public BoardView(final BoardController controller) {
        this.controller = controller;
        this.root = new StackPane();
    }

    public StackPane build() {
        showBoardList();
        return root;
    }

    private void showBoardList() {
        final FlowPane grid = new FlowPane(GRID_SPACING, GRID_SPACING);
        grid.setPadding(new Insets(PADDING));

        controller.getBoards().values().forEach(board -> grid.getChildren().add(buildBoardCard(board)));
        grid.getChildren().add(buildAddCard());

        final BorderPane page = new BorderPane();
        page.setCenter(grid);
        root.getChildren().setAll(page);
    }

    private VBox buildBoardCard(final Board board) {
        final Label title = new Label(board.title());
        title.setStyle("-fx-font-weight: bold;");

        final Button del = new Button("✕");
        del.setStyle("-fx-background-color: transparent; -fx-text-fill: #e74c3c; -fx-cursor: hand;");
        del.setOnAction(e -> {
            controller.deleteBoard(board.id());
            showBoardList();
        });

        final VBox card = new VBox(8, title, del);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        card.setStyle(CARD_STYLE);
        card.setOnMouseClicked(e -> showBoardDetail(board.id()));
        return card;
    }

    private VBox buildAddCard() {
        final Label lbl = new Label("+ Nuova Bacheca");
        lbl.setStyle("-fx-font-weight: 600;");

        final VBox card = new VBox(lbl);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        card.setStyle(ADD_CARD_STYLE);
        card.setOnMouseClicked(e -> {
            final TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText(null);
            dialog.setContentText("Nome bacheca:");
            dialog.showAndWait().filter(s -> !s.isBlank()).ifPresent(name -> {
                controller.createBoard(name);
                showBoardList();
            });
        });
        return card;
    }

    private void showBoardDetail(final String boardId) {
        final BoardInsideView detail = new BoardInsideView(controller, boardId, this::showBoardList);
        root.getChildren().setAll(detail.build());
    }
}
