package com.chronio.budget.controller;

import com.chronio.budget.model.BudgetService;
import com.chronio.budget.model.BudgetSummary;
import com.chronio.budget.model.Transaction;
import com.chronio.budget.model.TransactionType;
import com.chronio.budget.model.Tag;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Coordina la logica del modulo budget tra il {@link BudgetService} e la
 * vista. Espone le operazioni invocabili dall'interfaccia (salvataggio,
 * eliminazione, gestione tag, cambio periodo) e le query di sola lettura
 * necessarie a popolare liste e grafici. Notifica la vista tramite
 * l'interfaccia {@link View} quando i dati cambiano.
 */
public final class BudgetController {

    /**
     * Callback che la vista implementa per ridisegnarsi quando i dati
     * gestiti dal controller cambiano.
     */
    public interface View {
        /** Richiede alla vista di rigenerare le liste delle transazioni. */
        void refreshTransactionLists();

        /** Richiede alla vista di aggiornare totali e grafici. */
        void refreshCharts();
    }

    private final BudgetService service;
    private View view;

    // Intervallo di tempo correntemente selezionato (default: dal 1° del mese corrente a oggi).
    private LocalDate periodStart;
    private LocalDate periodEnd;

    private final java.util.Set<String> activeTagIds = new java.util.HashSet<>();

    /**
     * Crea il controller impostando il periodo di default dal primo giorno
     * del mese corrente a oggi.
     *
     * @param service il service che fornisce i dati e la logica
     */
    public BudgetController(final BudgetService service) {
        this.service = service;
        final LocalDate today = LocalDate.now();
        this.periodStart = today.withDayOfMonth(1);
        this.periodEnd = today;
    }

    /**
     * Registra la vista da notificare a ogni cambiamento dei dati.
     *
     * @param view la vista da collegare
     */
    public void setView(final View view) {
        this.view = view;
    }

    //Intervallo di tempo

    /**
     * @return la data iniziale del periodo selezionato
     */
    public LocalDate getPeriodStart() {
        return periodStart;
    }

    /**
     * @return la data finale del periodo selezionato
     */
    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    /**
     * Aggiornato quando l'utente cambia uno dei due DatePicker.
     * Ricalcola il riepilogo e ridisegna i grafici.
     *
     * @param start nuova data iniziale, ignorata se null
     * @param end   nuova data finale, ignorata se null
     */
    public void onPeriodChanged(final LocalDate start, final LocalDate end) {
        if (start != null) {
            this.periodStart = start;
        }
        if (end != null) {
            this.periodEnd = end;
        }
        refreshTransactionLists();
        refreshCharts();
    }

    //Operazioni sulle transazioni

    /**
     * Salva una transazione: crea se id è null, altrimenti aggiorna.
     * Dopo il salvataggio aggiorna liste e grafici.
     *
     * @param id          id della transazione da aggiornare, o null per crearne una nuova
     * @param type        tipo della transazione
     * @param description descrizione
     * @param amount      importo positivo
     * @param date        data della transazione
     * @param tagId       id del tag categoria, può essere null
     * @return la transazione salvata
     */
    public Transaction onSaveTransaction(final String id,
                                         final TransactionType type,
                                         final String description,
                                         final double amount,
                                         final LocalDate date,
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
     *
     * @param id id della transazione da eliminare
     */
    public void onDeleteTransaction(final String id) {
        service.removeTransaction(id);
        refreshTransactionLists();
        refreshCharts();
    }

    //Operazioni sui tag

    /**
     * Crea un nuovo tag categoria e aggiorna la UI.
     *
     * @param name  nome del tag
     * @param color colore in formato esadecimale
     * @return il tag creato
     */
    public Tag onAddTag(final String name, final String color) {
        final Tag tag = service.addTag(name, color);
        refreshTransactionLists();
        refreshCharts();
        return tag;
    }

    /**
     * Rimuove un tag e aggiorna la UI. Le transazioni che lo usavano
     * perdono la categoria ma non vengono eliminate.
     *
     * @param id id del tag da rimuovere
     */
    public void onRemoveTag(final String id) {
        service.removeTag(id);
        refreshTransactionLists();
        refreshCharts();
    }

    /**
     * Aggiorna un tag esistente e ridisegna la UI.
     *
     * @param id    id del tag
     * @param name  nuovo nome
     * @param color nuovo colore esadecimale
     * @return il tag aggiornato, o null se non esiste
     */
    public Tag onUpdateTag(final String id, final String name, final String color) {
        final Tag tag = service.updateTag(id, name, color);
        refreshTransactionLists();
        refreshCharts();
        return tag;
    }

    /**
     * @return il set (modificabile) dei tag attivi per il filtro
     */
    public java.util.Set<String> getActiveTagIds() {
        return activeTagIds;
    }

    //Query per la view

    /**
     * @return le entrate, ordinate dalla più recente
     */
    public List<Transaction> getIncomes() {
    return getCurrentSummary().transactions().stream()
            .filter(t -> t.type() == TransactionType.INCOME)
            .filter(this::matchesFilter)
            .sorted(java.util.Comparator.comparing(Transaction::date).reversed())
            .toList();
    }

    /**
     * @return le uscite, ordinate dalla più recente
     */
    public List<Transaction> getExpenses() {
    return getCurrentSummary().transactions().stream()
            .filter(t -> t.type() == TransactionType.EXPENSE)
            .filter(this::matchesFilter)
            .sorted(java.util.Comparator.comparing(Transaction::date).reversed())
            .toList();
    }

    /**
     * @return true se la transazione passa il filtro tag attivo
     *         (se nessun tag è selezionato, passano tutte)
     */
    private boolean matchesFilter(final Transaction t) {
        return activeTagIds.isEmpty() || activeTagIds.contains(t.tagId());
    }

    /**
     * @return il riepilogo (entrate, uscite, saldo) del periodo selezionato
     */
    public BudgetSummary getCurrentSummary() {
        return service.calculateSummary(periodStart, periodEnd);
    }

    /**
     * @return le uscite del periodo aggregate per tag (chiave tagId, può essere null)
     */
    public Map<String, Double> getExpensesByTag() {
        return service.aggregateByTag(periodStart, periodEnd);
    }

    /**
     * @return il saldo netto del periodo mese per mese (chiave "YYYY-MM")
     */
    public Map<String, Double> getNetByMonth() {
        return service.aggregateByMonth(periodStart, periodEnd);
    }

    /**
     * Recupera un tag dal suo id.
     *
     * @param tagId id del tag, può essere null
     * @return il tag corrispondente, o null se l'id è null o non esiste
     */
    public Tag getTag(final String tagId) {
        return tagId == null ? null : service.getTags().get(tagId);
    }

    /**
     * @return la lista di tutti i tag esistenti
     */
    public List<Tag> getAllTags() {
        return List.copyOf(service.getTags().values());
    }

    //Refresh

    /**
     * Notifica la vista (se presente) di rigenerare le liste delle transazioni.
     */
    public void refreshTransactionLists() {
        if (view != null) {
            view.refreshTransactionLists();
        }
    }

    /**
     * Notifica la vista (se presente) di aggiornare totali e grafici.
     */
    public void refreshCharts() {
        if (view != null) {
            view.refreshCharts();
        }
    }
}