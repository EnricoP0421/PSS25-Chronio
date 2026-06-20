package com.chronio.kanban.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.chronio.kanban.model.Board;
import com.chronio.kanban.model.Card;
import com.chronio.kanban.model.Column;
import com.chronio.kanban.model.KanbanTag;

public interface BoardController {

    Map<String, Board> getBoards();

    Map<String, KanbanTag> getTags();

    KanbanTag createTag(String name, String color);

    void deleteTag(String tagId);

    Optional<KanbanTag> updateTag(String tagId, String name, String color);

    Board createBoard(String title);

    Optional<Board> renameBoard(String boardId, String title);

    void deleteBoard(String boardId);

    Column createColumn(String boardId, String title);

    Optional<Column> renameColumn(String boardId, String columnId, String title);

    void deleteColumn(String boardId, String columnId);

    Card createCard(String boardId, String columnId, String title, String description, List<String> tagIds);

    Optional<Card> updateCard(String boardId, String columnId, String cardId,
                              String title, String description, List<String> tagIds);

    void deleteCard(String boardId, String columnId, String cardId);

    void toggleCard(String boardId, String columnId, String cardId);

    List<Card> getFilteredCards(String boardId, String columnId, List<String> tagIds);
}
