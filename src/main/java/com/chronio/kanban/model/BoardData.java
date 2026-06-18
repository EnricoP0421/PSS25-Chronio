package com.chronio.kanban.model;

import java.util.LinkedHashMap;

public record BoardData(
        LinkedHashMap<String, Board> boards,
        int nextBoardId,
        int nextColumnId,
        int nextCardId
) {

    public static BoardData empty() {
        return new BoardData(new LinkedHashMap<>(), 1, 1, 1);
    }
}
