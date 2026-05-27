# Chronio

Applicazione JavaFX per la produttività personale: calendario, bacheche Kanban e budget tracker, con persistenza locale in JSON via Gson.

## Team

- **Aurora Loprino** — Calendario (model `Event`/`Tag`, viste, serializzazione JSON condivisa)
- **Lorenzo Billi** — Bacheche Kanban (model `Board`/`Column`/`Card`, drag-and-drop)
- **Enrico Poli** — Budget tracker (model `Transaction`, aggregazioni, grafici)

## Requisiti

- **JDK 25** (Amazon Corretto, Temurin, Oracle: indifferente)
- Git
- IDE a piacere (IntelliJ IDEA consigliato per JavaFX)

> ⚠️ **Importante per il team**: tutti e tre dobbiamo avere il JDK 25 installato. Il progetto usa Gradle 9.1.0 che richiede questa versione.

Non serve installare Gradle: il progetto userà il **Gradle Wrapper** (`./gradlew` / `gradlew.bat`).

## Setup iniziale

```bash
git clone <url-repo>
cd chronio
./gradlew build      # Linux/Mac
gradlew.bat build    # Windows
```

## Comandi utili

```bash
./gradlew run        # avvia l'app
./gradlew test       # esegue i test
./gradlew build      # compila + test + jar
./gradlew clean      # pulisce la cartella build/
```

## Struttura

```
src/main/java/com/chronio/
  App.java              # entrypoint JavaFX
  calendar/             # modulo Aurora
  kanban/               # modulo Lorenzo
  budget/               # modulo Enrico
  shared/               # model condivisi + persistenza Gson
src/main/resources/     # FXML, CSS, icone
src/test/java/          # test JUnit 5
```

## Workflow Git

**Branch principali:**
- `main` — sempre funzionante, protetto, ci si arriva solo via Pull Request
- `dev` — branch di integrazione (opzionale ma consigliato in 3)

**Branch di lavoro:**
- Uno per feature, con prefisso del nome:
  - `aurora/calendar-month-view`
  - `lorenzo/kanban-drag-drop`
  - `enrico/budget-piechart`

**Flusso tipico:**
```bash
git checkout dev
git pull
git checkout -b enrico/budget-transaction-model
# ... lavori, committi ...
git push -u origin enrico/budget-transaction-model
# poi apri una Pull Request da GitHub verso dev
```

**Convenzione commit:** in italiano o inglese, ma consistente. Esempio breve:
```
feat(budget): aggiungi model Transaction con serializzazione Gson
fix(kanban): corregge crash su drop in colonna vuota
test(calendar): aggiunge test su filtro per tag
```

**Regole pratiche:**
- Niente push diretto su `main`
- Almeno **1 review** da un compagno prima di mergiare una PR
- Prima di aprire una PR: `git pull origin dev` sul tuo branch e risolvi eventuali conflitti
- Il file JSON dei dati utente **non va committato** (è già in `.gitignore` sotto `/data/`)

## Persistenza dati

Tutti i dati (eventi, bacheche, transazioni) vengono salvati in `data/state.json` nella cartella di lavoro. Il file viene creato al primo salvataggio e ignorato da Git.
