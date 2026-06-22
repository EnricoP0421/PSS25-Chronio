package com.chronio.kanban.model;

import java.util.LinkedHashMap;

/**
 * Bacheca con colonne e card
 * Usa LinkedHashMap per preservare l'ordine di inserimento durante la serializzazione Gson.
 */
public record Board(
        String id,
        String title,
        LinkedHashMap<String, Column> columns
) {
}
