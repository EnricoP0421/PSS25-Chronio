package com.chronio.budget.model;

import java.util.List;

public record BudgetSummary(
    double totalIncome,
    double totalExpenses,
    double balance,
    List<Transaction> transactions

) {

public static BudgetSummary of(final double totalIncome,
                               final double totalExpenses,
                               final List<Transaction> transactions) {
        return new BudgetSummary(totalIncome, totalExpenses, totalIncome - totalExpenses, transactions);        
    }
}