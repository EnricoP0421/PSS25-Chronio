package com.chronio.kanban;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.chronio.kanban.controller.BoardControllerImpl;
import com.chronio.kanban.model.BoardData;
import com.chronio.kanban.persistence.BoardPersistence;

class BoardPersistenceTest {

    @TempDir
    private Path tempDir;

    @Test
    void boardsAndColumnsShouldBeRestoredAfterRestart() {
        final Path file = tempDir.resolve("boards.json");
        final BoardPersistence persistence = new BoardPersistence(file);
        final BoardControllerImpl controller = new BoardControllerImpl(BoardData.empty(), persistence);

        controller.createBoard("Progetto");
        final String boardId = controller.getBoards().keySet().iterator().next();
        controller.createColumn(boardId, "Da fare");

        final BoardControllerImpl reloaded = new BoardControllerImpl(persistence.load(), persistence);
        assertEquals(1, reloaded.getBoards().size());
        assertEquals("Progetto", reloaded.getBoards().values().iterator().next().title());
        assertEquals(1, reloaded.getBoards().get(boardId).columns().size());
        assertEquals("Da fare", reloaded.getBoards().get(boardId).columns().values().iterator().next().title());
    }
}
