package com.chronio.calendar.view;

import com.chronio.calendar.controller.CalendarController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public final class TagSidebarView {

    private final CalendarController controller;
    private final Stage stage;

    public TagSidebarView(final CalendarController controller, final Stage stage) {
        this.controller = controller;
        this.stage = stage;
    }

    public VBox build() {
        final VBox box = new VBox(8);
        box.setPrefWidth(200);
        box.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 12;");

        final Label title = new Label("Tags");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        final Button addBtn = new Button("+");
        addBtn.setOnAction(e -> openTagDialog());

        final HBox header = new HBox(8, title, addBtn);
        header.setAlignment(Pos.CENTER_LEFT);

        box.getChildren().add(header);
        return box;
    }

    private void openTagDialog() {
        final Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(stage);
        dialog.setTitle("Nuovo tag");

        final TextField nameField = new TextField();
        nameField.setPromptText("Nome tag");

        final VBox content = new VBox(8, new Label("Nome:"), nameField);
        content.setPrefWidth(250);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK && !nameField.getText().isBlank()) {
                controller.createTag(nameField.getText(), "#888888");
            }
            return null;
        });
        dialog.showAndWait();
    }
}
