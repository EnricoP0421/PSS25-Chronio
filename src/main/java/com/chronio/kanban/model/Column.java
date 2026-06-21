package com.chronio.kanban.model;

import java.util.Map;

public record Column(
        String id,
        String title,
        Map<String, Card> cards
) {
}
