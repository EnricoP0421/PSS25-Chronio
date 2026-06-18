package com.chronio.budget.model;

/**
 * Tag categoria associabile alle transazioni (es. "Cibo", "Affitto").
 * È immutabile: i metodi {@code with...} restituiscono una copia modificata.
 *
 * @param id      identificativo univoco del tag
 * @param name    nome visualizzato del tag
 * @param color   colore in formato esadecimale "#rrggbb"
 * @param visible indica se il tag è visibile nell'interfaccia
 */
public record Tag(String id, String name, String color, boolean visible) {

    /**
     * @param name il nuovo nome
     * @return una copia del tag con il nome indicato
     */
    public Tag withName(final String name) {
        return new Tag(id, name, color, visible);
    }

    /**
     * @param color il nuovo colore in formato esadecimale
     * @return una copia del tag con il colore indicato
     */
    public Tag withColor(final String color) {
        return new Tag(id, name, color, visible);
    }

    /**
     * @param visible la nuova visibilità
     * @return una copia del tag con la visibilità indicata
     */
    public Tag withVisible(final boolean visible) {
        return new Tag(id, name, color, visible);
    }
}