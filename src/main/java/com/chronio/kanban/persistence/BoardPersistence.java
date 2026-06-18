package com.chronio.kanban.persistence;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.chronio.kanban.model.BoardData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class BoardPersistence {

    private static final Logger LOGGER = Logger.getLogger(BoardPersistence.class.getName());
    private static final String SAVE_DIR = ".chronio";
    private static final String SAVE_FILE = "boards.json";

    private final Path filePath;
    private final Gson gson;

    public BoardPersistence(final Path filePath) {
        this.filePath = filePath;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public static Path getDefaultPath() {
        return Paths.get(System.getProperty("user.home"), SAVE_DIR, SAVE_FILE);
    }

    public void save(final BoardData data) {
        try {
            final Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (Writer writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
                gson.toJson(data, writer);
            }
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Errore nel salvataggio", e);
        }
    }

    public BoardData load() {
        if (!Files.exists(filePath)) {
            return BoardData.empty();
        }
        try (Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            final BoardData data = gson.fromJson(reader, BoardData.class);
            return data != null ? data : BoardData.empty();
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Errore nel caricamento", e);
            return BoardData.empty();
        }
    }
}
