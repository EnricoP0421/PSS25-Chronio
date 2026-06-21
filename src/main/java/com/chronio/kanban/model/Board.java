package com.chronio.kanban.model;
import java.util.Map;

public record Board(
        String id,
        String title,
        Map<String, Column> columns
) {
}
