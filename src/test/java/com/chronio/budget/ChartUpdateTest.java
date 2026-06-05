package com.chronio.budget;

import com.chronio.budget.model.BudgetService;
import com.chronio.budget.model.Tag;
import com.chronio.budget.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChartUpdateTest {

    private BudgetService service;

    @BeforeEach
    void setUp() {
        service = new BudgetService(new InMemoryBudgetRepository());
    }

    // Test 3: i dati dei grafici si aggiornano dopo l'aggiunta di una transazione.
    @Test
    void chartsDataUpdatesAfterAddingTransaction() {
        final Tag food = service.addTag("Cibo", "#ff0000");

        // Stato iniziale: nessuna uscita per il tag, nessun dato mensile.
        Map<String, Double> byTag = service.aggregateByTag("2025-05-01", "2025-05-31");
        Map<String, Double> byMonth = service.aggregateByMonth("2025-05-01", "2025-05-31");
        assertTrue(byTag.isEmpty());
        assertTrue(byMonth.isEmpty());

        // Aggiunta di un'uscita con tag.
        service.addTransaction(TransactionType.EXPENSE, "Pranzo", 30.0, "2025-05-12", food.id());

        byTag = service.aggregateByTag("2025-05-01", "2025-05-31");
        byMonth = service.aggregateByMonth("2025-05-01", "2025-05-31");

        assertEquals(30.0, byTag.get(food.id()), 1e-9);
        assertEquals(-30.0, byMonth.get("2025-05"), 1e-9);

        // Aggiunta di un'entrata: il saldo netto del mese deve riflettersi.
        service.addTransaction(TransactionType.INCOME, "Rimborso", 50.0, "2025-05-13", null);
        byMonth = service.aggregateByMonth("2025-05-01", "2025-05-31");
        assertEquals(20.0, byMonth.get("2025-05"), 1e-9);
    }
}