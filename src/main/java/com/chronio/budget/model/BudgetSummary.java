package com.chronio.budget.model;

import java.util.List;

/**
 * Riepilogo del budget in un dato periodo: totali di entrate e uscite,
 * saldo e l'elenco delle transazioni considerate.
 *
 * @param totalIncome   somma delle entrate del periodo
 * @param totalExpenses somma delle uscite del periodo
 * @param balance       saldo (entrate meno uscite)
 * @param transactions  transazioni incluse nel periodo
 */
public record BudgetSummary(
    double totalIncome,
    double totalExpenses,
    double balance,
    List<Transaction> transactions

) {

    /**
     * Crea un riepilogo calcolando automaticamente il saldo come differenza
     * tra entrate e uscite.
     *
     * @param totalIncome   somma delle entrate
     * @param totalExpenses somma delle uscite
     * @param transactions  transazioni del periodo
     * @return il riepilogo con il saldo già calcolato
     */
    public static BudgetSummary of(final double totalIncome,
                                   final double totalExpenses,
                                   final List<Transaction> transactions) {
        return new BudgetSummary(totalIncome, totalExpenses, totalIncome - totalExpenses, transactions);
    }
}