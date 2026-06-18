package com.chronio.budget.model;

import java.util.LinkedHashMap;

/**
 * Contenitore immutabile di tutti i dati del budget: transazioni, tag e
 * i contatori per generare i prossimi identificativi. È l'oggetto che viene
 * salvato e caricato dalla persistenza.
 *
 * @param transactions      mappa id -> transazione, in ordine di inserimento
 * @param tags              mappa id -> tag, in ordine di inserimento
 * @param nextTransactionId prossimo id disponibile per una transazione
 * @param nextTagId         prossimo id disponibile per un tag
 */
public record BudgetData (
    LinkedHashMap<String, Transaction> transactions,
    LinkedHashMap<String, Tag> tags,
    int nextTransactionId,
    int nextTagId

){
    /**
     * Crea un budget vuoto, usato alla prima apertura quando non esistono
     * dati salvati.
     *
     * @return un'istanza senza transazioni né tag, con i contatori a 1
     */
    public static BudgetData empty() {
        return new BudgetData(new LinkedHashMap<>(), new LinkedHashMap<>(), 1, 1);
    }
}