package com.chronio.budget.persistence;

import com.chronio.budget.model.BudgetData;


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
