package com.chronio.kanban.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.chronio.kanban.controller.BoardController;
import com.chronio.kanban.model.Board;
import com.chronio.kanban.model.Card;
import com.chronio.kanban.model.Column;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

/**
 * Vista interna di una bacheca: mostra colonne, card e sidebar tag.
 */
public final class BoardInsideView {

    private static final int PADDING = 16;
    private static final int SPACING = 12;
    private static final int COLUMN_WIDTH = 220;
    private static final int COLUMN_SPACING = 12;
    private static final int CARD_SPACING = 6;
    private static final int TAG_DOT_RADIUS = 6;
    private static final int ADD_COLUMN_HEIGHT = 60;

    private final BoardController controller;
    private final String boardId;
    private final Runnable onBack;
    private final Set<String> activeTagIds = new HashSet<>();
    private StackPane container;

    /**
     * Costruisce la vista interna
     * @param controller il controller delle bacheche
     * @param boardId id della bacheca da visualizzare
     * @param onBack callback eseguita al click su "Bacheche"
     */
    public BoardInsideView(final BoardController controller, final String boardId, final Runnable onBack) {
        this.controller = controller;
        this.boardId = boardId;
        this.onBack = onBack;
    }

    /**
     * Costruisce e restituisce il nodo radice della vista.
     * @return StackPane con il contenuto della bacheca
     */
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

        final KanbanTagSidebarView sidebarView = new KanbanTagSidebarView(controller, activeTagIds, this::refresh);
        root.setLeft(sidebarView.build());
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

        final TextField editor = new TextField(col.title());
        editor.setVisible(false);
        editor.setManaged(false);

