package com.chronio;

import com.chronio.budget.model.BudgetService;
import com.chronio.budget.persistence.JsonBudgetRepository;
import com.chronio.budget.controller.BudgetController;
import com.chronio.budget.view.BudgetView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(final Stage stage) {
        final BudgetService service = new BudgetService(new JsonBudgetRepository());
        final BudgetController controller = new BudgetController(service);
        final BudgetView budgetView = new BudgetView(controller);

        stage.setTitle("Chronio - Budget");
        stage.setScene(new Scene(budgetView, 1000, 640));
        stage.show();
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
