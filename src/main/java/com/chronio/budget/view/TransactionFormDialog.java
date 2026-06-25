package com.chronio.budget.view;

import com.chronio.budget.controller.BudgetController;
import com.chronio.budget.model.Transaction;
import com.chronio.budget.model.TransactionType;
import com.chronio.budget.model.Tag;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

import java.time.LocalDate;

/**
 * Finestra di dialogo per creare o modificare una transazione. Se il
 * parametro {@code existing} del costruttore è null la finestra opera in
 * creazione, altrimenti in modifica (campi precompilati e pulsante Elimina).
 * Permette di impostare descrizione, importo, data e categoria, validando
 * l'input prima del salvataggio.
 */
public final class TransactionFormDialog extends Dialog<Void> {

    private final BudgetController controller;
    private final TransactionType type;
    private final Transaction existing;

    private final TextField descriptionField = new TextField();
    private final TextField amountField = new TextField();
    private final DatePicker datePicker = new DatePicker();
    private final Label errorLabel = new Label();
    private final ComboBox<Tag> tagCombo = new ComboBox<>();

    /**
     * Crea la finestra del form.
     *
     * @param controller il controller a cui inoltrare salvataggio ed eliminazione
     * @param type       tipo della transazione (entrata o uscita)
     * @param existing   transazione da modificare, oppure null per crearne una nuova
     */
    public TransactionFormDialog(final BudgetController controller,
                                 final TransactionType type,
                                 final Transaction existing) {
        this.controller = controller;
        this.type = type;
        this.existing = existing;

        final boolean editing = existing != null;
        final String kind = type == TransactionType.INCOME ? "entrata" : "uscita";
        setTitle((editing ? "Modifica " : "Nuova ") + kind);
        
        // Scelta tag (opzionale)
        tagCombo.getItems().add(null);
        tagCombo.getItems().addAll(controller.getAllTags());
        tagCombo.setConverter(new StringConverter<Tag>() {
            @Override
            public String toString(final Tag tag) {
                return tag == null ? "(nessuna categoria)" : tag.name();
            }
            @Override
            public Tag fromString(final String s) {
                return null;
            }
        });
        tagCombo.getSelectionModel().selectFirst(); 

        // Pulsanti: Salva e Annulla sempre; Elimina solo in modifica.
        final ButtonType saveButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        final ButtonType deleteButton = new ButtonType("Elimina", ButtonBar.ButtonData.LEFT);
        getDialogPane().getButtonTypes().add(saveButton);
        if (editing) {
            getDialogPane().getButtonTypes().add(deleteButton);
        }
        getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        getDialogPane().setContent(buildForm());

        // Precompila i campi se stiamo modificando.
        if (editing) {
            descriptionField.setText(existing.description() == null ? "" : existing.description());
            amountField.setText(String.valueOf(existing.amount()));
            datePicker.setValue(existing.date());
            tagCombo.getSelectionModel().select(controller.getTag(existing.tagId()));
        } else {
            datePicker.setValue(LocalDate.now());
        }

        // Intercetta il click su Salva per validare prima di chiudere.
        getDialogPane().lookupButton(saveButton).addEventFilter(
                javafx.event.ActionEvent.ACTION, e -> {
                    if (!handleSave()) {
                        e.consume();
                    }
                });

        // Elimina: chiede al controller e chiude.
        if (editing) {
            getDialogPane().lookupButton(deleteButton).addEventFilter(
                    javafx.event.ActionEvent.ACTION, e -> handleDelete());
        }
    }

    private GridPane buildForm() {
        final GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setPadding(new Insets(12));

        grid.add(new Label("Descrizione:"), 0, 0);
        grid.add(descriptionField, 1, 0);
        grid.add(new Label("Importo (€):"), 0, 1);
        grid.add(amountField, 1, 1);
        grid.add(new Label("Data:"), 0, 2);
        grid.add(datePicker, 1, 2);
        grid.add(new Label("Categoria:"), 0, 3);
        grid.add(tagCombo, 1, 3); 

        errorLabel.setStyle("-fx-text-fill: #dc2626;");
        grid.add(errorLabel, 0, 4, 2, 1);

        return grid;
    }

    // Valida e salva. Ritorna true se ok (il dialog può chiudersi).
    private boolean handleSave() {
        final String description = descriptionField.getText();
        final LocalDate date = datePicker.getValue();

        if (date == null) {
            errorLabel.setText("Seleziona una data.");
            return false;
        }

        final double amount;
        try {
            amount = Double.parseDouble(amountField.getText().trim().replace(",", "."));
        } catch (final NumberFormatException ex) {
            errorLabel.setText("L'importo non è un numero valido.");
            return false;
        }
        if (amount <= 0) {
            errorLabel.setText("L'importo deve essere positivo.");
            return false;
        }

        final Tag selectedTag = tagCombo.getSelectionModel().getSelectedItem();
        final String tagId = selectedTag == null ? null : selectedTag.id();
        final String id = existing == null ?  null : existing.id();
        controller.onSaveTransaction(id, type, description, amount, date, tagId);
        return true;
    }

    private void handleDelete() {
        controller.onDeleteTransaction(existing.id());
    }
}