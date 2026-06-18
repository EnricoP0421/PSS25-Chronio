package com.chronio;

import com.chronio.budget.model.BudgetService;
import com.chronio.budget.persistence.JsonBudgetRepository;
import com.chronio.budget.controller.BudgetController;
import com.chronio.budget.view.BudgetView;
import com.chronio.shared.NavBar;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(final Stage stage) {
        final BudgetService service = new BudgetService(new JsonBudgetRepository());
        final BudgetController controller = new BudgetController(service);
        final BudgetView budgetView = new BudgetView(controller);

        // Navbar condivisa. Per ora Calendario e Bacheche sono collegamenti
        // a vuoto: le rispettive view non esistono ancora in questa branch.
        final NavBar navBar = new NavBar(
                () -> System.out.println("Calendario: non ancora implementato"),
                () -> System.out.println("Bacheche: non ancora implementato"),
                () -> System.out.println("Budget: non ancora implementato"));

        // Contenitore principale: navbar in alto, budget al centro.
        final BorderPane root = new BorderPane();
        root.setTop(navBar.build());
        root.setCenter(budgetView);

        stage.setTitle("Chronio - Budget");
        stage.setScene(new Scene(root, 1000, 640));
        stage.show();
    }

    public static void main(final String[] args) {
        launch(args);
    }
}