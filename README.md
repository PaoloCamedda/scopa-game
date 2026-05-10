# 🃏 Scopa Online Siciliana - Full Stack Project

[![Spring Boot](https://img.shields.io/badge/Backend-Spring%20Boot%203-brightgreen?style=flat-square&logo=spring)](https://spring.io/)
[![Vue.js](https://img.shields.io/badge/Frontend-Vue.js%203-4fc08d?style=flat-square&logo=vuedotjs)](https://vuejs.org/)
[![Tailwind CSS](https://img.shields.io/badge/Style-Tailwind%20CSS-38bdf8?style=flat-square&logo=tailwindcss)](https://tailwindcss.com/)

Un'applicazione web moderna per giocare a **Scopa**, il classico gioco di carte italiano. Il progetto implementa un'architettura robusta con un motore di gioco Java e un'interfaccia utente dinamica e reattiva.

---

## 📝 Descrizione del Progetto

Il progetto nasce con l'obiettivo di digitalizzare l'esperienza del gioco della Scopa, curando particolarmente l'aspetto visivo e la fluidità delle mosse.

### Caratteristiche principali:
* **Motore di Gioco Centrale**: Gestione completa del mazzo di 40 carte, distribuzione, turni e calcolo dei punteggi (Scopa, Settebello, Denara, Primiera, Carte).
* **IA Avversario**: Un motore decisionale integrato che analizza lo stato del tavolo per sfidare l'utente in tempo reale.
* **Interfaccia Drag & Drop**: Esperienza utente intuitiva che permette di trascinare le carte dalla propria mano direttamente sul tavolo da gioco.
* **Design Siciliano**: Ottimizzato per l'uso di mazzi regionali, con icone personalizzate (Trinacria) e layout che richiama i classici tavoli in panno verde.

---

## 🛠️ Stack Tecnologico

### Backend
* **Java 17**: Linguaggio core per la logica di business.
* **Spring Boot 3**: Framework per la creazione delle API REST e la gestione del server web.
* **Maven**: Gestione delle dipendenze e automazione della build.

### Frontend
* **Vue.js 3**: Framework reattivo per la gestione del DOM e dello stato dell'interfaccia.
* **Tailwind CSS**: Framework CSS per un design moderno, pulito e responsive.
* **HTML5 & JavaScript (ES6+)**: Struttura e interazioni avanzate.

---

## 📂 Struttura del Progetto

```text
.
├── src/main/java/com/example/scopa/
│   ├── controller/      # Gestione delle richieste HTTP (API play, status, start)
│   ├── model/           # Entità del gioco (Carta, Mazzo, Giocatore, Partita)
│   └── service/         # Logica delle regole e calcolo prese
├── src/main/resources/
│   └── static/          # Root del Frontend
│        ├── index.html  # File principale (UI + Logica Vue)
│        └── images/     # Asset grafici
│             ├── trinacrea.png
│             └── carte/ # Mazzo di 40 carte + Dorso
└── pom.xml              # Configurazione Maven
```

## 🎮 Guida all'Utilizzo

Segui questi passaggi per configurare, avviare e giocare a **Scopa Online**.

### 🛠️ Requisiti Minimi
* **Java**: Versione 17 o superiore.
* **Maven**: Per la gestione delle dipendenze.
* **Browser**: Chrome, Firefox o Edge (aggiornati).

### ⚙️ Installazione e Setup
1. **Clona il repository:**
   ```bash
   git clone [https://github.com/tuo-username/scopa-online.git](https://github.com/tuo-username/scopa-online.git)
   cd scopa-online
### 2. Avvio del Server (Backend)

Per avviare il "cervello" del gioco, devi eseguire l'applicazione Spring Boot. Puoi farlo in due modi:

* **Tramite Terminale (Consigliato):**
    Posizionati nella cartella principale del progetto e digita:
    ```bash
    ./mvnw spring-boot:run
    ```
    *(Se sei su Windows e il comando sopra non va, usa `mvnw.cmd spring-boot:run`)*.

* **Tramite IDE (IntelliJ / Eclipse):**
    Trova la classe principale (quella con l'annotazione `@SpringBootApplication`, solitamente `ScopaApplication.java`), clicca con il tasto destro e seleziona **"Run 'ScopaApplication'"**.

**Verifica:** Il server è attivo correttamente quando vedi nel log la scritta:
`Tomcat started on port(s): 8080 (http) with context path ''`

---

### 3. URL per Giocare (Frontend)

Una volta che il server è in esecuzione, l'interfaccia di gioco è accessibile tramite qualsiasi browser moderno.

* **Indirizzo Locale:** Digita nella barra degli indirizzi del browser:
    `http://localhost:8080`

* **Primo Avvio:**
    Al caricamento della pagina, apparirà la schermata iniziale con il pulsante **"Nuova Partita"**. Se le immagini delle carte non dovessero apparire immediatamente, controlla che il percorso `src/main/resources/static/images/carte/` contenga i file rinominati correttamente.

* **Debug Rapido:**
    Se vuoi verificare che il server stia rispondendo correttamente ai dati, puoi visitare:
    `http://localhost:8080/api/game/status`
    Dovresti vedere un file di testo (JSON) con i dati tecnici della partita attuale.