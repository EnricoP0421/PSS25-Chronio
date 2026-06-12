package com.chronio.calendar.view;

import com.chronio.calendar.controller.CalendarController;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public final class TagSidebarView {

    private final CalendarController controller;
    private final Stage stage;
    private final Runnable onTagChanged;

    public TagSidebarView(final CalendarController controller, final Stage stage, final Runnable onTagChanged) {
        this.controller = controller;
        this.stage = stage;
        this.onTagChanged = onTagChanged;
    }

    public VBox build() {
        final VBox box = new VBox(8);
        box.setPrefWidth(200);
        box.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 12;");

        final Label title = new Label("Tags");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        final Button addBtn = new Button("+");
        addBtn.setOnAction(e -> openTagDialog(box));

        final HBox header = new HBox(8, title, addBtn);
        header.setAlignment(Pos.CENTER_LEFT);

        box.getChildren().add(header);
        refreshList(box);
        return box;
    }

    private void openTagDialog(final VBox box) {
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
                refreshList(box);
            }
            return null;
        });
        dialog.showAndWait();
    }

    private void refreshList(final VBox box) {
        box.getChildren().subList(1, box.getChildren().size()).clear();
        controller.getTags().forEach((id, tag) -> {
            final Circle dot = new Circle(6, Color.web(tag.color()));
            final Label lbl = new Label(tag.name());
            final Button editBtn = new Button("✎");
            final Button delBtn = new Button("✕");
            delBtn.setStyle("-fx-text-fill: red;");
            editBtn.setOnAction(e -> openEditDialog(box, tag.id(), tag.name(), tag.color()));
            delBtn.setOnAction(e -> { controller.deleteTag(tag.id()); refreshList(box); onTagChanged.run(); });
            final HBox row = new HBox(6, dot, lbl, editBtn, delBtn);
            row.setAlignment(Pos.CENTER_LEFT);
            box.getChildren().add(row);
        });
    }

    private void openEditDialog(final VBox box, final String id, final String currentName, final String currentColor) {
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
                refreshList(box);
                onTagChanged.run();
            }
            return null;
        });
        dialog.showAndWait();
    }
}
