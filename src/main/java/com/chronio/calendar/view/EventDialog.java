package com.chronio.calendar.view;

import com.chronio.calendar.controller.CalendarController;
import com.chronio.calendar.model.Event;
import com.chronio.calendar.model.Tag;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Dialog per la creazione e modifica di un evento.
 * Supporta tre modalità di apertura: nuovo evento su una data, nuovo evento con ora preimpostata,
 * e modifica di un evento esistente.
 */
public final class EventDialog extends Dialog<Void> {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final int SPINNER_WIDTH = 70;
    private static final int MAX_HOUR = 23;
    private static final int TAG_DOT_RADIUS = 6;
    private static final int DIALOG_WIDTH = 400;
    private static final int CONTENT_SPACING = 8;
    private static final int ROW_SPACING = 4;
    private static final int DEFAULT_START_HOUR = 9;
    private static final int DEFAULT_END_HOUR = 10;
    private static final int MINUTE_STEP = 15;
    private static final int MAX_MINUTE = 59;

    /**
     * Apre il dialog per creare un nuovo evento tutto il giorno sulla data specificata.
     *
     * @param owner      lo stage proprietario
     * @param controller il controller del calendario
     * @param date       la data selezionata
     */
    public EventDialog(final Stage owner, final CalendarController controller, final LocalDate date) {
        this(owner, controller, date, -1, null);
    }

    /**
     * Apre il dialog per modificare un evento esistente.
     *
     * @param owner      lo stage proprietario
     * @param controller il controller del calendario
     * @param date       la data selezionata
     * @param existing   l'evento da modificare
     */
    public EventDialog(final Stage owner, final CalendarController controller,
                       final LocalDate date, final Event existing) {
        this(owner, controller, date, -1, existing);
    }

    /**
     * Costruttore principale del dialog.
     *
     * @param owner       lo stage proprietario
     * @param controller  il controller del calendario
     * @param date        la data selezionata
     * @param presetHour  ora preimpostata, -1 se non specificata
     * @param existing    l'evento da modificare, null se nuovo
     */
    public EventDialog(final Stage owner, final CalendarController controller,
                       final LocalDate date, final int presetHour, final Event existing) {
        initOwner(owner);
        setTitle(existing == null ? "Nuovo evento" : "Modifica evento");

        final TextField titleField = new TextField(existing != null ? existing.title() : "");
        titleField.setPromptText("Titolo");

        final LocalDate startDate0 = existing != null ? parseDate(existing.start(), date) : date;
        final LocalDate endDate0 = existing != null && existing.end() != null
            ? parseDate(existing.end(), date) : date;
        final int startH = existing != null
            ? parseHour(existing.start(), DEFAULT_START_HOUR)
            : presetHour >= 0 ? presetHour : DEFAULT_START_HOUR;
        final int startM = existing != null ? parseMin(existing.start(), 0) : 0;
        final int endH = existing != null && existing.end() != null
            ? parseHour(existing.end(), DEFAULT_END_HOUR)
            : presetHour >= 0 ? Math.min(presetHour + 1, MAX_HOUR) : DEFAULT_END_HOUR;
        final int endM = existing != null && existing.end() != null ? parseMin(existing.end(), 0) : 0;

        final DatePicker startDate = new DatePicker(startDate0);
        final Spinner<Integer> startHour = new Spinner<>(0, MAX_HOUR, startH);
        final Spinner<Integer> startMin = new Spinner<>(0, MAX_MINUTE, startM, MINUTE_STEP);
        startHour.setEditable(true);
        startMin.setEditable(true);
        startHour.setPrefWidth(SPINNER_WIDTH);
        startMin.setPrefWidth(SPINNER_WIDTH);

        final DatePicker endDate = new DatePicker(endDate0);
        final Spinner<Integer> endHour = new Spinner<>(0, MAX_HOUR, endH);
        final Spinner<Integer> endMin = new Spinner<>(0, MAX_MINUTE, endM, MINUTE_STEP);
        endHour.setEditable(true);
        endMin.setEditable(true);
        endHour.setPrefWidth(SPINNER_WIDTH);
        endMin.setPrefWidth(SPINNER_WIDTH);

        startHour.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                endHour.getValueFactory().setValue(Math.min(newVal + 1, MAX_HOUR));
            }
        });

        final ComboBox<Tag> tagCombo = new ComboBox<>();
        tagCombo.getItems().add(null);
        tagCombo.getItems().addAll(controller.getTags().values());
        tagCombo.setConverter(new javafx.util.StringConverter<Tag>() {
            @Override
            public String toString(final Tag t) {
                return t == null ? "(nessun tag)" : t.name();
            }

            @Override
            public Tag fromString(final String s) {
                return null;
            }
        });
        tagCombo.setCellFactory(lv -> makeTagCell());
        tagCombo.setButtonCell(makeTagCell());
        if (existing != null && existing.tagId() != null) {
            controller.getTags().values().stream()
                .filter(t -> t.id().equals(existing.tagId()))
                .findFirst()
                .ifPresent(tagCombo.getSelectionModel()::select);
        }

        final CheckBox allDayBox = new CheckBox("Tutto il giorno");
        allDayBox.setSelected(existing != null && existing.allDay());

        final HBox startRow = new HBox(ROW_SPACING, startDate, startHour, startMin);
        final HBox endRow = new HBox(ROW_SPACING, endDate, endHour, endMin);
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

        final TextArea descField = new TextArea(existing != null ? existing.description() : "");
        descField.setPromptText("Descrizione");
        descField.setPrefRowCount(3);

        final VBox content = new VBox(CONTENT_SPACING,
            titleField,
            allDayBox,
            new Label("Inizio:"), startRow,
            new Label("Fine:"), endRow,
            new Label("Descrizione:"), descField,
            new Label("Tag:"), tagCombo
        );
        content.setPrefWidth(DIALOG_WIDTH);
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
                    controller.createEvent(titleField.getText(), descField.getText(), start, end, tagId, allDay);
                } else {
                    controller.updateEvent(existing.id(), titleField.getText(), descField.getText(), start, end, tagId, allDay);
                }
            } else if (btn != null && btn.getButtonData() == ButtonBar.ButtonData.LEFT && existing != null) {
                controller.deleteEvent(existing.id());
            }
            return null;
        });
    }

    private ListCell<Tag> makeTagCell() {
        return new ListCell<Tag>() {
            @Override
            protected void updateItem(final Tag t, final boolean empty) {
                super.updateItem(t, empty);
                if (empty || t == null) {
                    setText("(nessun tag)");
                    setGraphic(null);
                } else {
                    setText(t.name());
                    setGraphic(new Circle(TAG_DOT_RADIUS, Color.web(t.color())));
                }
            }
        };
    }

    private LocalDate parseDate(final String dt, final LocalDate fallback) {
        try {
            return LocalDateTime.parse(dt, FMT).toLocalDate();
        } catch (final DateTimeParseException e) {
            return fallback;
        }
    }

    private int parseHour(final String dt, final int fallback) {
        try {
            return LocalDateTime.parse(dt, FMT).getHour();
        } catch (final DateTimeParseException e) {
            return fallback;
        }
    }

    private int parseMin(final String dt, final int fallback) {
        try {
            return LocalDateTime.parse(dt, FMT).getMinute();
        } catch (final DateTimeParseException e) {
            return fallback;
        }
    }
}
