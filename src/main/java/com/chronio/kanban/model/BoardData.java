package com.chronio.kanban.model;

import java.util.LinkedHashMap;
import java.util.Map;

public record BoardData(
        Map<String, Board> boards,
        Map<String, KanbanTag> tags,
        int nextBoardId,
        int nextColumnId,
        int nextCardId,
        int nextTagId
) {

    public static BoardData empty() {
        return new BoardData(new LinkedHashMap<>(), new LinkedHashMap<>(), 1, 1, 1, 1);
    }
}
