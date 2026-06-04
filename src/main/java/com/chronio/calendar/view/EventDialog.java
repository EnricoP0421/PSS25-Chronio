package com.chronio.calendar.view;

import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

public final class EventDialog extends Dialog<Void> {

    public EventDialog(final Stage owner, final LocalDate date) {
        initOwner(owner);
        setTitle("Nuovo evento");

        final TextField titleField = new TextField();
        titleField.setPromptText("Titolo");

        final DatePicker startDate = new DatePicker(date);
        final Spinner<Integer> startHour = new Spinner<>(0, 23, 9);
        final Spinner<Integer> startMin = new Spinner<>(0, 59, 0, 15);
        startHour.setPrefWidth(70);
        startMin.setPrefWidth(70);

        final DatePicker endDate = new DatePicker(date);
        final Spinner<Integer> endHour = new Spinner<>(0, 23, 10);
        final Spinner<Integer> endMin = new Spinner<>(0, 59, 0, 15);
        endHour.setPrefWidth(70);
        endMin.setPrefWidth(70);

        final VBox content = new VBox(8,
            titleField,
            new Label("Inizio:"), new HBox(4, startDate, startHour, startMin),
            new Label("Fine:"),   new HBox(4, endDate, endHour, endMin)
        );
        content.setPrefWidth(400);
        getDialogPane().setContent(content);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    }
}
