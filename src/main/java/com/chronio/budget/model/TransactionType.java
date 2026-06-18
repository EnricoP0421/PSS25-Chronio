package com.chronio.budget.model;

/**
 * Tipo di una transazione: entrata o uscita di denaro.
 */
public enum TransactionType {
    /** Entrata di denaro (incremento del saldo). */
    INCOME,
    /** Uscita di denaro (decremento del saldo). */
    EXPENSE
}