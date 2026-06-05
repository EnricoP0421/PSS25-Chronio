package com.chronio.budget.controller;

import com.chronio.budget.model.BudgetService;
import com.chronio.budget.model.BudgetSummary;
import com.chronio.budget.model.Transaction;
import com.chronio.budget.model.TransactionType;
import com.chronio.budget.model.Tag;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public final class BudgetController {

    // Callback che la view implementa per ridisegnarsi quando i dati cambiano.
    public interface View {
        void refreshTransactionLists();
        void refreshCharts();
    }

    private final BudgetService service;
    private View view;

    // Intervallo di tempo correntemente selezionato (default: dal 1° del mese corrente a oggi).
    private LocalDate periodStart;
    private LocalDate periodEnd;

    public BudgetController(final BudgetService service) {
        this.service = service;
        final LocalDate today = LocalDate.now();
        this.periodStart = today.withDayOfMonth(1);
        this.periodEnd = today;
    }

    public void setView(final View view) {
        this.view = view;
    }

    //Intervallo di tempo

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    /**
     * Aggiornato quando l'utente cambia uno dei due DatePicker.
     * Ricalcola il riepilogo e ridisegna i grafici.
     */
    public void onPeriodChanged(final LocalDate start, final LocalDate end) {
        if (start != null) {
            this.periodStart = start;
        }
        if (end != null) {
            this.periodEnd = end;
        }
        refreshCharts();
    }

    //Operazioni sulle transazioni

    /**
     * Salva una transazione: crea se id è null, altrimenti aggiorna.
     * Dopo il salvataggio aggiorna liste e grafici.
     *
     * @return la transazione salvata
     */
    public Transaction onSaveTransaction(final String id,
                                         final TransactionType type,
                                         final String description,
                                         final double amount,
                                         final String date,
                                         final String tagId) {
        final Transaction saved;
        if (id == null) {
            saved = service.addTransaction(type, description, amount, date, tagId);
        } else {
            saved = service.updateTransaction(id, description, amount, date, tagId);
        }
        refreshTransactionLists();
        refreshCharts();
        return saved;
    }

    /**
     * Elimina una transazione e aggiorna la UI.
     * La conferma utente è responsabilità della view.
     */
    public void onDeleteTransaction(final String id) {
        service.removeTransaction(id);
        refreshTransactionLists();
        refreshCharts();
    }

    //Operazioni sui tag

    public Tag onAddTag(final String name, final String color) {
        final Tag tag = service.addTag(name, color);
        refreshTransactionLists();
        refreshCharts();
        return tag;
    }

    public void onRemoveTag(final String id) {
        service.removeTag(id);
        refreshTransactionLists();
        refreshCharts();
    }

    //Query per la view

    public List<Transaction> getIncomes() {
        return service.getAllTransactionsSorted().stream()
                .filter(t -> t.type() == TransactionType.INCOME)
                .toList();
    }

    public List<Transaction> getExpenses() {
        return service.getAllTransactionsSorted().stream()
                .filter(t -> t.type() == TransactionType.EXPENSE)
                .toList();
    }

    public BudgetSummary getCurrentSummary() {
        return service.calculateSummary(periodStart.toString(), periodEnd.toString());
    }

    public Map<String, Double> getExpensesByTag() {
        return service.aggregateByTag(periodStart.toString(), periodEnd.toString());
    }

    public Map<String, Double> getNetByMonth() {
        return service.aggregateByMonth(periodStart.toString(), periodEnd.toString());
    }

    public Tag getTag(final String tagId) {
        return tagId == null ? null : service.getTags().get(tagId);
    }

    public List<Tag> getAllTags() {
        return List.copyOf(service.getTags().values());
    }

    //Refresh

    public void refreshTransactionLists() {
        if (view != null) {
            view.refreshTransactionLists();
        }
    }

    public void refreshCharts() {
        if (view != null) {
            view.refreshCharts();
        }
    }
}