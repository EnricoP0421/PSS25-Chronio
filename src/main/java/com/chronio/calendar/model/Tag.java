package com.chronio.calendar.model;

/**
 *Tag che può essere associato a uno o più eventi
 * Il campo visible è usato per filtrare: se false, gli eventi con questo tag vengono nascosti
 */
public record Tag(String id, String name, String color, boolean visible) {


    public Tag withName(final String name) {
        return new Tag(id, name, color, visible);
    }

    public Tag withColor(final String color) {
        return new Tag(id, name, color, visible);
    }

    public Tag withVisible(final boolean visible) {
        return new Tag(id, name, color, visible);
    }
}
