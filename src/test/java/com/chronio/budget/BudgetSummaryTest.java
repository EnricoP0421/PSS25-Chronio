package com.chronio.budget;

import com.chronio.budget.model.BudgetService;
import com.chronio.budget.model.BudgetSummary;
import com.chronio.budget.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

// Test della logica pura di BudgetService, senza JavaFX e senza filesystem.
// Si usa un BudgetRepository in-memory iniettato nel costruttore del service.
class BudgetSummaryTest {

    private BudgetService service;

    @BeforeEach
    void setUp() {
        service = new BudgetService(new InMemoryBudgetRepository());
    }

    // Test 1: il saldo corrisponde alla somma entrate meno uscite nel periodo.
    @Test
    void balanceEqualsIncomesMinusExpenses() {
        service.addTransaction(TransactionType.INCOME, "Stipendio", 2000.0, LocalDate.parse("2025-05-10"), null);
        service.addTransaction(TransactionType.INCOME, "Bonus", 500.0, LocalDate.parse("2025-05-15"), null);
        service.addTransaction(TransactionType.EXPENSE, "Affitto", 800.0, LocalDate.parse("2025-05-05"), null);
        service.addTransaction(TransactionType.EXPENSE, "Spesa", 200.0, LocalDate.parse("2025-05-20"), null);

        final BudgetSummary summary = service.calculateSummary(LocalDate.parse("2025-05-01"), LocalDate.parse("2025-05-31"));

        assertEquals(2500.0, summary.totalIncome(), 1e-9);
        assertEquals(1000.0, summary.totalExpenses(), 1e-9);
        assertEquals(1500.0, summary.balance(), 1e-9);
        assertEquals(summary.totalIncome() - summary.totalExpenses(), summary.balance(), 1e-9);
        assertEquals(4, summary.transactions().size());
    }

    // Test 2: il filtro per periodo esclude le transazioni fuori intervallo.
    @Test
    void periodFilterExcludesTransactionsOutsideRange() {
        service.addTransaction(TransactionType.INCOME, "Dentro inizio", 100.0, LocalDate.parse("2025-05-01"), null);
        service.addTransaction(TransactionType.INCOME, "Dentro fine", 100.0, LocalDate.parse("2025-05-31"), null);
        service.addTransaction(TransactionType.EXPENSE, "Prima", 50.0, LocalDate.parse("2025-04-30"), null);
        service.addTransaction(TransactionType.EXPENSE, "Dopo", 50.0, LocalDate.parse("2025-06-01"), null);

        final BudgetSummary summary = service.calculateSummary(LocalDate.parse("2025-05-01"), LocalDate.parse("2025-05-31"));

        // Solo le due transazioni dentro l'intervallo (estremi inclusi).
        assertEquals(2, summary.transactions().size());
        assertEquals(200.0, summary.totalIncome(), 1e-9);
        assertEquals(0.0, summary.totalExpenses(), 1e-9);
        final boolean hasOutside = summary.transactions().stream()
                .anyMatch(t -> t.date().equals(LocalDate.parse("2025-04-30")) || t.date().equals(LocalDate.parse("2025-06-01")));
        assertFalse(hasOutside);
    }
}