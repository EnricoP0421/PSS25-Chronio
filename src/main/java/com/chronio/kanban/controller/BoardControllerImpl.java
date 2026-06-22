package com.chronio.kanban.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.chronio.kanban.model.Board;
import com.chronio.kanban.model.BoardData;
import com.chronio.kanban.model.Card;
import com.chronio.kanban.model.Column;
import com.chronio.kanban.model.KanbanTag;
import com.chronio.kanban.persistence.BoardPersistence;

/**
 * Implementazione di BoardController.
 */
public final class BoardControllerImpl implements BoardController {

    private BoardData data;
    private final BoardPersistence persistence;

    /**
     * Costruisce il controller a partire da dati esistenti
     * @param data i dati iniziali delle bacheche
     * @param persistence la persistenza su disco
     */
    public BoardControllerImpl(final BoardData data, final BoardPersistence persistence) {
        this.data = Objects.requireNonNull(data, "i dati non possono essere null");
        this.persistence = Objects.requireNonNull(persistence, "la persistenza non può essere null");
    }

    @Override
    public Map<String, Board> getBoards() {
        return data.boards();
    }

    @Override
    public Map<String, KanbanTag> getTags() {
        return data.tags();
    }

    @Override
    public KanbanTag createTag(final String name, final String color) {
        final String id = "kt" + data.nextTagId();
        final KanbanTag tag = new KanbanTag(id, name, color);
        final LinkedHashMap<String, KanbanTag> tags = new LinkedHashMap<>(data.tags());
        tags.put(id, tag);
        data = new BoardData(
            data.boards(), tags,
            data.nextBoardId(), data.nextColumnId(), data.nextCardId(), data.nextTagId() + 1
        );
        persistence.save(data);
        return tag;
    }

    @Override
    public void deleteTag(final String tagId) {
        final LinkedHashMap<String, KanbanTag> tags = new LinkedHashMap<>(data.tags());
        tags.remove(tagId);
        data = new BoardData(
            data.boards(), tags,
            data.nextBoardId(), data.nextColumnId(), data.nextCardId(), data.nextTagId()
        );
        persistence.save(data);
    }

    @Override
    public Optional<KanbanTag> updateTag(final String tagId, final String name, final String color) {
        final KanbanTag existing = data.tags().get(tagId);
        if (existing == null) {
            return Optional.empty();
        }
        final KanbanTag updated = new KanbanTag(tagId, name, color);
        final LinkedHashMap<String, KanbanTag> tags = new LinkedHashMap<>(data.tags());
        tags.put(tagId, updated);
        data = new BoardData(
            data.boards(), tags,
            data.nextBoardId(), data.nextColumnId(), data.nextCardId(), data.nextTagId()
        );
        persistence.save(data);
        return Optional.of(updated);
    }

    @Override
    public Board createBoard(final String title) {
        final String id = "b" + data.nextBoardId();
        final Board board = new Board(id, title, new LinkedHashMap<>());
        final LinkedHashMap<String, Board> boards = new LinkedHashMap<>(data.boards());
        boards.put(id, board);
        data = new BoardData(
            boards, data.tags(),
            data.nextBoardId() + 1, data.nextColumnId(), data.nextCardId(), data.nextTagId()
        );
        persistence.save(data);
        return board;
    }

    @Override
    public Optional<Board> renameBoard(final String boardId, final String title) {
        final Board existing = data.boards().get(boardId);
        if (existing == null) {
            return Optional.empty();
        }
        final Board updated = new Board(boardId, title, existing.columns());
        saveBoard(boardId, updated, data.nextBoardId(), data.nextColumnId(), data.nextCardId());
        return Optional.of(updated);
    }

    @Override
    public void deleteBoard(final String boardId) {
        final LinkedHashMap<String, Board> boards = new LinkedHashMap<>(data.boards());
        boards.remove(boardId);
        data = new BoardData(
            boards, data.tags(),
            data.nextBoardId(), data.nextColumnId(), data.nextCardId(), data.nextTagId()
        );
        persistence.save(data);
    }

