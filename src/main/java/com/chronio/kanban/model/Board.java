package com.chronio.kanban.model;

import java.util.LinkedHashMap;

public record Board(
        String id,
        String title,
        LinkedHashMap<String, Column> columns
) {
}
