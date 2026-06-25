package com.chronio;

import javafx.application.Application;

/**
 * Entry point del fat-jar. Non estende Application: questo evita
 * l'errore "JavaFX runtime components are missing" quando si lancia
 * il jar con "java -jar". Delega l'avvio alla classe App.
 */
public final class Launcher {

    private Launcher() { }

    public static void main(final String[] args) {
        Application.launch(App.class, args);
    }
}
