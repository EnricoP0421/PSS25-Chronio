package com.chronio.kanban.model;
import java.util.Map;

/**
 * Bacheca con colonne e card
 */
public record Board(
        String id,
        String title,
        Map<String, Column> columns
) {
}
