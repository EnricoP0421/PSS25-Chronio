package com.chronio.kanban.model;

import java.util.LinkedHashMap;

/**
 * Colonna di una bacheca, contiene un insieme ordinato di card
 * Usa LinkedHashMap} per preservare l'ordine di inserimento durante la serializzazione Gson.
 */
public record Column(
        String id,
        String title,
        LinkedHashMap<String, Card> cards
) {
}
