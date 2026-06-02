package com.chronio.budget;

import com.chronio.budget.model.BudgetData;
import com.chronio.budget.model.BudgetSummary;
import com.chronio.budget.model.Tag;
import com.chronio.budget.model.Transaction;
import com.chronio.budget.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BudgetServiceTest {

    private static final class InMemoryBudgetRepository implements BudgetRepository {
        private BudgetData data = BudgetData.empty();

        @Override
        public BudgetData load() {
            return data;
        }

        @Override
        public void save(final BudgetData data) {
            this.data = data;
        }
    }

    private BudgetService service;

    @BeforeEach
    void setUp() {
        service = new BudgetService(new InMemoryBudgetRepository());
    }

    @Test
    void balanceEqualsIncomesMinusExpenses() {
        service.addTransaction(TransactionType.INCOME, "Stipendio", 2000.0, "2025-05-10", null);
        service.addTransaction(TransactionType.INCOME, "Bonus", 500.0, "2025-05-15", null);
        service.addTransaction(TransactionType.EXPENSE, "Affitto", 800.0, "2025-05-05", null);
        service.addTransaction(TransactionType.EXPENSE, "Spesa", 200.0, "2025-05-20", null);

        final BudgetSummary summary = service.calculateSummary("2025-05-01", "2025-05-31");

        assertEquals(2500.0, summary.totalIncome(), 1e-9);
        assertEquals(1000.0, summary.totalExpenses(), 1e-9);
        assertEquals(1500.0, summary.balance(), 1e-9);
        assertEquals(summary.totalIncome() - summary.totalExpenses(), summary.balance(), 1e-9);
        assertEquals(4, summary.transactions().size());
    }

    @Test
    void periodFilterExcludesTransactionsOutsideRange() {
        service.addTransaction(TransactionType.INCOME, "Dentro inizio", 100.0, "2025-05-01", null);
        service.addTransaction(TransactionType.INCOME, "Dentro fine", 100.0, "2025-05-31", null);
        service.addTransaction(TransactionType.EXPENSE, "Prima", 50.0, "2025-04-30", null);
        service.addTransaction(TransactionType.EXPENSE, "Dopo", 50.0, "2025-06-01", null);

        final BudgetSummary summary = service.calculateSummary("2025-05-01", "2025-05-31");

        assertEquals(2, summary.transactions().size());
        assertEquals(200.0, summary.totalIncome(), 1e-9);
        assertEquals(0.0, summary.totalExpenses(), 1e-9);
        final boolean hasOutside = summary.transactions().stream()
                .anyMatch(t -> t.date().equals("2025-04-30") || t.date().equals("2025-06-01"));
        assertFalse(hasOutside);
    }

    @Test
    void chartsDataUpdatesAfterAddingTransaction() {
        final Tag food = service.addTag("Cibo", "#ff0000");

        Map<String, Double> byTag = service.aggregateByTag("2025-05-01", "2025-05-31");
        Map<String, Double> byMonth = service.aggregateByMonth("2025-05-01", "2025-05-31");
        assertTrue(byTag.isEmpty());
        assertTrue(byMonth.isEmpty());

        service.addTransaction(TransactionType.EXPENSE, "Pranzo", 30.0, "2025-05-12", food.id());

        byTag = service.aggregateByTag("2025-05-01", "2025-05-31");
        byMonth = service.aggregateByMonth("2025-05-01", "2025-05-31");

        assertEquals(30.0, byTag.get(food.id()), 1e-9);
        assertEquals(-30.0, byMonth.get("2025-05"), 1e-9);

        service.addTransaction(TransactionType.INCOME, "Rimborso", 50.0, "2025-05-13", null);
        byMonth = service.aggregateByMonth("2025-05-01", "2025-05-31");
        assertEquals(20.0, byMonth.get("2025-05"), 1e-9);
    }

    @Test
    void removingTagClearsTagIdOnRelatedTransactions() {
        final Tag svago = service.addTag("Svago", "#00ff00");
        final Transaction tagged = service.addTransaction(
                TransactionType.EXPENSE, "Cinema", 15.0, "2025-05-09", svago.id());
        final Transaction other = service.addTransaction(
                TransactionType.EXPENSE, "Bolletta", 60.0, "2025-05-09", null);

        assertEquals(svago.id(), service.getData().transactions().get(tagged.id()).tagId());

        service.removeTag(svago.id());

        assertNull(service.getTags().get(svago.id()));

        final Transaction afterRemoval = service.getData().transactions().get(tagged.id());
        assertNotNull(afterRemoval);
        assertNull(afterRemoval.tagId());

        assertNull(service.getData().transactions().get(other.id()).tagId());
        assertEquals(2, service.getData().transactions().size());
    }

    @Test
    void getAllTransactionsSortedReturnsNewestFirst() {
        service.addTransaction(TransactionType.EXPENSE, "Vecchia", 10.0, "2025-01-01", null);
        service.addTransaction(TransactionType.EXPENSE, "Nuova", 10.0, "2025-12-31", null);
        service.addTransaction(TransactionType.EXPENSE, "Media", 10.0, "2025-06-15", null);

        final var sorted = service.getAllTransactionsSorted();
        assertEquals("2025-12-31", sorted.get(0).date());
        assertEquals("2025-06-15", sorted.get(1).date());
        assertEquals("2025-01-01", sorted.get(2).date());
    }
}