package com.chronio.kanban.model;

import java.util.LinkedHashMap;

public record Column(
        String id,
        String title,
        LinkedHashMap<String, Card> cards
) {
}
