package com.chronio.kanban.view;

import com.chronio.kanban.controller.BoardController;
import com.chronio.kanban.model.Board;
import com.chronio.kanban.model.Card;
import com.chronio.kanban.model.Column;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public final class BoardInsideView {

    private static final int PADDING = 16;
    private static final int SPACING = 12;
    private static final int COLUMN_WIDTH = 220;
    private static final int COLUMN_SPACING = 12;
    private static final int CARD_SPACING = 6;

    private final BoardController controller;
    private final String boardId;
    private final Runnable onBack;
    private StackPane container;

    public BoardInsideView(final BoardController controller, final String boardId, final Runnable onBack) {
        this.controller = controller;
        this.boardId = boardId;
        this.onBack = onBack;
    }

    public StackPane build() {
        container = new StackPane();
        container.getChildren().setAll(buildContent());
        return container;
    }

    private BorderPane buildContent() {
        final Board board = controller.getBoards().get(boardId);
        final BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");
        root.setPadding(new Insets(PADDING));

        final Button backBtn = new Button("← Bacheche");
        backBtn.setOnAction(e -> onBack.run());

        final Label title = new Label(board != null ? board.title() : "");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black;");

        final HBox header = new HBox(SPACING, backBtn, title);
        header.setPadding(new Insets(0, 0, PADDING, 0));
        header.setAlignment(Pos.CENTER_LEFT);
        root.setTop(header);
        root.setCenter(buildColumnsArea(board));
        return root;
    }

    private ScrollPane buildColumnsArea(final Board board) {
        final HBox columnsBox = new HBox(COLUMN_SPACING);
        columnsBox.setPadding(new Insets(PADDING));
        columnsBox.setStyle("-fx-background-color: white;");

        if (board != null) {
            board.columns().values().forEach(col -> columnsBox.getChildren().add(buildColumnCard(col)));
        }
        columnsBox.getChildren().add(buildAddColumnCard());

        final ScrollPane sp = new ScrollPane(columnsBox);
        sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color: white; -fx-background: white;");
        return sp;
    }

    private VBox buildColumnCard(final Column col) {
        final Label title = new Label(col.title());
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");

        final Button delCol = new Button("✕");
        delCol.setStyle("-fx-background-color: transparent; -fx-text-fill: #e74c3c; -fx-cursor: hand;");
        delCol.setOnAction(e -> {
            controller.deleteColumn(boardId, col.id());
            refresh();
        });

        final HBox colHeader = new HBox(8, title, delCol);
        colHeader.setAlignment(Pos.CENTER_LEFT);

        final VBox cardsBox = new VBox(CARD_SPACING);
        col.cards().values().forEach(card -> cardsBox.getChildren().add(buildCardItem(col.id(), card)));

        final Button addCard = new Button("+ Aggiungi card");
        addCard.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-border-color: gray; -fx-border-style: dashed; -fx-text-fill: black;");
        addCard.setMaxWidth(Double.MAX_VALUE);
        addCard.setOnAction(e -> {
            final TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText(null);
            dialog.setContentText("Titolo card:");
            dialog.showAndWait().filter(s -> !s.isBlank()).ifPresent(name -> {
                controller.createCard(boardId, col.id(), name, "", null);
                refresh();
            });
        });

        final VBox column = new VBox(CARD_SPACING, colHeader, cardsBox, addCard);
        column.setPrefWidth(COLUMN_WIDTH);
        column.setPadding(new Insets(PADDING));
        column.setStyle("-fx-border-color: gray; -fx-padding: 8; -fx-background-color: white;");
        return column;
    }

    private HBox buildCardItem(final String columnId, final Card card) {
        final javafx.scene.control.CheckBox check = new javafx.scene.control.CheckBox();
        check.setSelected(card.completed());
        check.setOnAction(e -> {
            controller.toggleCard(boardId, columnId, card.id());
            refresh();
        });

        final Label lbl = new Label(card.title());
        lbl.setStyle("-fx-text-fill: black;" + (card.completed() ? " -fx-strikethrough: true; -fx-opacity: 0.5;" : ""));
        lbl.setWrapText(true);
        lbl.setMaxWidth(Double.MAX_VALUE);

        final Button del = new Button("✕");
        del.setStyle("-fx-background-color: transparent; -fx-text-fill: #e74c3c; -fx-cursor: hand; -fx-font-size: 10px;");
        del.setOnAction(e -> {
            controller.deleteCard(boardId, columnId, card.id());
            refresh();
        });

        final HBox row = new HBox(4, check, lbl, del);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-border-color: gray; -fx-padding: 4; -fx-background-color: white;");
        return row;
    }

    private VBox buildAddColumnCard() {
        final Label lbl = new Label("+ Aggiungi colonna");
        lbl.setStyle("-fx-font-weight: 600; -fx-text-fill: black;");

        final VBox card = new VBox(lbl);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(COLUMN_WIDTH);
        card.setPrefHeight(60);
        card.setStyle("-fx-border-color: gray; -fx-border-style: dashed; -fx-padding: 8; -fx-cursor: hand; -fx-background-color: white;");
        card.setOnMouseClicked(e -> {
            final TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText(null);
            dialog.setContentText("Nome colonna:");
            dialog.showAndWait().filter(s -> !s.isBlank()).ifPresent(name -> {
                controller.createColumn(boardId, name);
                refresh();
            });
        });
        return card;
    }

    private void refresh() {
        container.getChildren().setAll(buildContent());
    }
}
