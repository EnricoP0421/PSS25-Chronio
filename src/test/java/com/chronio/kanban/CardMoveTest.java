package com.chronio.kanban;
 
import java.nio.file.Path;
import java.util.List;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
 
import com.chronio.kanban.controller.BoardControllerImpl;
import com.chronio.kanban.model.Board;
import com.chronio.kanban.model.Card;
import com.chronio.kanban.model.Column;
import com.chronio.kanban.model.BoardData;
import com.chronio.kanban.persistence.BoardPersistence;
 
/**
 * Verifica lo spostamento di una card tra colonne e la sua persistenza:
 * dopo un riavvio (ricaricamento dal file) la card deve trovarsi nella
 * colonna di destinazione.
 */
class CardMoveTest {
 
    @TempDir
    private Path tempDir;
 
    @Test
    void movedCardShouldBeInDestinationColumnAfterRestart() {
        final Path file = tempDir.resolve("boards.json");
        final BoardPersistence persistence = new BoardPersistence(file);
        final BoardControllerImpl controller = new BoardControllerImpl(BoardData.empty(), persistence);
 
        // Predispone una bacheca con due colonne e una card nella prima.
        final Board board = controller.createBoard("Progetto");
        final String boardId = board.id();
        final Column todo = controller.createColumn(boardId, "Da fare");
        final Column done = controller.createColumn(boardId, "Fatto");
        final Card card = controller.createCard(boardId, todo.id(), "Compito", "", List.of());
 
        // Sposta la card da "Da fare" a "Fatto".
        controller.moveCard(boardId, todo.id(), done.id(), card.id());
 
        // Verifica immediata: la card non è più nell'origine ed è nella destinazione.
        final Board afterMove = controller.getBoards().get(boardId);
        assertFalse(afterMove.columns().get(todo.id()).cards().containsKey(card.id()),
                "La card non dovrebbe più essere nella colonna di origine");
        assertTrue(afterMove.columns().get(done.id()).cards().containsKey(card.id()),
                "La card dovrebbe trovarsi nella colonna di destinazione");
 
        // Simula il riavvio ricaricando dal file e verifica che lo spostamento sia persistito.
        final BoardControllerImpl reloaded = new BoardControllerImpl(persistence.load(), persistence);
        final Board restored = reloaded.getBoards().get(boardId);
        assertFalse(restored.columns().get(todo.id()).cards().containsKey(card.id()),
                "Dopo il riavvio la card non dovrebbe essere nell'origine");
        assertTrue(restored.columns().get(done.id()).cards().containsKey(card.id()),
                "Dopo il riavvio la card dovrebbe essere nella destinazione");
        assertEquals("Compito", restored.columns().get(done.id()).cards().get(card.id()).title(),
                "I dati della card spostata devono essere preservati");
    }
}