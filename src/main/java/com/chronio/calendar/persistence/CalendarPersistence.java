package com.chronio.calendar.persistence;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.chronio.calendar.model.CalendarData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Gestisce il salvataggio e il caricamento dei dati del calendario su disco in formato JSON
 */
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

    /**
     * @return il percorso di default del file di salvataggio
     */
    public static Path getDefaultPath() {
        return Paths.get(System.getProperty("user.home"), SAVE_DIR, SAVE_FILE);
    }

    /**
     * Salva i dati del calendario su disco
     * Controlla se la cartella dove deve salvare esiste, se no la crea 
     * Apre il file in scrittura
     * Usa Gson per convertire l'oggetto CalendarData in JSON e scriverlo nel file
     * Se qualcosa va storto logga l'errore senza far crashare l'app
     * @param data i dati da salvare
     */
    public void save(final CalendarData data) {
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

    /**
     * Carica i dati del calendario da disco
     * Se il file non esiste o è corrotto, restituisce uno stato vuoto
     * @return i dati caricati, o CalendarData#empty() in caso di errore
     */
    public CalendarData load() {
        if (!Files.exists(filePath)) {
            return CalendarData.empty();
        }
        try (Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            final CalendarData data = gson.fromJson(reader, CalendarData.class);
            return data != null ? data : CalendarData.empty();
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Errore nel caricamento", e);
            return CalendarData.empty();
        }
    }
}
