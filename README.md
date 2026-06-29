# Chronio
 
Applicazione desktop per la **gestione personale del tempo e delle attività**.
Chronio riunisce in un'unica interfaccia tre strumenti: un calendario, bacheche
kanban e un modulo di budget personale.
 
Progetto sviluppato per il corso di *Progettazione e Sviluppo Software*
(A.A. 2025/2026).
 
---
 
## Funzionalità
 
### Calendario
- Creazione, modifica ed eliminazione di eventi (anche su più giorni e "tutto il giorno").
- Viste mensile, settimanale e giornaliera, con navigazione tra i periodi.
- Tag colorati per categorizzare gli eventi, con possibilità di nasconderli per filtrare la vista.
- Barra laterale con gli eventi del giorno e dei successivi.
### Bacheche (Kanban)
- Bacheche con colonne e card personalizzabili.
- Card con descrizione, stato di completamento e tag associati.
- **Spostamento delle card tramite drag-and-drop**, tra colonne diverse o all'interno della stessa.
- Filtro delle card per tag.
### Budget
- Registrazione di entrate e uscite con descrizione, importo, data e categoria.
- Totali di periodo (entrate, uscite, saldo) e grafici: uscite per categoria e andamento mensile.
- Tag colorati e filtro delle transazioni per tag e per periodo.
---
 
## Tecnologie
 
- **Java** — linguaggio principale.
- **JavaFX** — interfaccia grafica.
- **Gson** — persistenza dei dati in formato JSON.
- **Gradle** — build e gestione delle dipendenze.
- **JUnit** — test automatizzati.
I dati vengono salvati automaticamente dopo ogni operazione e ricaricati
all'avvio. I file sono memorizzati nella cartella dati dell'utente
(`~/.chronio`).
 
---

 
## Architettura
 
Chronio segue il pattern **MVC** applicato in modo indipendente a ciascuno dei
tre moduli (calendario, kanban, budget). Ogni modulo ha il proprio model,
controller, view e persistenza, e nessuna interfaccia di model o controller
dipende da JavaFX: la grafica è quindi sostituibile senza toccare la logica.
 
---
 
## Autori
 
- **Aurora Loprino**
- **Enrico Poli**