        final Runnable save = () -> {
            final String val = editor.getText().trim();
            if (!val.isBlank()) {
                controller.renameColumn(boardId, col.id(), val);
            }
            refresh();
        };
        editor.setOnAction(e -> save.run());
        editor.focusedProperty().addListener((obs, o, focused) -> {
            if (!focused) {
                save.run();
            }
        });
        editor.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                refresh();
            }
        });

        final Button editCol = new Button("✎");
        editCol.setOnAction(e -> {
            title.setVisible(false);
            title.setManaged(false);
            editor.setVisible(true);
            editor.setManaged(true);
            editor.selectAll();
            editor.requestFocus();
        });

        final Button delCol = new Button("✕");
        delCol.setStyle("-fx-text-fill: red;");
        delCol.setOnAction(e -> {
            controller.deleteColumn(boardId, col.id());
            refresh();
        });

        final HBox colHeader = new HBox(4, title, editor, editCol, delCol);
        colHeader.setAlignment(Pos.CENTER_LEFT);

        final VBox cardsBox = new VBox(CARD_SPACING);
        col.cards().values().forEach(card -> {
            final List<String> ids = card.tagIds() != null ? card.tagIds() : List.of();
            if (activeTagIds.isEmpty() || ids.stream().anyMatch(activeTagIds::contains)) {
                cardsBox.getChildren().add(buildCardItem(col.id(), card));
            }
        });

        final Button addCard = new Button("+ Aggiungi card");
        addCard.setMaxWidth(Double.MAX_VALUE);
        addCard.setOnAction(e -> {
            final TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText(null);
            dialog.setContentText("Titolo card:");
            dialog.showAndWait().filter(s -> !s.isBlank()).ifPresent(name -> {
                controller.createCard(boardId, col.id(), name, "", List.of());
                refresh();
            });
        });

        cardsBox.setPadding(new Insets(0, 8, 0, 0));

        final ScrollPane cardsScroll = new ScrollPane(cardsBox);
        cardsScroll.setFitToWidth(true);
        cardsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        cardsScroll.setStyle("-fx-background-color: white; -fx-background: white;");
        VBox.setVgrow(cardsScroll, Priority.ALWAYS);

        final VBox column = new VBox(CARD_SPACING, colHeader, cardsScroll, addCard);
        column.setPrefWidth(COLUMN_WIDTH);
        column.setPadding(new Insets(PADDING));
        column.setStyle("-fx-border-color: gray; -fx-padding: 8; -fx-background-color: white;");
        column.setOnDragOver(e -> {
            if (e.getGestureSource() != column && e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });
        column.setOnDragDropped(e -> {
            final Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                final String[] parts = db.getString().split(":", 2);
                final String fromColumn = parts[0];
                final String cardId = parts[1];
                controller.moveCard(boardId, fromColumn, col.id(), cardId, -1);
                refresh();
                success = true;
            }
            e.setDropCompleted(success);
            e.consume();
        });
        return column;
    }

    private VBox buildCardItem(final String columnId, final Card card) {
        final CheckBox check = new CheckBox();
        check.setSelected(card.completed());
        check.setOnAction(e -> {
            controller.toggleCard(boardId, columnId, card.id());
            refresh();
        });

        final Label lbl = new Label(card.title());
        lbl.setStyle("-fx-text-fill: black;" + (card.completed() ? " -fx-strikethrough: true; -fx-opacity: 0.5;" : ""));
        lbl.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(lbl, Priority.ALWAYS);

        if (card.description() != null && !card.description().isBlank()) {
            final Tooltip tip = new Tooltip(card.description());
            tip.setShowDelay(Duration.ZERO);
            lbl.setTooltip(tip);
        }

        final HBox dots = buildTagDots(card);

        final Button editBtn = new Button("✎");
        editBtn.setOnAction(e -> openCardEditDialog(columnId, card));

        final Button del = new Button("✕");
        del.setStyle("-fx-text-fill: red;");
        del.setOnAction(e -> {
            controller.deleteCard(boardId, columnId, card.id());
            refresh();
        });

        final HBox topRow = new HBox(4, check, lbl, editBtn, del);
        topRow.setAlignment(Pos.CENTER_LEFT);

        final VBox cardBox = new VBox(2, topRow, dots);
        cardBox.setStyle("-fx-border-color: gray; -fx-padding: 4; -fx-background-color: white;");
        cardBox.setOnDragDetected(e -> {
            final Dragboard db = cardBox.startDragAndDrop(TransferMode.MOVE);
            final ClipboardContent content = new ClipboardContent();
            content.putString(columnId + ":" + card.id());
            db.setContent(content);
            e.consume();
        });

        cardBox.setOnDragOver(e -> {
            if (e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });
        cardBox.setOnDragDropped(e -> {
            final Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                final String[] parts = db.getString().split(":", 2);
                final VBox parent = (VBox) cardBox.getParent();
                final int index = parent.getChildren().indexOf(cardBox);
                controller.moveCard(boardId, parts[0], columnId, parts[1], index);
                refresh();
                success = true;
            }
            e.setDropCompleted(success);
            e.consume();
        });
        return cardBox;
    }

    private HBox buildTagDots(final Card card) {
        final HBox dots = new HBox(2);
        dots.setAlignment(Pos.CENTER_LEFT);
        (card.tagIds() != null ? card.tagIds() : List.of()).forEach(tid -> {
            final var tag = controller.getTags().get((String) tid);
            if (tag != null) {
                final Circle dot = new Circle(TAG_DOT_RADIUS, Color.web(tag.color()));
                final Tooltip tip = new Tooltip(tag.name());
                tip.setShowDelay(Duration.ZERO);
                Tooltip.install(dot, tip);
                dots.getChildren().add(dot);
            }
        });
        return dots;
    }

    private void openCardEditDialog(final String columnId, final Card card) {
        final Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Modifica card");

        final TextField titleField = new TextField(card.title());
        final TextArea descField = new TextArea(card.description() != null ? card.description() : "");
        descField.setPromptText("Descrizione");
        descField.setPrefRowCount(3);

        final List<String> selected = new ArrayList<>(card.tagIds() != null ? card.tagIds() : List.of());
        final VBox tagBox = new VBox(CARD_SPACING);
        controller.getTags().forEach((id, tag) -> {
            final CheckBox cb = new CheckBox(tag.name());
            cb.setSelected(selected.contains(id));
            cb.setOnAction(e -> {
                if (cb.isSelected()) {
                    selected.add(id);
                } else {
                    selected.remove(id);
                }
            });
            final Circle dot = new Circle(TAG_DOT_RADIUS, Color.web(tag.color()));
            final HBox row = new HBox(6, dot, cb);
            row.setAlignment(Pos.CENTER_LEFT);
            tagBox.getChildren().add(row);
        });

        final VBox content = new VBox(CARD_SPACING,
            new Label("Titolo:"), titleField,
            new Label("Descrizione:"), descField,
            new Label("Tag:"), tagBox);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK && !titleField.getText().isBlank()) {
                controller.updateCard(boardId, columnId, card.id(),
                    titleField.getText(), descField.getText(), selected);
            }
            return null;
        });
        dialog.showAndWait();
        refresh();
    }

    private VBox buildAddColumnCard() {
        final Label lbl = new Label("+ Aggiungi colonna");
        lbl.setStyle("-fx-font-weight: 600; -fx-text-fill: black;");

        final VBox card = new VBox(lbl);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(COLUMN_WIDTH);
        card.setPrefHeight(ADD_COLUMN_HEIGHT);
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
