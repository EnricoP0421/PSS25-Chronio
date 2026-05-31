package com.chronio.budget.model;

public record Transaction(
    String id,
    TransactionType type,
    String description,
    double amount,
    String date,
    String tagId
) {

    public Transaction withDescription(final String description) {
        return new Transaction(id, type, description, amount, date, tagId);
    }

    public Transaction withAmount(final double amount) {
        return new Transaction(id, type, description, amount, date, tagId);
    }

    public Transaction withDate(final String date) {
        return new Transaction(id, type, description, amount, date, tagId);
    }

    public Transaction withTagId(final String tagId) {
        return new Transaction(id, type, description, amount, date, tagId);
    }
    
}
