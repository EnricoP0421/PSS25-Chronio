package com.chronio.budget.model;

import java.util.LinkedHashMap;

import javax.swing.text.html.HTML.Tag;


public record BudgetData (
    LinkedHashMap<String, Transaction> transactions,
    LinkedHashMap<String, Tag> tags,
    int nextTransactionIs,
    int nextTagId
    
){
    public static BudgetData empty() {
        return new BudgetData(new LinkedHashMap<>(), new LinkedHashMap<>(), 1, 1);
    }
}
