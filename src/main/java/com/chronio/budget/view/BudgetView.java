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
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.DatePicker;

public final class BudgetView extends HBox implements BudgetController.View {

    private final BudgetController controller;

    private final VBox incomeList = new VBox(6);
    private final VBox expenseList = new VBox(6);
    private final DatePicker fromPicker = new DatePicker();
    private final DatePicker toPicker = new DatePicker();
    private final Label incomeTotalLabel = new Label();
    private final Label expenseTotalLabel = new Label();
    private final Label balanceLabel = new Label();
    private final PieChart pieChart = new PieChart();
    private final LineChart<String, Number> lineChart;

    public BudgetView(final BudgetController controller) {
        this.controller = controller;
        controller.setView(this);

        setSpacing(12);
        setPadding(new Insets(12));

        final CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Mese");
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Saldo netto");
        this.lineChart = new LineChart<>(xAxis, yAxis);
        this.lineChart.setTitle("Andamento saldo mensile");
        this.lineChart.setLegendVisible(false);

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
        final HBox header = new HBox(8, title, spacer());
        header.setAlignment(Pos.CENTER_LEFT);

        final Label fromLabel = new Label("Da:");
        fromLabel.setMinWidth(Region.USE_PREF_SIZE);
        final Label toLabel = new Label("A:");
        toLabel.setMinWidth(Region.USE_PREF_SIZE);

        final HBox periodBox = new HBox(8, fromLabel, fromPicker, toLabel, toPicker);
        periodBox.setAlignment(Pos.CENTER_LEFT);

        final VBox summaryBox = new VBox(4, incomeTotalLabel, expenseTotalLabel, balanceLabel);
        summaryBox.setPadding(new Insets(8, 0, 8, 0));

        pieChart.setTitle("Uscite per categoria");
        pieChart.setPrefHeight(240);
        lineChart.setPrefHeight(240);

        final VBox panel = new VBox(10, header, periodBox, summaryBox, pieChart, lineChart);
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