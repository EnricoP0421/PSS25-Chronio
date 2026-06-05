package com.chronio.budget.model;

import java.util.LinkedHashMap;

public record BudgetData (
    LinkedHashMap<String, Transaction> transactions,
    LinkedHashMap<String, Tag> tags,
    int nextTransactionId,
    int nextTagId
    
){
    public static BudgetData empty() {
        return new BudgetData(new LinkedHashMap<>(), new LinkedHashMap<>(), 1, 1);
    }
}
