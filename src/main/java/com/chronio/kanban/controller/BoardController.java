package com.chronio.kanban.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.chronio.kanban.model.Board;
import com.chronio.kanban.model.Card;
import com.chronio.kanban.model.Column;
import com.chronio.kanban.model.KanbanTag;

/**
 * Interfaccia del controller delle bacheche
 */
public interface BoardController {

    /** @return mappa id -> ritorna la bacheca corrispondente all'id salvato */
    Map<String, Board> getBoards();

    /** @return mappa id -> ritorna il tag corrispondente all'id salvato */
    Map<String, KanbanTag> getTags();

    /**
     * Crea un nuovo tag.
     * @param name nome del tag
     * @param color colore in formato hex
     * @return il tag creato
     */
    KanbanTag createTag(String name, String color);

    /**
     * Elimina un tag.
     * @param tagId id del tag
     */
    void deleteTag(String tagId);

    /**
     * Aggiorna nome e colore di un tag esistente
     * @param tagId id del tag
     * @param name nuovo nome
     * @param color nuovo colore
     * @return il tag aggiornato, o empty se non trovato
     */
    Optional<KanbanTag> updateTag(String tagId, String name, String color);

    /**
     * Crea una nuova bacheca
     * @param title titolo della bacheca
     * @return la bacheca creata
     */
    Board createBoard(String title);

    /**
     * Rinomina una bacheca esistente
     * @param boardId id della bacheca
     * @param title nuovo titolo
     * @return la bacheca aggiornata, o empty se non trovata
     */
    Optional<Board> renameBoard(String boardId, String title);

    /**
     * Elimina una bacheca
     * @param boardId id della bacheca
     */
    void deleteBoard(String boardId);

    /**
     * Crea una nuova colonna in una bacheca
     * @param boardId id della bacheca
     * @param title titolo della colonna
     * @return la colonna creata
     */
    Column createColumn(String boardId, String title);

    /**
     * Rinomina una colonna esistente
     * @param boardId id della bacheca
     * @param columnId id della colonna
     * @param title nuovo titolo
     * @return la colonna aggiornata, o empty se non trovata
     */
    Optional<Column> renameColumn(String boardId, String columnId, String title);

    /**
     * Elimina una colonna e tutte le sue card
     * @param boardId id della bacheca
     * @param columnId id della colonna
     */
    void deleteColumn(String boardId, String columnId);

    /**
     * Crea una nuova card in una colonna
     * @param boardId id della bacheca
     * @param columnId id della colonna
     * @param title titolo della card
     * @param description descrizione della card
     * @param tagIds lista di id tag associati
     * @return la card creata
     */
    Card createCard(String boardId, String columnId, String title, String description, List<String> tagIds);

    /**
     * Aggiorna una card esistente
     * @param boardId id della bacheca
     * @param columnId id della colonna
     * @param cardId id della card
     * @param title nuovo titolo
     * @param description nuova descrizione
     * @param tagIds nuova lista di tag
     * @return la card aggiornata, o empty se non trovata
     */
    Optional<Card> updateCard(String boardId, String columnId, String cardId,
                              String title, String description, List<String> tagIds);

    /**
     * Elimina una card
     * @param boardId id della bacheca
     * @param columnId id della colonna
     * @param cardId id della card
     */
    void deleteCard(String boardId, String columnId, String cardId);

    /**
     * Inverte lo stato completato di una card
     * Le card completate vengono spostate in fondo alla colonna
     * @param boardId id della bacheca
     * @param columnId id della colonna
     * @param cardId id della card
     */
    void toggleCard(String boardId, String columnId, String cardId);

     /**
     * Sposta una card da una colonna a un'altra della stessa bacheca, inserendola
     * alla posizione indicata. Se l'indice è fuori range o negativo, la card va in fondo.
     *
     * @param boardId    id della bacheca
     * @param fromColumn id della colonna di origine
     * @param toColumn   id della colonna di destinazione
     * @param cardId     id della card da spostare
     * @param toIndex    posizione di inserimento nella colonna destinazione (in fondo se fuori range)
     */
    void moveCard(String boardId, String fromColumn, String toColumn, String cardId, int toIndex);

    /**
     * Restituisce le card di una colonna filtrate per tag
     * @param boardId id della bacheca
     * @param columnId id della colonna
     * @param tagIds lista di tag da filtrare, null o vuota per nessun filtro
     * @return lista di card filtrate
     */
    List<Card> getFilteredCards(String boardId, String columnId, List<String> tagIds);
}
