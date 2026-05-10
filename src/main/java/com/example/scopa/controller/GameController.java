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
        Giocatore umano = partitaCorrente.getUmano();
        List<Carta> mano = umano.getMano();
        List<Carta> tavolo = partitaCorrente.getTavolo();

        if (index >= mano.size()) return "Indice carta non valido!";

        // --- 1. TURNO UMANO ---
        Carta giocata = mano.remove(index);
        List<List<Carta>> possibiliPrese = regoleService.calcolaPresePossibili(giocata, tavolo);

        if (possibiliPrese.isEmpty()) {
            tavolo.add(giocata);
        } else {
            List<Carta> daPrendere = possibiliPrese.get(0);
            tavolo.removeAll(daPrendere);
            umano.aggiungiAlMazzetto(daPrendere);
            umano.getMazzettoPreso().add(giocata);
            if (tavolo.isEmpty()) umano.faiScopa();
        }

        // --- 2. TURNO CPU (Spostato fuori, viene eseguito SEMPRE) ---
        if (!partitaCorrente.getCpu().getMano().isEmpty()) {
            regoleService.eseguiMossaCPU(partitaCorrente);
        }

        // --- 3. GESTIONE NUOVE MANI / FINE PARTITA ---
        if (umano.getMano().isEmpty() && partitaCorrente.getCpu().getMano().isEmpty()) {
            boolean mazzoHaAncoraCarte = partitaCorrente.getMazzo().carteRimanenti() > 0;

            if (mazzoHaAncoraCarte) {
                partitaCorrente.nuoveManiSeNecessario();
                return "Nuove carte distribuite!";
            } else {
                regoleService.calcolaPunteggioFinale(partitaCorrente);
                return "Partita terminata!";
            }
        }

        return "Mossa registrata.";
    }
}