package com.chronio.budget.view;

import com.chronio.budget.controller.BudgetController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public final class BudgetView extends HBox implements BudgetController.View {

    private final BudgetController controller;

    private final VBox incomeList = new VBox(6);
    private final VBox expenseList = new VBox(6);

    public BudgetView(final BudgetController controller) {
        this.controller = controller;
        controller.setView(this);

        setSpacing(12);
        setPadding(new Insets(12));

        getChildren().addAll(
                buildPanel("Entrate", incomeList),
                buildPanel("Uscite", expenseList),
                buildTotalPanel());

    }

    private Node buildPanel(final String titleText, final VBox list) {
        final Label title = sectionTitle(titleText);
        final Button add = new Button("+");
        add.setOnAction(e -> System.out.println("Aggiungi in " + titleText));

        final HBox header = new HBox(8, title, spacer(), add);
        header.setAlignment(Pos.CENTER_LEFT);

        final ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        final VBox panel = new VBox(8, header, scroll);
        panel.setPadding(new Insets(8));
        HBox.setHgrow(panel, Priority.ALWAYS);
        panel.setPrefWidth(280);
        return panel;
    }

    private Node buildTotalPanel() {
        final Label title = sectionTitle("Totale");
        final VBox panel = new VBox(10, title);
        panel.setPadding(new Insets(8));
        panel.setPrefWidth(380);
        return panel;
    }

    //BudgetController.View

    @Override
    public void refreshTransactionLists() {
        // TODO: popolare incomeList ed expenseList dai dati del controller
    }

    @Override
    public void refreshCharts() {
        // TODO: aggiornare totali e grafici
    }

    //Util

    private static Label sectionTitle(final String text) {
        final Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        return label;
    }

    private static Region spacer() {
        final Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        return region;
    }
}