package com.chronio.kanban.model;

import java.util.LinkedHashMap;

/**
 * Contenitore di tutti i dati delle bacheche che persistono su disco
 * Gson serializza e deserializza questo record in JSON preservando l'ordine di inserimento
 * grazie all'uso di LinkedHashMap
 * I contatori garantiscono id univoci senza usare UUID.
 */
public record BoardData(
        LinkedHashMap<String, Board> boards,
        LinkedHashMap<String, KanbanTag> tags,
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
