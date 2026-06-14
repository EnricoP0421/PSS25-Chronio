package com.chronio.calendar.view;

import com.chronio.calendar.controller.CalendarController;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
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

    private final CalendarController controller;
    private final Stage stage;
    private final Runnable onTagChanged;

    public TagSidebarView(final CalendarController controller, final Stage stage, final Runnable onTagChanged) {
        this.controller = controller;
        this.stage = stage;
        this.onTagChanged = onTagChanged;
    }

    /**
     * Costruisce e restituisce il nodo radice della sidebar dei tag.
     * @return VBox con l'elenco dei tag e i controlli di gestione
     */
    public VBox build() {
        final VBox box = new VBox(8);
        box.setPrefWidth(200);
        box.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 12;");

        final Label title = new Label("Tags");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        final VBox tagList = new VBox(8);

        final Button burgerBtn = new Button("☰");
        burgerBtn.setOnAction(e -> tagList.setVisible(!tagList.isVisible()));

        final Button addBtn = new Button("+");
        addBtn.setOnAction(e -> openTagDialog(tagList));

        final HBox header = new HBox(8, burgerBtn, title, addBtn);
        header.setAlignment(Pos.CENTER_LEFT);

        box.getChildren().addAll(header, tagList);
        refreshList(tagList);
        return box;
    }

    private void openTagDialog(final VBox tagList) {
        final Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(stage);
        dialog.setTitle("Nuovo tag");

        final TextField nameField = new TextField();
        nameField.setPromptText("Nome tag");
        final ColorPicker colorPicker = new ColorPicker(Color.web("#888888"));

        final VBox content = new VBox(8, new Label("Nome:"), nameField, new Label("Colore:"), colorPicker);
        content.setPrefWidth(250);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK && !nameField.getText().isBlank()) {
                final Color c = colorPicker.getValue();
                final String hex = String.format("#%02x%02x%02x",
                    (int)(c.getRed()*255), (int)(c.getGreen()*255), (int)(c.getBlue()*255));
                controller.createTag(nameField.getText(), hex);
                refreshList(tagList);
            }
            return null;
        });
        dialog.showAndWait();
    }

    private void refreshList(final VBox tagList) {
        tagList.getChildren().clear();
        controller.getTags().forEach((id, tag) -> {
            final CheckBox cb = new CheckBox();
            cb.setSelected(tag.visible());
            cb.setOnAction(e -> { controller.toggleTagVisibility(tag.id()); onTagChanged.run(); });
            final Circle dot = new Circle(6, Color.web(tag.color()));
            final Label lbl = new Label(tag.name());
            final Button editBtn = new Button("✎");
            final Button delBtn = new Button("✕");
            delBtn.setStyle("-fx-text-fill: red;");
            editBtn.setOnAction(e -> openEditDialog(tagList, tag.id(), tag.name(), tag.color()));
            delBtn.setOnAction(e -> { controller.deleteTag(tag.id()); refreshList(tagList); onTagChanged.run(); });
            final HBox row = new HBox(6, cb, dot, lbl, editBtn, delBtn);
            row.setAlignment(Pos.CENTER_LEFT);
            tagList.getChildren().add(row);
        });
    }

    private void openEditDialog(final VBox tagList, final String id, final String currentName, final String currentColor) {
        final Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(stage);
        dialog.setTitle("Modifica tag");

        final TextField nameField = new TextField(currentName);
        final ColorPicker colorPicker = new ColorPicker(Color.web(currentColor));

        final VBox content = new VBox(8, new Label("Nome:"), nameField, new Label("Colore:"), colorPicker);
        content.setPrefWidth(250);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK && !nameField.getText().isBlank()) {
                final Color c = colorPicker.getValue();
                final String hex = String.format("#%02x%02x%02x",
                    (int)(c.getRed()*255), (int)(c.getGreen()*255), (int)(c.getBlue()*255));
                controller.updateTag(id, nameField.getText(), hex);
                refreshList(tagList);
                onTagChanged.run();
            }
            return null;
        });
        dialog.showAndWait();
    }
}
