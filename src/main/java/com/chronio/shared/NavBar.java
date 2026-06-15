package com.chronio.shared;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public final class NavBar {

    private final Runnable onCalendar;
    private final Runnable onKanban;

    public NavBar(final Runnable onCalendar, final Runnable onKanban) {
        this.onCalendar = onCalendar;
        this.onKanban = onKanban;
    }

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
