package com.chronio.budget.view;

import com.chronio.budget.controller.BudgetController;
import com.chronio.budget.model.Tag;
import com.chronio.budget.model.Transaction;
import com.chronio.budget.model.TransactionType;
import com.chronio.budget.model.BudgetSummary;

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
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;
import java.time.format.DateTimeFormatter;
import java.util.Map;

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
                buildPanel("Entrate", incomeList, TransactionType.INCOME),
                buildPanel("Uscite", expenseList, TransactionType.EXPENSE),
                buildTotalPanel());
        
        refreshTransactionLists();
        refreshCharts();  
    }

    private Node buildPanel(final String titleText, final VBox list, final TransactionType type) {
        final Label title = sectionTitle(titleText);
        final Button add = new Button("+");
        add.setOnAction(e -> openTransactionForm(type, null));

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
        incomeList.getChildren().setAll(rowsFor(controller.getIncomes()));
        expenseList.getChildren().setAll(rowsFor(controller.getExpenses()));
    }

    @Override
    public void refreshCharts() {
        final BudgetSummary summary = controller.getCurrentSummary();
        incomeTotalLabel.setText(String.format("Entrate totali: €%.2f", summary.totalIncome()));
        expenseTotalLabel.setText(String.format("Uscite totali: €%.2f", summary.totalExpenses()));
        balanceLabel.setText(String.format("Saldo: €%.2f", summary.balance()));
        balanceLabel.setTextFill(balanceColor(summary.balance()));

        // PieChart: distribuzione uscite per tag con percentuali.
        final Map<String, Double> byTag = controller.getExpensesByTag();
        final double totalExpenses = byTag.values().stream().mapToDouble(Double::doubleValue).sum();
        pieChart.getData().clear();
        for (final Map.Entry<String, Double> entry : byTag.entrySet()) {
            final Tag tag = controller.getTag(entry.getKey());
            final String name = tag != null ? tag.name() : "Senza categoria";
            final double pct = totalExpenses > 0 ? (entry.getValue() / totalExpenses) * 100 : 0;
            pieChart.getData().add(new PieChart.Data(
                    String.format("%s (%.1f%%)", name, pct), entry.getValue()));
        }

        // LineChart: saldo netto mese per mese.
        final Map<String, Double> byMonth = controller.getNetByMonth();
        final XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (final Map.Entry<String, Double> entry : byMonth.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        lineChart.getData().clear();
        lineChart.getData().add(series);
    }

    private List<Node> rowsFor(final List<Transaction> transactions) {
        return transactions.stream().map(this::row).toList();
    }

    private Node row(final Transaction tx) {
        final Label desc = new Label(tx.description() == null ? "" : tx.description());
        desc.setStyle("-fx-font-weight: bold;");
        final Label amount = new Label(String.format("€%.2f", tx.amount()));
        final Label date = new Label(
            tx.date().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        date.setStyle("-fx-text-fill: gray;");

        final HBox top = new HBox(8, desc, spacer(), amount);
        top.setAlignment(Pos.CENTER_LEFT);

        final HBox bottom = new HBox(8, date);
        bottom.setAlignment(Pos.CENTER_LEFT);
        final Tag tag = controller.getTag(tx.tagId());
        if (tag != null) {
            bottom.getChildren().add(tagBadge(tag));
        }

        final VBox card = new VBox(2, top, bottom);
        card.setPadding(new Insets(8));
        card.setStyle("-fx-background-color: #f4f4f5; -fx-background-radius: 6;");
        card.setOnMouseClicked(e -> openTransactionForm(tx.type(), tx));
        return card;
    }

    private Node tagBadge(final Tag tag) {
        final Circle dot = new Circle(5);
        dot.setFill(parseColor(tag.color()));
        final Label name = new Label(tag.name());
        final HBox badge = new HBox(4, dot, name);
        badge.setAlignment(Pos.CENTER_LEFT);
        badge.setPadding(new Insets(2, 6, 2, 6));
        badge.setStyle("-fx-background-color: #e4e4e7; -fx-background-radius: 10;");
        return badge;
    }

    static Color parseColor(final String hex) {
        try {
            return hex == null || hex.isBlank() ? Color.GRAY : Color.web(hex);
        } catch (final IllegalArgumentException e) {
            return Color.GRAY;
        }
    }

    private void openTransactionForm(final TransactionType defaultType, final Transaction existing) {
        new TransactionFormDialog(controller, defaultType, existing).showAndWait();
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

    private static Color balanceColor(final double balance) {
        if (balance > 0) {
            return Color.web("#16a34a");
        }
        if (balance < 0) {
            return Color.web("#dc2626");
        }
        return Color.web("#52525b");
    }
}