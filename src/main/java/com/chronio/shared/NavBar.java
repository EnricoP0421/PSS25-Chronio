package com.chronio.shared;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Barra di navigazione principale dell'applicazione.
 * Contiene i pulsanti per passare tra la vista calendario e le bacheche kanban.
 */
public final class NavBar {

    private final Runnable onCalendar;
    private final Runnable onKanban;

    /**
     * Costruisce la navbar con le azioni di navigazione.
     *
     * @param onCalendar azione eseguita al click su "Calendario"
     * @param onKanban   azione eseguita al click su "Bacheche"
     */
    public NavBar(final Runnable onCalendar, final Runnable onKanban) {
        this.onCalendar = onCalendar;
        this.onKanban = onKanban;
    }

    /**
     * Costruisce e restituisce il nodo HBox della navbar.
     *
     * @return {@link HBox} con logo e pulsanti di navigazione
     */
    public HBox build() {
        final Label logo = new Label("Chronio");
        logo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        final Button calBtn = new Button("Calendario");
        final Button kanBtn = new Button("Bacheche");

        calBtn.setOnAction(e -> onCalendar.run());
        kanBtn.setOnAction(e -> onKanban.run());

        final Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        final HBox bar = new HBox(12, logo, spacer, calBtn, kanBtn);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-padding: 8 16;");
        return bar;
    }
}