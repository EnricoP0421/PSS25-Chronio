package com.chronio.budget.persistence;

import com.chronio.budget.model.BudgetData;


/**
 * Astrazione per la persistenza dei dati del budget. Permette di
 * sostituire la strategia di salvataggio (file JSON, memoria, ecc.)
 * senza modificare la logica applicativa.
 */
public interface BudgetRepository {

    /**
     * Carica i dati del budget. Alla prima apertura (nessun dato salvato)
     * restituisce BudgetData.empty().
     *
     * @return i dati del budget
     */
    BudgetData load();

    /**
     * Salva i dati del budget.
     *
     * @param data i dati da persistere
     */
    void save(BudgetData data);
}