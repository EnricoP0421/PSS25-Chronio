package com.chronio.kanban;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.chronio.kanban.controller.BoardControllerImpl;
import com.chronio.kanban.model.BoardData;
import com.chronio.kanban.model.Card;
import com.chronio.kanban.persistence.BoardPersistence;

class TagFilterTest {

    @TempDir
    private Path tempDir;

    @Test
    void tagFilterShouldShowOnlyMatchingCards() {
        final Path file = tempDir.resolve("boards.json");
        final BoardControllerImpl controller = new BoardControllerImpl(BoardData.empty(), new BoardPersistence(file));

        controller.createBoard("Board");
        final String boardId = controller.getBoards().keySet().iterator().next();
        controller.createColumn(boardId, "Colonna");
        final String columnId = controller.getBoards().get(boardId).columns().keySet().iterator().next();

        controller.createTag("Urgente", "#ff0000");
        final String tagId = controller.getTags().keySet().iterator().next();

        controller.createCard(boardId, columnId, "Con tag", "", List.of(tagId));
        controller.createCard(boardId, columnId, "Senza tag", "", List.of());

        final List<Card> filtered = controller.getFilteredCards(boardId, columnId, List.of(tagId));

        assertEquals(1, filtered.size());
        assertEquals("Con tag", filtered.get(0).title());
    }
}
