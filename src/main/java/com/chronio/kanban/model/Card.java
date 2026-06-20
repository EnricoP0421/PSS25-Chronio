package com.chronio.kanban.model;

import java.util.List;

public record Card(
        String id,
        String title,
        String description,
        List<String> tagIds,
        boolean completed
) {
}
