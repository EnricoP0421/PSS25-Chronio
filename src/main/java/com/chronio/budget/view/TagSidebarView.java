package com.chronio.budget.view;
 
import java.util.Set;
import java.util.function.Consumer;
 
import com.chronio.budget.controller.BudgetController;
import com.chronio.budget.model.Tag;
 
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
 
/**
 * Sidebar per la gestione e il filtro dei tag categoria del budget.
 * Permette di creare, modificare ed eliminare i tag e di filtrare le
 * transazioni mostrando solo quelle con i tag selezionati. Coerente con
 * le sidebar tag di calendario e bacheche.
 */
public final class TagSidebarView {
 
    private static final int SIDEBAR_WIDTH = 200;
    private static final int BOX_SPACING = 8;
    private static final int ROW_SPACING = 6;
    private static final int TAG_DOT_RADIUS = 6;
    private static final int MAX_COLOR_VALUE = 255;
 
    private final BudgetController controller;
    private final Set<String> activeTagIds;
    private final Runnable onFilterChanged;
 
    /**
     * Costruisce la sidebar.
     *
     * @param controller      il controller del budget
     * @param activeTagIds    set dei tag attivi per il filtro
     * @param onFilterChanged callback eseguita quando il filtro cambia
     */
    public TagSidebarView(final BudgetController controller,
                          final Set<String> activeTagIds,
                          final Runnable onFilterChanged) {
        this.controller = controller;
        this.activeTagIds = activeTagIds;
        this.onFilterChanged = onFilterChanged;
    }
 
    /**
     * Costruisce e restituisce il nodo radice della sidebar.
     *
     * @return VBox con header e lista tag
     */
    public VBox build() {
        final VBox box = new VBox(BOX_SPACING);
        box.setPrefWidth(SIDEBAR_WIDTH);
        box.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 12;");

        // Selezione periodo di tempo
        final DatePicker fromPicker = new DatePicker(controller.getPeriodStart());
        final DatePicker toPicker = new DatePicker(controller.getPeriodEnd());
        fromPicker.setMaxWidth(Double.MAX_VALUE);
        toPicker.setMaxWidth(Double.MAX_VALUE);
        fromPicker.setOnAction(e -> controller.onPeriodChanged(fromPicker.getValue(), toPicker.getValue()));
        toPicker.setOnAction(e -> controller.onPeriodChanged(fromPicker.getValue(), toPicker.getValue()));

        final Label periodTitle = new Label("Periodo");
        periodTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        final VBox periodSection = new VBox(4,
            periodTitle,
            new Label("Da:"), fromPicker,
            new Label("A:"), toPicker);
        periodSection.setStyle("-fx-padding: 0 0 12 0;");
 
        final Label title = new Label("Tags");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
 
        final VBox tagList = new VBox(BOX_SPACING);
 
        final Button burgerBtn = new Button("\u2630");
        burgerBtn.setFocusTraversable(false);
        burgerBtn.setOnAction(e -> {
            tagList.setVisible(!tagList.isVisible());
            tagList.setManaged(tagList.isVisible());
        });
 
        final Button addBtn = new Button("+");
        addBtn.setOnAction(e -> openTagDialog(tagList));
 
        final HBox header = new HBox(BOX_SPACING, burgerBtn, title, addBtn);
        header.setAlignment(Pos.CENTER_LEFT);
 
        final ScrollPane scroll = new ScrollPane(tagList);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        box.getChildren().addAll(periodSection, header, scroll);
        refreshList(tagList);
        return box;
    }
 
    private void refreshList(final VBox tagList) {
        tagList.getChildren().clear();
        for (final Tag tag : controller.getAllTags()) {
            final String id = tag.id();
            final CheckBox cb = new CheckBox();
            cb.setSelected(activeTagIds.contains(id));
            cb.setOnAction(e -> {
                if (cb.isSelected()) {
                    activeTagIds.add(id);
                } else {
                    activeTagIds.remove(id);
                }
                onFilterChanged.run();
            });
            final Circle dot = new Circle(TAG_DOT_RADIUS, Color.web(tag.color()));
            final Label lbl = new Label(tag.name());
            final Button editBtn = new Button("\u270e");
            final Button delBtn = new Button("\u2715");
            delBtn.setStyle("-fx-text-fill: red;");
            editBtn.setOnAction(e -> openEditDialog(tagList, id, tag.name(), tag.color()));
            delBtn.setOnAction(e -> {
                activeTagIds.remove(id);
                controller.onRemoveTag(id);
                refreshList(tagList);
                onFilterChanged.run();
            });
            final HBox row = new HBox(ROW_SPACING, cb, dot, lbl, editBtn, delBtn);
            row.setAlignment(Pos.CENTER_LEFT);
            tagList.getChildren().add(row);
        }
    }
 
    private void openTagDialog(final VBox tagList) {
        final TextField nameField = new TextField();
        nameField.setPromptText("Nome tag");
        final ColorPicker colorPicker = new ColorPicker(Color.web("#888888"));
        showDialog("Nuovo tag", nameField, colorPicker, hex -> {
            controller.onAddTag(nameField.getText(), hex);
            refreshList(tagList);
            onFilterChanged.run();
        });
    }
 
    private void openEditDialog(final VBox tagList, final String id,
                                final String currentName, final String currentColor) {
        final TextField nameField = new TextField(currentName);
        final ColorPicker colorPicker = new ColorPicker(Color.web(currentColor));
        showDialog("Modifica tag", nameField, colorPicker, hex -> {
            controller.onUpdateTag(id, nameField.getText(), hex);
            refreshList(tagList);
            onFilterChanged.run();
        });
    }
 
    private void showDialog(final String title, final TextField nameField,
                            final ColorPicker colorPicker, final Consumer<String> onOk) {
        final Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(title);
        final VBox content = new VBox(BOX_SPACING,
            new Label("Nome:"), nameField,
            new Label("Colore:"), colorPicker);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK && !nameField.getText().isBlank()) {
                onOk.accept(toHex(colorPicker.getValue()));
            }
            return null;
        });
        dialog.showAndWait();
    }
 
    private String toHex(final Color c) {
        return String.format("#%02x%02x%02x",
            Math.round(c.getRed() * MAX_COLOR_VALUE),
            Math.round(c.getGreen() * MAX_COLOR_VALUE),
            Math.round(c.getBlue() * MAX_COLOR_VALUE));
    }
}