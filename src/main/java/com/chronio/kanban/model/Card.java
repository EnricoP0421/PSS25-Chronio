package com.chronio.kanban.model;

public record Card(
        String id,
        String title,
        String description,
        String tagId,
        boolean completed
) {
}
