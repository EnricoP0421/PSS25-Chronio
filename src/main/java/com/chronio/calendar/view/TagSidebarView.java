package com.chronio.calendar.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public final class TagSidebarView {

    public VBox build() {
        final VBox box = new VBox(8);
        box.setPrefWidth(200);
        box.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 12;");

        final Label title = new Label("Tags");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        final Button addBtn = new Button("+");

        final HBox header = new HBox(8, title, addBtn);
        header.setAlignment(Pos.CENTER_LEFT);

        box.getChildren().add(header);
        return box;
    }
}
