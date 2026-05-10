package com.example.scopa.service;

import com.example.scopa.model.*;
import com.example.scopa.controller.GameController.*;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class RegoleService {

    /**
     * calcola se è possibile prendere delle carte dal tavolo
     * @param giocata
     * @param tavolo
     * @return
     */

    public List<List<Carta>> calcolaPresePossibili(Carta giocata, List<Carta> tavolo) {
        List<List<Carta>> combinazioniValide = new ArrayList<>();

        // 1. Regola d'oro della Scopa: se c'è il valore esatto, prendi quello e basta
        for (Carta c : tavolo) {
            if (c.getValore() == giocata.getValore()) {
                List<Carta> presaDiretta = new ArrayList<>();
                presaDiretta.add(c);
                combinazioniValide.add(presaDiretta);
                return combinazioniValide;
            }
        }

        // 2. Se non c'è la diretta, cerchiamo le somme
        trovaCombinazioniSomma(tavolo, giocata.getValore(), 0, new ArrayList<>(), combinazioniValide);

        // Rimuoviamo eventuali duplicati logici (se presenti)
        return combinazioniValide.stream().distinct().toList();
    }

    /**
     * Calcola le carte che puoi prendere con una somma
     * @param tavolo
     * @param target
     * @param index
     * @param corrente
     * @param risultato
     */
    private void trovaCombinazioniSomma(List<Carta> tavolo, int target, int index,
                                        List<Carta> corrente, List<List<Carta>> risultato) {
        int sommaCorrente = corrente.stream().mapToInt(Carta::getValore).sum();

        if (sommaCorrente == target) {
            risultato.add(new ArrayList<>(corrente));
            return;
        }

        if (sommaCorrente > target || index >= tavolo.size()) {
            return;
        }

        for (int i = index; i < tavolo.size(); i++) {
            corrente.add(tavolo.get(i));
            // Passiamo i + 1 per non riprendere la stessa carta
            trovaCombinazioniSomma(tavolo, target, i + 1, corrente, risultato);
            corrente.remove(corrente.size() - 1); // Rimuovi l'ultima carta per provare un'altra combinazione
        }
    }

    /**
     * Genera la mossa della CPU
     * @param partita
     */
    public void eseguiMossaCPU(Partita partita) {
        Giocatore cpu = partita.getCpu();
        List<Carta> mano = cpu.getMano();
        List<Carta> tavolo = partita.getTavolo();

        if (mano.isEmpty()) return;

        // La CPU cerca una presa per ogni carta che ha in mano
        for (int i = 0; i < mano.size(); i++) {
            Carta c = mano.get(i);
            List<List<Carta>> prese = calcolaPresePossibili(c, tavolo);

            if (!prese.isEmpty()) {
                // La CPU fa la presa!
                mano.remove(i);
                List<Carta> daPrendere = prese.get(0);
                tavolo.removeAll(daPrendere);
                cpu.aggiungiAlMazzetto(daPrendere);
                cpu.getMazzettoPreso().add(c);

                if (tavolo.isEmpty()) cpu.faiScopa();
                return; // Fine turno CPU
            }
        }

        // Se non ha trovato prese, gioca la prima carta che ha
        Carta scartata = mano.remove(0);
        tavolo.add(scartata);
    }

    /**
     * calcolo dei punti a fine partita ovvero mazzo finito.
     * @param partita
     */
    public void calcolaPunteggioFinale(Partita partita) {
        Giocatore u = partita.getUmano();
        Giocatore c = partita.getCpu();

        // 1. Settebello (7 di Oro)
        if (controllaSetebello(u)) u.incrementaPunti(1);
        else c.incrementaPunti(1);

        // 2. Maggior numero di carte
        if (u.getMazzettoPreso().size() > c.getMazzettoPreso().size()) u.incrementaPunti(1);
        else if (c.getMazzettoPreso().size() > u.getMazzettoPreso().size()) c.incrementaPunti(1);

        // ... qui aggiungeremo Denari e Primiera ...
    }

    private boolean controllaSetebello(Giocatore g) {
        return g.getMazzettoPreso().stream()
                .anyMatch(carta -> carta.getValore() == 7 && carta.getSeme() == Carta.Seme.ORO);
    }
}