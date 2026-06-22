package com.chronio.kanban.model;

import java.util.List;

/**
 * Card all'interno di una colonna della bacheca
 */
public record Card(
        String id,
        String title,
        String description,
        List<String> tagIds,
        boolean completed
) {
}