    @Override
    public Column createColumn(final String boardId, final String title) {
        final Board board = data.boards().get(boardId);
        if (board == null) {
            throw new IllegalArgumentException("Board non trovata: " + boardId);
        }
        final String id = "c" + data.nextColumnId();
        final Column column = new Column(id, title, new LinkedHashMap<>());
        final LinkedHashMap<String, Column> columns = new LinkedHashMap<>(board.columns());
        columns.put(id, column);
        final Board updated = new Board(boardId, board.title(), columns);
        saveBoard(boardId, updated, data.nextBoardId(), data.nextColumnId() + 1, data.nextCardId());
        return column;
    }

    @Override
    public Optional<Column> renameColumn(final String boardId, final String columnId, final String title) {
        final Board board = data.boards().get(boardId);
        if (board == null) {
            return Optional.empty();
        }
        final Column existing = board.columns().get(columnId);
        if (existing == null) {
            return Optional.empty();
        }
        final Column updated = new Column(columnId, title, existing.cards());
        saveColumn(boardId, board, columnId, updated, data.nextCardId());
        return Optional.of(updated);
    }

    @Override
    public void deleteColumn(final String boardId, final String columnId) {
        final Board board = data.boards().get(boardId);
        if (board == null) {
            return;
        }
        final LinkedHashMap<String, Column> columns = new LinkedHashMap<>(board.columns());
        columns.remove(columnId);
        final Board updated = new Board(boardId, board.title(), columns);
        saveBoard(boardId, updated, data.nextBoardId(), data.nextColumnId(), data.nextCardId());
    }

    @Override
    public Card createCard(final String boardId, final String columnId,
                           final String title, final String description, final List<String> tagIds) {
        final Board board = data.boards().get(boardId);
        if (board == null) {
            throw new IllegalArgumentException("Board non trovata: " + boardId);
        }
        final Column column = board.columns().get(columnId);
        if (column == null) {
            throw new IllegalArgumentException("Colonna non trovata: " + columnId);
        }
        final String id = "k" + data.nextCardId();
        final Card card = new Card(id, title, description, tagIds != null ? tagIds : List.of(), false);
        final LinkedHashMap<String, Card> cards = new LinkedHashMap<>(column.cards());
        cards.put(id, card);
        saveColumn(boardId, board, columnId, new Column(columnId, column.title(), sortCards(cards)),
            data.nextCardId() + 1);
        return card;
    }

    @Override
    public Optional<Card> updateCard(final String boardId, final String columnId, final String cardId,
                                     final String title, final String description, final List<String> tagIds) {
        final Board board = data.boards().get(boardId);
        if (board == null) {
            return Optional.empty();
        }
        final Column column = board.columns().get(columnId);
        if (column == null || !column.cards().containsKey(cardId)) {
            return Optional.empty();
        }
        final Card updated = new Card(
            cardId, title, description,
            tagIds != null ? tagIds : List.of(),
            column.cards().get(cardId).completed()
        );
        final LinkedHashMap<String, Card> cards = new LinkedHashMap<>(column.cards());
        cards.put(cardId, updated);
        saveColumn(boardId, board, columnId, new Column(columnId, column.title(), cards), data.nextCardId());
        return Optional.of(updated);
    }

    @Override
    public void deleteCard(final String boardId, final String columnId, final String cardId) {
        final Board board = data.boards().get(boardId);
        if (board == null) {
            return;
        }
        final Column column = board.columns().get(columnId);
        if (column == null) {
            return;
        }
        final LinkedHashMap<String, Card> cards = new LinkedHashMap<>(column.cards());
        cards.remove(cardId);
        saveColumn(boardId, board, columnId, new Column(columnId, column.title(), cards), data.nextCardId());
    }

