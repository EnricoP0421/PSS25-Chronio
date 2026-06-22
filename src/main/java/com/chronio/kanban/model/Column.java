package com.chronio.kanban.model;

import java.util.Map;

/**
 * Colonna di una bacheca, contiene un insieme ordinato di card
 */
public record Column(
        String id,
        String title,
        Map<String, Card> cards
) {
}
