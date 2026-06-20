package com.chronio.kanban.view;

import com.chronio.kanban.controller.BoardController;
import com.chronio.kanban.model.Board;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
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

        final TextField editor = new TextField(board.title());
        editor.setVisible(false);
        editor.setManaged(false);

        final Runnable save = () -> {
            final String val = editor.getText().trim();
            if (!val.isBlank()) {
                controller.renameBoard(board.id(), val);
            }
            showBoardList();
        };
        editor.setOnAction(e -> save.run());
        editor.focusedProperty().addListener((obs, o, focused) -> { if (!focused) save.run(); });
        editor.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ESCAPE) showBoardList(); });

        title.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                e.consume();
                title.setVisible(false); title.setManaged(false);
                editor.setVisible(true); editor.setManaged(true);
                editor.selectAll();
                editor.requestFocus();
            }
        });

        final Button editBtn = new Button("✎");
        editBtn.setOnAction(e -> {
            title.setVisible(false); title.setManaged(false);
            editor.setVisible(true); editor.setManaged(true);
            editor.selectAll();
            editor.requestFocus();
        });

        final Button del = new Button("✕");
        del.setStyle("-fx-text-fill: red;");
        del.setOnAction(e -> { controller.deleteBoard(board.id()); showBoardList(); });

        final HBox btnRow = new HBox(4, editBtn, del);
        btnRow.setAlignment(Pos.CENTER);
        final VBox card = new VBox(8, title, editor, btnRow);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        card.setStyle(CARD_STYLE);
        card.setOnMouseClicked(e -> { if (e.getClickCount() == 1 && title.isVisible()) showBoardDetail(board.id()); });
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
