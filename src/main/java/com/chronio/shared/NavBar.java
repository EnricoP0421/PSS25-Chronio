package com.chronio.shared;
 
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
 
/**
 * Barra di navigazione principale dell'applicazione.
 * Contiene i pulsanti per passare tra la vista calendario, le bacheche kanban e il budget.
 */
public final class NavBar {
 
    private final Runnable onCalendar;
    private final Runnable onKanban;
    private final Runnable onBudget;
 
    /**
     * Costruisce la navbar con le azioni di navigazione.
     *
     * @param onCalendar azione eseguita al click su "Calendario"
     * @param onKanban   azione eseguita al click su "Bacheche"
     * @param onBudget   azione eseguita al click su "Budget"
     */
    public NavBar(final Runnable onCalendar, final Runnable onKanban, final Runnable onBudget) {
        this.onCalendar = onCalendar;
        this.onKanban = onKanban;
        this.onBudget = onBudget;
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
        final Button budBtn = new Button("Budget");
 
        calBtn.setFocusTraversable(false);
        kanBtn.setFocusTraversable(false);
        budBtn.setFocusTraversable(false);
        calBtn.setOnAction(e -> {
            onCalendar.run();
            calBtn.getScene().getRoot().requestFocus();
        });
        kanBtn.setOnAction(e -> {
            onKanban.run();
            kanBtn.getScene().getRoot().requestFocus();
        });
        budBtn.setOnAction(e -> {
            onBudget.run();
            budBtn.getScene().getRoot().requestFocus();
        });
 
        final Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
 
        final HBox bar = new HBox(12, logo, spacer, calBtn, kanBtn, budBtn);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-padding: 8 16;");
        return bar;
    }
}