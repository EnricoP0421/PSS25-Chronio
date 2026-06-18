package com.chronio.kanban.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.chronio.kanban.model.Board;
import com.chronio.kanban.model.BoardData;
import com.chronio.kanban.model.Card;
import com.chronio.kanban.model.Column;
import com.chronio.kanban.persistence.BoardPersistence;

public final class BoardControllerImpl implements BoardController {

    private BoardData data;
    private final BoardPersistence persistence;

    public BoardControllerImpl(final BoardData data, final BoardPersistence persistence) {
        this.data = data;
        this.persistence = persistence;
    }

    @Override
    public Map<String, Board> getBoards() {
        return data.boards();
    }

    @Override
    public Board createBoard(final String title) {
        final String id = "b" + data.nextBoardId();
        final Board board = new Board(id, title, new LinkedHashMap<>());
        final LinkedHashMap<String, Board> boards = new LinkedHashMap<>(data.boards());
        boards.put(id, board);
        data = new BoardData(boards, data.nextBoardId() + 1, data.nextColumnId(), data.nextCardId());
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
        final LinkedHashMap<String, Board> boards = new LinkedHashMap<>(data.boards());
        boards.put(boardId, updated);
        data = new BoardData(boards, data.nextBoardId(), data.nextColumnId(), data.nextCardId());
        persistence.save(data);
        return Optional.of(updated);
    }

    @Override
    public void deleteBoard(final String boardId) {
        final LinkedHashMap<String, Board> boards = new LinkedHashMap<>(data.boards());
        boards.remove(boardId);
        data = new BoardData(boards, data.nextBoardId(), data.nextColumnId(), data.nextCardId());
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
        final LinkedHashMap<String, Board> boards = new LinkedHashMap<>(data.boards());
        boards.put(boardId, updated);
        data = new BoardData(boards, data.nextBoardId(), data.nextColumnId() + 1, data.nextCardId());
        persistence.save(data);
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
        final LinkedHashMap<String, Column> columns = new LinkedHashMap<>(board.columns());
        columns.put(columnId, updated);
        final Board updatedBoard = new Board(boardId, board.title(), columns);
        final LinkedHashMap<String, Board> boards = new LinkedHashMap<>(data.boards());
        boards.put(boardId, updatedBoard);
        data = new BoardData(boards, data.nextBoardId(), data.nextColumnId(), data.nextCardId());
        persistence.save(data);
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
        final LinkedHashMap<String, Board> boards = new LinkedHashMap<>(data.boards());
        boards.put(boardId, updated);
        data = new BoardData(boards, data.nextBoardId(), data.nextColumnId(), data.nextCardId());
        persistence.save(data);
    }

    @Override
    public Card createCard(final String boardId, final String columnId,
                           final String title, final String description, final String tagId) {
        final Board board = data.boards().get(boardId);
        if (board == null) {
            throw new IllegalArgumentException("Board non trovata: " + boardId);
        }
        final Column column = board.columns().get(columnId);
        if (column == null) {
            throw new IllegalArgumentException("Colonna non trovata: " + columnId);
        }
        final String id = "k" + data.nextCardId();
        final Card card = new Card(id, title, description, tagId);
        final LinkedHashMap<String, Card> cards = new LinkedHashMap<>(column.cards());
        cards.put(id, card);
        final Column updatedCol = new Column(columnId, column.title(), cards);
        final LinkedHashMap<String, Column> columns = new LinkedHashMap<>(board.columns());
        columns.put(columnId, updatedCol);
        final Board updatedBoard = new Board(boardId, board.title(), columns);
        final LinkedHashMap<String, Board> boards = new LinkedHashMap<>(data.boards());
        boards.put(boardId, updatedBoard);
        data = new BoardData(boards, data.nextBoardId(), data.nextColumnId(), data.nextCardId() + 1);
        persistence.save(data);
        return card;
    }

    @Override
    public Optional<Card> updateCard(final String boardId, final String columnId, final String cardId,
                                     final String title, final String description, final String tagId) {
        final Board board = data.boards().get(boardId);
        if (board == null) {
            return Optional.empty();
        }
        final Column column = board.columns().get(columnId);
        if (column == null || !column.cards().containsKey(cardId)) {
            return Optional.empty();
        }
        final Card updated = new Card(cardId, title, description, tagId);
        final LinkedHashMap<String, Card> cards = new LinkedHashMap<>(column.cards());
        cards.put(cardId, updated);
        final Column updatedCol = new Column(columnId, column.title(), cards);
        final LinkedHashMap<String, Column> columns = new LinkedHashMap<>(board.columns());
        columns.put(columnId, updatedCol);
        final Board updatedBoard = new Board(boardId, board.title(), columns);
        final LinkedHashMap<String, Board> boards = new LinkedHashMap<>(data.boards());
        boards.put(boardId, updatedBoard);
        data = new BoardData(boards, data.nextBoardId(), data.nextColumnId(), data.nextCardId());
        persistence.save(data);
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
        final Column updatedCol = new Column(columnId, column.title(), cards);
        final LinkedHashMap<String, Column> columns = new LinkedHashMap<>(board.columns());
        columns.put(columnId, updatedCol);
        final Board updatedBoard = new Board(boardId, board.title(), columns);
        final LinkedHashMap<String, Board> boards = new LinkedHashMap<>(data.boards());
        boards.put(boardId, updatedBoard);
        data = new BoardData(boards, data.nextBoardId(), data.nextColumnId(), data.nextCardId());
        persistence.save(data);
    }

    @Override
    public List<Card> getFilteredCards(final String boardId, final String columnId, final String tagId) {
        final Board board = data.boards().get(boardId);
        if (board == null) {
            return List.of();
        }
        final Column column = board.columns().get(columnId);
        if (column == null) {
            return List.of();
        }
        return column.cards().values().stream()
            .filter(c -> tagId == null || tagId.equals(c.tagId()))
            .collect(Collectors.toList());
    }

    public BoardData getData() {
        return data;
    }
}
