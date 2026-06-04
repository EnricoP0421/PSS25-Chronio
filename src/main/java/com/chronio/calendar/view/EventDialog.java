package com.chronio.calendar.view;

import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

public final class EventDialog extends Dialog<Void> {

    public EventDialog(final Stage owner, final LocalDate date) {
        initOwner(owner);
        setTitle("Nuovo evento");

        final TextField titleField = new TextField();
        titleField.setPromptText("Titolo");

        final VBox content = new VBox(8, titleField);
        content.setPrefWidth(300);
        getDialogPane().setContent(content);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    }
}