    @Override
    public void toggleCard(final String boardId, final String columnId, final String cardId) {
        final Board board = data.boards().get(boardId);
        if (board == null) {
            return;
        }
        final Column column = board.columns().get(columnId);
        if (column == null) {
            return;
        }
        final Card existing = column.cards().get(cardId);
        if (existing == null) {
            return;
        }
        final Card toggled = new Card(
            cardId, existing.title(), existing.description(), existing.tagIds(), !existing.completed()
        );
        final LinkedHashMap<String, Card> cards = new LinkedHashMap<>(column.cards());
        cards.put(cardId, toggled);
        saveColumn(boardId, board, columnId,
            new Column(columnId, column.title(), sortCards(cards)), data.nextCardId());
    }

    @Override
    public void moveCard(final String boardId, final String fromColumn,
                         final String toColumn, final String cardId) {
        if (fromColumn.equals(toColumn)) {
            return;
        }
        final Board board = data.boards().get(boardId);
        if (board == null) {
            return;
        }
        final Column source = board.columns().get(fromColumn);
        final Column dest = board.columns().get(toColumn);
        if (source == null || dest == null) {
            return;
        }
        final Card card = source.cards().get(cardId);
        if (card == null) {
            return;
        }

        final LinkedHashMap<String, Card> sourceCards = new LinkedHashMap<>(source.cards());
        sourceCards.remove(cardId);

        final LinkedHashMap<String, Card> destCards = new LinkedHashMap<>(dest.cards());
        destCards.put(cardId, card);

        // costruzione delle due aggiornate.
        final LinkedHashMap<String, Column> columns = new LinkedHashMap<>(board.columns());
        columns.put(fromColumn, new Column(fromColumn, source.title(), sourceCards));
        columns.put(toColumn, new Column(toColumn, dest.title(), sortCards(destCards)));

        // Salva la board aggiornata (i contatori non cambiano: nessun nuovo id).
        saveBoard(boardId, new Board(boardId, board.title(), columns),
            data.nextBoardId(), data.nextColumnId(), data.nextCardId());
    }

    @Override
    public List<Card> getFilteredCards(final String boardId, final String columnId, final List<String> tagIds) {
        final Board board = data.boards().get(boardId);
        if (board == null) {
            return List.of();
        }
        final Column column = board.columns().get(columnId);
        if (column == null) {
            return List.of();
        }
        return column.cards().values().stream()
            .filter(c -> tagIds == null || tagIds.isEmpty() || c.tagIds().stream().anyMatch(tagIds::contains))
            .collect(Collectors.toList());
    }

    /**
     * @return i dati correnti delle bacheche, usato dalla persistenza
     */
    public BoardData getData() {
        return data;
    }

    private void saveBoard(final String boardId, final Board updated,
                           final int nextBoard, final int nextCol, final int nextCard) {
        final LinkedHashMap<String, Board> boards = new LinkedHashMap<>(data.boards());
        boards.put(boardId, updated);
        data = new BoardData(boards, data.tags(), nextBoard, nextCol, nextCard, data.nextTagId());
        persistence.save(data);
    }

    private void saveColumn(final String boardId, final Board board,
                            final String columnId, final Column updatedCol, final int nextCard) {
        final LinkedHashMap<String, Column> columns = new LinkedHashMap<>(board.columns());
        columns.put(columnId, updatedCol);
        saveBoard(boardId, new Board(boardId, board.title(), columns),
            data.nextBoardId(), data.nextColumnId(), nextCard);
    }

    private LinkedHashMap<String, Card> sortCards(final LinkedHashMap<String, Card> cards) {
        final LinkedHashMap<String, Card> reordered = new LinkedHashMap<>();
        cards.entrySet().stream()
            .filter(e -> !e.getValue().completed())
            .forEach(e -> reordered.put(e.getKey(), e.getValue()));
        cards.entrySet().stream()
            .filter(e -> e.getValue().completed())
            .forEach(e -> reordered.put(e.getKey(), e.getValue()));
        return reordered;
    }
}
