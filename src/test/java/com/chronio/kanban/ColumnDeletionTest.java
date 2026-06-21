package com.chronio.kanban;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.chronio.kanban.controller.BoardControllerImpl;
import com.chronio.kanban.model.BoardData;
import com.chronio.kanban.persistence.BoardPersistence;

class ColumnDeletionTest {

    @TempDir
    private Path tempDir;

    @Test
    void deletingColumnShouldRemoveItsCards() {
        final Path file = tempDir.resolve("boards.json");
        final BoardControllerImpl controller = new BoardControllerImpl(BoardData.empty(), new BoardPersistence(file));

        controller.createBoard("Board");
        final String boardId = controller.getBoards().keySet().iterator().next();
        controller.createColumn(boardId, "Colonna");
        final String columnId = controller.getBoards().get(boardId).columns().keySet().iterator().next();
        controller.createCard(boardId, columnId, "Card 1", "", List.of());
        controller.createCard(boardId, columnId, "Card 2", "", List.of());

        controller.deleteColumn(boardId, columnId);

        assertTrue(controller.getBoards().get(boardId).columns().isEmpty());
    }
}
