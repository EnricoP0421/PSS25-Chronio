package com.chronio.budget.view;

import com.chronio.budget.controller.BudgetController;
import com.chronio.budget.model.Tag;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


/**
 * Finestra per la gestione dei tag categoria. Mostra l'elenco dei tag
 * esistenti con possibilità di rimozione e offre una riga per aggiungerne
 * di nuovi indicando nome e colore. Le modifiche sono inoltrate al
 * controller, che aggiorna a sua volta la vista principale.
 */
public final class TagManagerDialog extends Dialog<Void> {

    private final BudgetController controller;
    private final VBox tagList = new VBox(6);

    private final TextField nameField = new TextField();
    private final ColorPicker colorPicker = new ColorPicker(Color.web("#3b82f6"));

    /**
     * Crea la finestra di gestione tag e popola l'elenco di quelli esistenti.
     *
     * @param controller il controller a cui inoltrare aggiunta e rimozione dei tag
     */
    public TagManagerDialog(final BudgetController controller) {
        this.controller = controller;

        setTitle("Gestione tag");
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        nameField.setPromptText("Nome categoria");

        final Button addButton = new Button("Aggiungi");
        addButton.setOnAction(e -> handleAdd());

        final HBox addRow = new HBox(8, nameField, colorPicker, addButton);
        addRow.setAlignment(Pos.CENTER_LEFT);

        final VBox content = new VBox(12,
                new Label("Tag esistenti:"), tagList,
                new Label("Nuovo tag:"), addRow);
        content.setPadding(new Insets(12));
        content.setPrefWidth(360);

        getDialogPane().setContent(content);

        refreshTagList();
    }

    private void refreshTagList() {
        tagList.getChildren().clear();
        for (final Tag tag : controller.getAllTags()) {
            tagList.getChildren().add(tagRow(tag));
        }
    }

    private HBox tagRow(final Tag tag) {
        final Circle dot = new Circle(6);
        dot.setFill(BudgetView.parseColor(tag.color()));
        final Label name = new Label(tag.name());

        final Button remove = new Button("Rimuovi");
        remove.setOnAction(e -> {
            controller.onRemoveTag(tag.id());
            refreshTagList();
        });

        final HBox row = new HBox(8, dot, name, spacer(), remove);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void handleAdd() {
        final String name = nameField.getText();
        if (name == null || name.isBlank()) {
            return; // niente nome: non aggiungere
        }
        controller.onAddTag(name.trim(), toHex(colorPicker.getValue()));
        nameField.clear();
        refreshTagList();
    }

    // Converte un Color JavaFX nella stringa hex "#rrggbb" attesa dal model.
    private static String toHex(final Color c) {
        return String.format("#%02x%02x%02x",
                Math.round(c.getRed() * 255),
                Math.round(c.getGreen() * 255),
                Math.round(c.getBlue() * 255));
    }

    private static Region spacer() {
        final Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        return region;
    }
}