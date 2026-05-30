package com.chronio.calendar.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.chronio.calendar.model.CalendarData;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class CalendarPersistence {

    private static final Logger LOGGER = Logger.getLogger(CalendarPersistence.class.getName());
    private static final String SAVE_DIR = ".chronio";
    private static final String SAVE_FILE = "calendar.json";

    private final Path filePath;
    private final Gson gson;

    public CalendarPersistence(final Path filePath) {
        this.filePath = filePath;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public static Path getDefaultPath() {
        return Paths.get(System.getProperty("user.home"), SAVE_DIR, SAVE_FILE);
    }

    public void save(final CalendarData data) {
        try {
            final Path parent = filePath.getParent();
            if (parent != null) Files.createDirectories(parent);
            try (Writer writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
                gson.toJson(data, writer);
            }
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Errore nel salvataggio", e);
        }
    }

    public CalendarData load() {
        if (!Files.exists(filePath)) return CalendarData.empty();
        try (Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            final CalendarData data = gson.fromJson(reader, CalendarData.class);
            return data != null ? data : CalendarData.empty();
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Errore nel caricamento", e);
            return CalendarData.empty();
        }
    }
}
