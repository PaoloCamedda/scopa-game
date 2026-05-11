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

        // IMPORTANTE: Partiamo dai punti accumulati con le scope durante la partita
        // Se non hai un metodo setPunti, assicurati che incrementaPunti aggiunga al totale esistente
        // u.setPunti(u.getScope());
        // c.setPunti(c.getScope());

        // 1. Settebello (7 di Oro)
        if (controllaSettebello(u)) u.incrementaPunti(1);
        else c.incrementaPunti(1);

        // 2. Maggior numero di carte (almeno 21 carte)
        if (u.getMazzettoPreso().size() > 20) u.incrementaPunti(1);
        else if (c.getMazzettoPreso().size() > 20) c.incrementaPunti(1);

        // 3. Denari (BUG FIX: rimosso .equals("ORO") a favore del confronto Enum ==)
        long denariU = u.getMazzettoPreso().stream()
                .filter(card -> card.getSeme() == Carta.Seme.ORO).count();
        long denariC = c.getMazzettoPreso().stream()
                .filter(card -> card.getSeme() == Carta.Seme.ORO).count();

        if (denariU > 5) u.incrementaPunti(1);
        else if (denariC > 5) c.incrementaPunti(1);

        // 4. Primiera
        int primieraU = calcolaValorePrimiera(u);
        int primieraC = calcolaValorePrimiera(c);

        if (primieraU > primieraC) u.incrementaPunti(1);
        else if (primieraC > primieraU) c.incrementaPunti(1);
    }

    /**
     * Calcola il punteggio della Primiera per un giocatore.
     * Si prende la carta con il valore di primiera più alto per ogni seme.
     */
    private boolean controllaSettebello(Giocatore g) {
        return g.getMazzettoPreso().stream()
                .anyMatch(c -> c.getValore() == 7 && c.getSeme() == Carta.Seme.ORO);
    }

    private int calcolaValorePrimiera(Giocatore g) {
        int[] valoriPrimiera = {0, 16, 12, 13, 14, 15, 18, 21, 10, 10, 10};
        int[] miglioriPerSeme = {0, 0, 0, 0};

        for (Carta c : g.getMazzettoPreso()) {
            int semeIndex = -1;
            // Usa direttamente l'Enum nel switch
            switch (c.getSeme()) {
                case ORO:      semeIndex = 0; break;
                case COPPE:    semeIndex = 1; break;
                case SPADE:    semeIndex = 2; break;
                case BASTONI:  semeIndex = 3; break;
            }

            if (semeIndex != -1) {
                int valoreAttuale = valoriPrimiera[c.getValore()];
                if (valoreAttuale > miglioriPerSeme[semeIndex]) {
                    miglioriPerSeme[semeIndex] = valoreAttuale;
                }
            }
        }

        int totale = 0;
        for (int v : miglioriPerSeme) totale += v;
        return totale;
    }
}