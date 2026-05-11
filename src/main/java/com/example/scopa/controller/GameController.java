package com.example.scopa.controller;

import com.example.scopa.model.*;
import com.example.scopa.service.RegoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @GetMapping("/start")
    public Partita start(@RequestParam String nome) {
        partitaCorrente = new Partita(nome);
        return partitaCorrente;
    }

    @GetMapping("/status")
    public Partita getStatus() {
        return partitaCorrente;
    }

    @PostMapping("/play")
    public ResponseEntity<String> giocaCarta(@RequestBody MossaRequest request) {
        int index = request.getIndexMano();
        List<Integer> indiciTavoloScelti = request.getIndiciTavolo();

        Giocatore umano = partitaCorrente.getUmano();
        List<Carta> mano = umano.getMano();
        List<Carta> tavolo = partitaCorrente.getTavolo();

        if (index >= mano.size()) return ResponseEntity.badRequest().body("Indice non valido");

        Carta giocata = mano.get(index);

        // --- 1. LOGICA TURNO UMANO ---
        if (indiciTavoloScelti != null && !indiciTavoloScelti.isEmpty()) {
            List<Carta> daPrendere = new ArrayList<>();
            int somma = 0;
            for (int i : indiciTavoloScelti) {
                daPrendere.add(tavolo.get(i));
                somma += tavolo.get(i).getValore();
            }

            if (somma == giocata.getValore()) {
                // Controllo presa diretta obbligatoria
                List<List<Carta>> possibili = regoleService.calcolaPresePossibili(giocata, tavolo);
                boolean haDiretta = possibili.stream().anyMatch(l -> l.size() == 1);

                if (haDiretta && daPrendere.size() > 1) {
                    return ResponseEntity.ok("Errore: Devi prendere la carta di valore uguale!");
                }

                // Eseguo la presa
                mano.remove(index);
                tavolo.removeAll(daPrendere);
                umano.getMazzettoPreso().addAll(daPrendere);
                umano.getMazzettoPreso().add(giocata);
                partitaCorrente.setUltimoPrenditore(umano);

                if (tavolo.isEmpty()) umano.faiScopa();
            } else {
                return ResponseEntity.ok("Errore: La somma non corrisponde!");
            }
        } else {
            // Calo della carta
            if (!regoleService.calcolaPresePossibili(giocata, tavolo).isEmpty()) {
                return ResponseEntity.ok("Errore: Ci sono prese possibili, seleziona le carte!");
            }
            mano.remove(index);
            tavolo.add(giocata);
        }

        // --- 2. TURNO CPU ---
        if (!partitaCorrente.getCpu().getMano().isEmpty()) {
            regoleService.eseguiMossaCPU(partitaCorrente);
            // Nota: assicurati che eseguiMossaCPU faccia: partita.setUltimoPrenditore(cpu) se prende
        }

        // --- 3. GESTIONE FINE MANO / PARTITA ---
        if (umano.getMano().isEmpty() && partitaCorrente.getCpu().getMano().isEmpty()) {
            if (partitaCorrente.getMazzo().carteRimanenti() > 0) {
                partitaCorrente.nuoveManiSeNecessario();
                return ResponseEntity.ok("Nuove carte distribuite!");
            } else {
                // FINE PARTITA: Assegnazione rimasugli tavolo
                List<Carta> rimasugli = new ArrayList<>(tavolo);
                Giocatore ultimo = partitaCorrente.getUltimoPrenditore();
                if (ultimo != null && !rimasugli.isEmpty()) {
                    ultimo.getMazzettoPreso().addAll(rimasugli);
                }
                tavolo.clear();

                regoleService.calcolaPunteggioFinale(partitaCorrente);
                return ResponseEntity.ok("Partita terminata!");
            }
        }

        return ResponseEntity.ok("Mossa registrata.");
    }
}