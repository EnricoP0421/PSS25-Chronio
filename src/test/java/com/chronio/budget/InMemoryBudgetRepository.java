package com.chronio.budget;

import com.chronio.budget.model.BudgetData;
import com.chronio.budget.persistence.BudgetRepository;

// Repository in-memory condiviso dai test: tiene i dati in un campo,
// niente I/O su disco. Iniettato nel costruttore di BudgetService per
// isolare la logica dal filesystem.
final class InMemoryBudgetRepository implements BudgetRepository {

    private BudgetData data = BudgetData.empty();

    @Override
    public BudgetData load() {
        return data;
    }

    @Override
    public void save(final BudgetData data) {
        this.data = data;
    }
}