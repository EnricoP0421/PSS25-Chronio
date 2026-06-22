package com.chronio.kanban.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Contenitore di tutti i dati delle bacheche che persistono su disco
 */
public record BoardData(
        Map<String, Board> boards,
        Map<String, KanbanTag> tags,
        int nextBoardId,
        int nextColumnId,
        int nextCardId,
        int nextTagId
) {

    /**
     * @return istanza di BoardData con mappe vuote e contatori a 1
     */
    public static BoardData empty() {
        return new BoardData(new LinkedHashMap<>(), new LinkedHashMap<>(), 1, 1, 1, 1);
    }
}
