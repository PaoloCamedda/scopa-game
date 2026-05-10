package com.example.scopa.controller;

import com.example.scopa.model.Carta;
import com.example.scopa.model.Giocatore;
import com.example.scopa.model.Partita;
import com.example.scopa.service.RegoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameController {

    private Partita partitaCorrente;
    private final RegoleService regoleService;

    public GameController(RegoleService regoleService) {
        this.regoleService = regoleService;
    }
    /** Inizia la partita */
    @GetMapping("/start")
    public Partita start(@RequestParam String nome) {
        partitaCorrente = new Partita(nome);
        return partitaCorrente;
    }

    /**
     *
     * @return
     */
    @GetMapping("/status")
    public Partita getStatus() {
        return partitaCorrente;
    }

    /**
     * Gioca un turno umano e cpu
     * @param index
     * @return
     */
    @GetMapping("/play")
    public String giocaCarta(@RequestParam int index) {
        Giocatore p = partitaCorrente.getUmano();
        List<Carta> mano = p.getMano();
        List<Carta> tavolo = partitaCorrente.getTavolo();

        if (index >= mano.size()) return "Indice carta non valido!";

        // 1. Il giocatore sceglie la carta
        Carta giocata = mano.remove(index);

        // 2. Chiediamo al RegoleService cosa possiamo prendere
        List<List<Carta>> possibiliPrese = regoleService.calcolaPresePossibili(giocata, tavolo);

        if (possibiliPrese.isEmpty()) {
            // NON C'È PRESA: La carta resta sul tavolo
            tavolo.add(giocata);
            return "Nessuna presa. Hai lasciato il " + giocata + " sul tavolo.";
        } else {
            // C'È UNA PRESA: Per ora prendiamo la prima combinazione trovata (semplificazione)
            List<Carta> daPrendere = possibiliPrese.get(0);

            // Rimuoviamo le carte dal tavolo e aggiungiamole al mazzetto del giocatore
            tavolo.removeAll(daPrendere);
            p.aggiungiAlMazzetto(daPrendere);
            p.getMazzettoPreso().add(giocata); // Aggiungiamo anche la carta che abbiamo usato per prendere

            // Controllo SCOPA
            String messaggioScopa = "";
            if (tavolo.isEmpty()) {
                p.faiScopa();
                messaggioScopa = " - SCOPA!!!";
            }

            if (!partitaCorrente.getCpu().getMano().isEmpty()) {
                regoleService.eseguiMossaCPU(partitaCorrente);
            }

            // Se entrambi hanno finito le carte, dobbiamo distribuirne altre 3 a testa
            if (partitaCorrente.getUmano().getMano().isEmpty() &&
                    partitaCorrente.getCpu().getMano().isEmpty()) {

                boolean nuoveCarte = partitaCorrente.nuoveManiSeNecessario();

                if (partitaCorrente.getUmano().getMano().isEmpty() && partitaCorrente.getMazzo().carteRimanenti() == 0) {
                    regoleService.calcolaPunteggioFinale(partitaCorrente);
                    return "Partita terminata! Guarda i punti nello /status";
                }

                return nuoveCarte ? "Nuove carte distribuite!" : "Mossa registrata.";
            }

            return "Mossa completata. Controlla lo /status per vedere la risposta della CPU!";
        }
    }
}