package com.chronio.budget.persistence;

import com.chronio.budget.model.BudgetData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class JsonBudgetRepository implements BudgetRepository {

    private static final String FILE_NAME = "budget.json";

    private final Path file;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    // Costruttore di default: usa la cartella dati standard dell'app
    // (la stessa in cui vivono gli altri file dati come data.json del calendario).
    public JsonBudgetRepository() {
        this(defaultDataDir().resolve(FILE_NAME));
    }

    // Costruttore esplicito: utile per indicare una cartella/percorso specifico.
    public JsonBudgetRepository(final Path file) {
        this.file = file;
    }

    // Cartella dati dell'applicazione: ~/.chronio
    // (coerente con un'app desktop che salva i propri dati nella home utente).
    private static Path defaultDataDir() {
        final String home = System.getProperty("user.home", ".");
        return Paths.get(home, ".chronio");
    }

    @Override
    public BudgetData load() {
        if (!Files.exists(file)) {
            return BudgetData.empty();
        }
        try {
            final String json = Files.readString(file, StandardCharsets.UTF_8);
            final BudgetData data = gson.fromJson(json, BudgetData.class);
            return data != null ? data : BudgetData.empty();
        } catch (final IOException e) {
            throw new UncheckedIOException("Impossibile leggere " + file, e);
        }
    }

    @Override
    public void save(final BudgetData data) {
        try {
            if (file.getParent() != null) {
                Files.createDirectories(file.getParent());
            }
            final String json = gson.toJson(data);
            Files.writeString(file, json, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new UncheckedIOException("Impossibile scrivere " + file, e);
        }
    }
}