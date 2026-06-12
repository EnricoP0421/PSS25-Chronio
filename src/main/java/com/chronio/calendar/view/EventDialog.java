package com.chronio.calendar.view;

import com.chronio.calendar.controller.CalendarController;
import com.chronio.calendar.model.Event;
import com.chronio.calendar.model.Tag;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class EventDialog extends Dialog<Void> {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public EventDialog(final Stage owner, final CalendarController controller, final LocalDate date) {
        this(owner, controller, date, null);
    }

    public EventDialog(final Stage owner, final CalendarController controller, final LocalDate date, final Event existing) {
        initOwner(owner);
        setTitle(existing == null ? "Nuovo evento" : "Modifica evento");

        final TextField titleField = new TextField(existing != null ? existing.title() : "");
        titleField.setPromptText("Titolo");

        final LocalDate startDate0 = existing != null ? parseDate(existing.start(), date) : date;
        final LocalDate endDate0 = existing != null && existing.end() != null ? parseDate(existing.end(), date) : date;
        final int startH = existing != null ? parseHour(existing.start(), 9) : 9;
        final int startM = existing != null ? parseMin(existing.start(), 0) : 0;
        final int endH = existing != null && existing.end() != null ? parseHour(existing.end(), 10) : 10;
        final int endM = existing != null && existing.end() != null ? parseMin(existing.end(), 0) : 0;

        final DatePicker startDate = new DatePicker(startDate0);
        final Spinner<Integer> startHour = new Spinner<>(0, 23, startH);
        final Spinner<Integer> startMin = new Spinner<>(0, 59, startM, 15);
        startHour.setEditable(true);
        startMin.setEditable(true);
        startHour.setPrefWidth(70);
        startMin.setPrefWidth(70);

        final DatePicker endDate = new DatePicker(endDate0);
        final Spinner<Integer> endHour = new Spinner<>(0, 23, endH);
        final Spinner<Integer> endMin = new Spinner<>(0, 59, endM, 15);
        endHour.setEditable(true);
        endMin.setEditable(true);
        endHour.setPrefWidth(70);
        endMin.setPrefWidth(70);

        startHour.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) endHour.getValueFactory().setValue(Math.min(newVal + 1, 23));
        });

        final ComboBox<Tag> tagCombo = new ComboBox<>();
        tagCombo.getItems().add(null);
        tagCombo.getItems().addAll(controller.getTags().values());
        tagCombo.setConverter(new javafx.util.StringConverter<Tag>() {
            @Override public String toString(final Tag t) { return t == null ? "(nessun tag)" : t.name(); }
            @Override public Tag fromString(final String s) { return null; }
        });
        tagCombo.getSelectionModel().selectFirst();
        if (existing != null && existing.tagId() != null) {
            controller.getTags().values().stream()
                .filter(t -> t.id().equals(existing.tagId()))
                .findFirst()
                .ifPresent(tagCombo.getSelectionModel()::select);
        }

        final CheckBox allDayBox = new CheckBox("Tutto il giorno");
        allDayBox.setSelected(existing != null && existing.allDay());

        final HBox startRow = new HBox(4, startDate, startHour, startMin);
        final HBox endRow = new HBox(4, endDate, endHour, endMin);
        allDayBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            startHour.setVisible(!newVal);
            startMin.setVisible(!newVal);
            endHour.setVisible(!newVal);
            endMin.setVisible(!newVal);
        });
        if (existing != null && existing.allDay()) {
            startHour.setVisible(false);
            startMin.setVisible(false);
            endHour.setVisible(false);
            endMin.setVisible(false);
        }

        final VBox content = new VBox(8,
            titleField,
            allDayBox,
            new Label("Inizio:"), startRow,
            new Label("Fine:"),   endRow,
            new Label("Tag:"), tagCombo
        );
        content.setPrefWidth(400);
        getDialogPane().setContent(content);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        if (existing != null) {
            final ButtonType deleteType = new ButtonType("Elimina", ButtonBar.ButtonData.LEFT);
            getDialogPane().getButtonTypes().add(deleteType);
            final Button deleteBtn = (Button) getDialogPane().lookupButton(deleteType);
            deleteBtn.setStyle("-fx-text-fill: red;");
        }

        setResultConverter(btn -> {
            if (btn == ButtonType.OK && !titleField.getText().isBlank()) {
                final boolean allDay = allDayBox.isSelected();
                final String start = allDay
                    ? startDate.getValue().toString()
                    : String.format("%sT%02d:%02d", startDate.getValue(), startHour.getValue(), startMin.getValue());
                final String end = allDay
                    ? endDate.getValue().toString()
                    : String.format("%sT%02d:%02d", endDate.getValue(), endHour.getValue(), endMin.getValue());
                final Tag selectedTag = tagCombo.getValue();
                final String tagId = selectedTag != null ? selectedTag.id() : null;
                if (existing == null) {
                    controller.createEvent(titleField.getText(), "", start, end, tagId, allDay);
                } else {
                    controller.updateEvent(existing.id(), titleField.getText(), "", start, end, tagId, allDay);
                }
            } else if (btn != null && btn.getButtonData() == ButtonBar.ButtonData.LEFT) {
                controller.deleteEvent(existing.id());
            }
            return null;
        });
    }

    private LocalDate parseDate(final String dt, final LocalDate fallback) {
        try { return LocalDateTime.parse(dt, FMT).toLocalDate(); }
        catch (final Exception e) { return fallback; }
    }

    private int parseHour(final String dt, final int fallback) {
        try { return LocalDateTime.parse(dt, FMT).getHour(); }
        catch (final Exception e) { return fallback; }
    }

    private int parseMin(final String dt, final int fallback) {
        try { return LocalDateTime.parse(dt, FMT).getMinute(); }
        catch (final Exception e) { return fallback; }
    }
}
