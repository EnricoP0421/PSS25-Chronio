plugins {
    java
    application

    /*
     * Aggiunge il task "shadowJar" per esportare un jar eseguibile (fat-jar).
     * Il jar si trova in build/libs/ dopo "gradle build".
     */
    id("com.gradleup.shadow") version "9.3.1"
}

group = "com.chronio"
version = "0.1.0"

repositories {
    mavenCentral()
}

java {
    toolchain {
        // Versione Java usata per compilare ed eseguire il progetto
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

val javaFxVersion = "25.0.2"
val javaFXModules = listOf("base", "controls", "fxml", "graphics")
val supportedPlatforms = listOf("linux", "mac", "win")

dependencies {
    // JavaFX: i classifier di tutte le piattaforme rendono il fat-jar
    // eseguibile su Linux, Windows e macOS partendo dal solo jar.
    implementation("org.openjfx:javafx:$javaFxVersion")
    for (platform in supportedPlatforms) {
        for (module in javaFXModules) {
            implementation("org.openjfx:javafx-$module:$javaFxVersion:$platform")
        }
    }

    // Persistenza JSON
    implementation("com.google.code.gson:gson:2.11.0")

    // Testing
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "failed", "skipped")
    }
}

application {
    // Launcher che NON estende Application: necessario per il fat-jar JavaFX
    mainClass.set("com.chronio.Launcher")
}

tasks.shadowJar {
    archiveBaseName.set("chronio")
    archiveClassifier.set("")
    archiveVersion.set("")
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
}

// "gradle build" deve includere la costruzione del fat-jar
tasks.build {
    dependsOn(tasks.shadowJar)
}
