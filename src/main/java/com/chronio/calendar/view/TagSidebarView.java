package com.chronio.calendar.view;

import com.chronio.calendar.controller.CalendarController;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * Barra lateral per la gestione dei tag.
 * Permette di creare, modificare, eliminare e mostrare/nascondere i tag
 */
public final class TagSidebarView {

    private static final int SIDEBAR_WIDTH = 200;
    private static final int DIALOG_WIDTH = 250;
    private static final int BOX_SPACING = 8;
    private static final int ROW_SPACING = 6;
    private static final int TAG_DOT_RADIUS = 6;
    private static final int MAX_COLOR_VALUE = 255;

    private final CalendarController controller;
    private final Stage stage;
    private final Runnable onTagChanged;

    /**
     * Costruisce la sidebar.
     * @param controller il controller del calendario
     * @param stage lo stage principale
     * @param onTagChanged callback eseguita quando un tag viene modificato
     */
    public TagSidebarView(final CalendarController controller, final Stage stage, final Runnable onTagChanged) {
        this.controller = controller;
        this.stage = stage;
        this.onTagChanged = onTagChanged;
    }

    /**
     * Costruisce e restituisce il nodo root della sidebar dei tag
     * @return VBox con l'elenco dei tag e i controlli di gestione
     */
    public VBox build() {
        final VBox box = new VBox(BOX_SPACING);
        box.setPrefWidth(SIDEBAR_WIDTH);
        box.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 12;");

        final Label title = new Label("Tags");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        final VBox tagList = new VBox(BOX_SPACING);

        final Button burgerBtn = new Button("☰");
        burgerBtn.setOnAction(e -> tagList.setVisible(!tagList.isVisible()));

        final Button addBtn = new Button("+");
        addBtn.setOnAction(e -> openTagDialog(tagList));

        final HBox header = new HBox(BOX_SPACING, burgerBtn, title, addBtn);
        header.setAlignment(Pos.CENTER_LEFT);

        final ScrollPane scroll = new ScrollPane(tagList);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, javafx.scene.layout.Priority.ALWAYS);
        box.getChildren().addAll(header, scroll);
        refreshList(tagList);
        return box;
    }

    private void openTagDialog(final VBox tagList) {
        final TextField nameField = new TextField();
        nameField.setPromptText("Nome tag");
        final ColorPicker colorPicker = new ColorPicker(Color.web("#888888"));
        showColorDialog("Nuovo tag", nameField, colorPicker, () -> {
            controller.createTag(nameField.getText(), toHex(colorPicker.getValue()));
            refreshList(tagList);
        });
    }

    private void refreshList(final VBox tagList) {
        tagList.getChildren().clear();
        controller.getTags().forEach((id, tag) -> {
            final CheckBox cb = new CheckBox();
            cb.setSelected(tag.visible());
            cb.setOnAction(e -> {
                controller.toggleTagVisibility(tag.id());
                onTagChanged.run();
            });
            final Circle dot = new Circle(TAG_DOT_RADIUS, Color.web(tag.color()));
            final Label lbl = new Label(tag.name());
            final Button editBtn = new Button("✎");
            final Button delBtn = new Button("✕");
            delBtn.setStyle("-fx-text-fill: red;");
            editBtn.setOnAction(e -> openEditDialog(tagList, tag.id(), tag.name(), tag.color()));
            delBtn.setOnAction(e -> {
                controller.deleteTag(tag.id());
                refreshList(tagList);
                onTagChanged.run();
            });
            final HBox row = new HBox(ROW_SPACING, cb, dot, lbl, editBtn, delBtn);
            row.setAlignment(Pos.CENTER_LEFT);
            tagList.getChildren().add(row);
        });
    }

    private void openEditDialog(final VBox tagList, final String id,
                                 final String currentName, final String currentColor) {
        final TextField nameField = new TextField(currentName);
        final ColorPicker colorPicker = new ColorPicker(Color.web(currentColor));
        showColorDialog("Modifica tag", nameField, colorPicker, () -> {
            controller.updateTag(id, nameField.getText(), toHex(colorPicker.getValue()));
            refreshList(tagList);
            onTagChanged.run();
        });
    }

    private void showColorDialog(final String title, final TextField nameField,
                                  final ColorPicker colorPicker, final Runnable onOk) {
        final Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(stage);
        dialog.setTitle(title);
        final VBox content = new VBox(BOX_SPACING, new Label("Nome:"), nameField, new Label("Colore:"), colorPicker);
        content.setPrefWidth(DIALOG_WIDTH);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK && !nameField.getText().isBlank()) {
                onOk.run();
            }
            return null;
        });
        dialog.showAndWait();
    }

    private String toHex(final Color c) {
        return String.format("#%02x%02x%02x",
            Math.round(c.getRed() * MAX_COLOR_VALUE),
            Math.round(c.getGreen() * MAX_COLOR_VALUE),
            Math.round(c.getBlue() * MAX_COLOR_VALUE));
    }
}
