package com.chronio.budget.model;

import com.chronio.budget.persistence.BudgetRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

/**
 * Logica applicativa del modulo budget: gestisce transazioni e tag,
 * calcola riepiloghi e aggregazioni, e delega la persistenza al repository.
 * Mantiene lo stato corrente in un {@link BudgetData} immutabile che viene
 * sostituito a ogni modifica.
 */
public final class BudgetService {

    private final BudgetRepository repository;
    private BudgetData data;

    /**
     * Crea il service caricando subito i dati dal repository.
     *
     * @param repository il repository da cui caricare e su cui salvare i dati
     */
    public BudgetService(final BudgetRepository repository) {
        this.repository = repository;
        this.data = repository.load();
    }

    /**
     * @return lo stato corrente del budget
     */
    public BudgetData getData() {
        return data;
    }

    /**
     * @return la mappa id -> tag dei tag esistenti
     */
    public LinkedHashMap<String, Tag> getTags() {
        return data.tags();
    }

    /**
     * Crea e aggiunge una nuova transazione.
     *
     * @param type        tipo (INCOME / EXPENSE)
     * @param description descrizione
     * @param amount      importo positivo
     * @param date        data ISO-8601 ("YYYY-MM-DD")
     * @param tagId       id del tag categoria, può essere null
     * @return la transazione creata
     */
    public Transaction addTransaction(final TransactionType type,
                                      final String description,
                                      final double amount,
                                      final LocalDate date,
                                      final String tagId) {
        if (type == null) {
            throw new IllegalArgumentException("Il tipo della transazione non può essere null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("L'importo deve essere positivo");
        }
        if (date == null) {
            throw new IllegalArgumentException("La data è obbligatoria");
        }

        final String id = String.valueOf(data.nextTransactionId());
        final Transaction tx = new Transaction(id, type, description, amount, date, tagId);

        final LinkedHashMap<String, Transaction> transactions = new LinkedHashMap<>(data.transactions());
        transactions.put(id, tx);

        data = new BudgetData(
                transactions,
                data.tags(),
                data.nextTransactionId() + 1,
                data.nextTagId()
        );
        persist();
        return tx;
    }

    /**
     * Rimuove una transazione dato il suo id. No-op se l'id non esiste.
     *
     * @param id id della transazione da rimuovere
     */
    public void removeTransaction(final String id) {
        if (!data.transactions().containsKey(id)) {
            return;
        }
        final LinkedHashMap<String, Transaction> transactions = new LinkedHashMap<>(data.transactions());
        transactions.remove(id);

        data = new BudgetData(transactions, data.tags(), data.nextTransactionId(), data.nextTagId());
        persist();
    }

    /**
     * Aggiorna i campi modificabili di una transazione esistente.
     * Il tipo (INCOME/EXPENSE) non viene modificato qui.
     *
     * @param id          id della transazione
     * @param description nuova descrizione
     * @param amount      nuovo importo positivo
     * @param date        nuova data ISO-8601
     * @param tagId       nuovo tag, può essere null
     * @return la transazione aggiornata, o null se l'id non esiste
     */
    public Transaction updateTransaction(final String id,
                                         final String description,
                                         final double amount,
                                         final LocalDate date,
                                         final String tagId) {
        final Transaction existing = data.transactions().get(id);
        if (existing == null) {
            return null;
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("L'importo deve essere positivo");
        }
        if (date == null) {
            throw new IllegalArgumentException("La data è obbligatoria");
        }

        final Transaction updated = existing
                .withDescription(description)
                .withAmount(amount)
                .withDate(date)
                .withTagId(tagId);

        final LinkedHashMap<String, Transaction> transactions = new LinkedHashMap<>(data.transactions());
        transactions.put(id, updated);

        data = new BudgetData(transactions, data.tags(), data.nextTransactionId(), data.nextTagId());
        persist();
        return updated;
    }

    /**
     * Crea e aggiunge un nuovo tag categoria.
     *
     * @param name  nome del tag
     * @param color colore in formato hex
     * @return il tag creato
     */
    public Tag addTag(final String name, final String color) {
        final String id = String.valueOf(data.nextTagId());
        final Tag tag = new Tag(id, name, color, true);

        final LinkedHashMap<String, Tag> tags = new LinkedHashMap<>(data.tags());
        tags.put(id, tag);

        data = new BudgetData(
                data.transactions(),
                tags,
                data.nextTransactionId(),
                data.nextTagId() + 1
        );
        persist();
        return tag;
    }

    /**
     * Rimuove un tag e imposta a null il tagId di tutte le transazioni
     * che lo usavano (perdono la categoria ma non vengono eliminate).
     *
     * @param id id del tag da rimuovere
     */
    public void removeTag(final String id) {
        if (!data.tags().containsKey(id)) {
            return;
        }

        final LinkedHashMap<String, Tag> tags = new LinkedHashMap<>(data.tags());
        tags.remove(id);

        final LinkedHashMap<String, Transaction> transactions = new LinkedHashMap<>();
        for (final Map.Entry<String, Transaction> entry : data.transactions().entrySet()) {
            final Transaction tx = entry.getValue();
            if (id.equals(tx.tagId())) {
                transactions.put(entry.getKey(), tx.withTagId(null));
            } else {
                transactions.put(entry.getKey(), tx);
            }
        }

        data = new BudgetData(transactions, tags, data.nextTransactionId(), data.nextTagId());
        persist();
    }

    /**
     * Calcola il riepilogo (entrate, uscite, saldo) sulle transazioni il cui
     * date è compreso tra startDate e endDate inclusi. Il confronto è
     * lessicografico su stringhe ISO-8601 (sufficiente per il formato "YYYY-MM-DD").
     *
     * @param startDate data iniziale inclusa ("YYYY-MM-DD")
     * @param endDate   data finale inclusa ("YYYY-MM-DD")
     * @return il riepilogo del periodo
     */
    public BudgetSummary calculateSummary(final LocalDate startDate, final LocalDate endDate) {
        final List<Transaction> filtered = filterByPeriod(startDate, endDate);

        double totalIncome = 0;
        double totalExpenses = 0;
        for (final Transaction tx : filtered) {
            if (tx.type() == TransactionType.INCOME) {
                totalIncome += tx.amount();
            } else {
                totalExpenses += tx.amount();
            }
        }
        return new BudgetSummary(totalIncome, totalExpenses, totalIncome - totalExpenses, filtered);
    }

    /**
     * Restituisce tutte le transazioni in ordine cronologico decrescente
     * (le più recenti per prime).
     *
     * @return lista ordinata di transazioni
     */
    public List<Transaction> getAllTransactionsSorted() {
        final List<Transaction> all = new ArrayList<>(data.transactions().values());
        all.sort(Comparator.comparing(Transaction::date).reversed());
        return all;
    }

    /**
     * Filtra le transazioni per tag.
     *
     * @param tagId id del tag (può essere null per le transazioni senza categoria)
     * @return lista delle transazioni con quel tag, in ordine cronologico decrescente
     */
    public List<Transaction> getTransactionsByTag(final String tagId) {
        final List<Transaction> result = new ArrayList<>();
        for (final Transaction tx : data.transactions().values()) {
            if (tagId == null ? tx.tagId() == null : tagId.equals(tx.tagId())) {
                result.add(tx);
            }
        }
        result.sort(Comparator.comparing(Transaction::date).reversed());
        return result;
    }

    /**
     * Aggrega le sole USCITE del periodo per tag.
     * Le uscite senza tag vengono raggruppate sotto la chiave null.
     *
     * @param startDate data iniziale inclusa
     * @param endDate   data finale inclusa
     * @return mappa tagId -> totale speso nel periodo
     */
    public Map<String, Double> aggregateByTag(final LocalDate startDate, final LocalDate endDate) {
        final Map<String, Double> result = new LinkedHashMap<>();
        for (final Transaction tx : filterByPeriod(startDate, endDate)) {
            if (tx.type() != TransactionType.EXPENSE) {
                continue;
            }
            result.merge(tx.tagId(), tx.amount(), Double::sum);
        }
        return result;
    }

    /**
     * Aggrega il saldo netto mese per mese nel periodo.
     * La chiave è "YYYY-MM" (ricavata dai primi 7 caratteri della data ISO-8601).
     * Il valore è entrate meno uscite di quel mese. Utile per il LineChart.
     *
     * @param startDate data iniziale inclusa
     * @param endDate   data finale inclusa
     * @return mappa "YYYY-MM" -> saldo netto del mese, ordinata cronologicamente
     */
    public Map<String, Double> aggregateByMonth(final LocalDate startDate, final LocalDate endDate) {
        final Map<String, Double> result = new LinkedHashMap<>();
        final List<Transaction> filtered = filterByPeriod(startDate, endDate);
        // Ordina cronologicamente crescente così le chiavi "YYYY-MM" entrano in ordine.
        filtered.sort(Comparator.comparing(Transaction::date));
        for (final Transaction tx : filtered) {
            final String month = monthKey(tx.date());
            final double signed = tx.type() == TransactionType.INCOME ? tx.amount() : -tx.amount();
            result.merge(month, signed, Double::sum);
        }
        return result;
    }

    private List<Transaction> filterByPeriod(final LocalDate startDate, final LocalDate endDate) {
        final List<Transaction> result = new ArrayList<>();
        for (final Transaction tx : data.transactions().values()) {
            final LocalDate d = tx.date();
            if (d != null && !d.isBefore(startDate) && !d.isAfter(endDate)) {
                result.add(tx);
            }
        }
        return result;
    }

    private static String monthKey(final LocalDate isoDate) {
        return isoDate != null ? isoDate.toString().substring(0, 7) : null;
    }

    private void persist() {
        repository.save(data);
    }
}