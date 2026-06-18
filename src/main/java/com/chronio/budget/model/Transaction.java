package com.chronio.budget.model;

import java.time.LocalDate;

/**
 * Una singola transazione del budget (entrata o uscita).
 * È immutabile: i metodi {@code with...} restituiscono una copia modificata.
 *
 * @param id          identificativo univoco della transazione
 * @param type        tipo (entrata o uscita)
 * @param description descrizione testuale, può essere null
 * @param amount      importo positivo
 * @param date        data della transazione
 * @param tagId       id del tag categoria associato, può essere null
 */
public record Transaction(
    String id,
    TransactionType type,
    String description,
    double amount,
    LocalDate date,
    String tagId
) {

    /**
     * @param description la nuova descrizione
     * @return una copia della transazione con la descrizione indicata
     */
    public Transaction withDescription(final String description) {
        return new Transaction(id, type, description, amount, date, tagId);
    }

    /**
     * @param amount il nuovo importo
     * @return una copia della transazione con l'importo indicato
     */
    public Transaction withAmount(final double amount) {
        return new Transaction(id, type, description, amount, date, tagId);
    }

    /**
     * @param date la nuova data
     * @return una copia della transazione con la data indicata
     */
    public Transaction withDate(final LocalDate date) {
        return new Transaction(id, type, description, amount, date, tagId);
    }

    /**
     * @param tagId il nuovo id del tag, può essere null
     * @return una copia della transazione con il tag indicato
     */
    public Transaction withTagId(final String tagId) {
        return new Transaction(id, type, description, amount, date, tagId);
    }

}