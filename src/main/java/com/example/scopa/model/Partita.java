package com.example.scopa.model;

import java.util.ArrayList;
import java.util.List;
import com.example.scopa.model.Mazzo.*;
import lombok.Getter;
import lombok.Setter;

public class Partita {

    @Getter
    @Setter
    private Giocatore ultimoPrenditore;

    @Getter
    private Mazzo mazzo;
    @Getter
    private Giocatore umano;
    @Getter
    private Giocatore cpu;
    // Getters per il controller REST
    @Getter
    private List<Carta> tavolo;
    private boolean turnoUmano; // true = tocca a te, false = tocca alla CPU
    private Giocatore ultimoCheHaPreso; // Serve per assegnare le carte rimaste alla fine

    public Partita(String nomeGiocatore) {
        this.mazzo = new Mazzo();
        this.umano = new Giocatore(nomeGiocatore);
        this.cpu = new Giocatore("CPU");
        this.tavolo = new ArrayList<>();
        this.turnoUmano = true; // Iniziamo noi (o potresti fare un random)

        inizializzaPartita();
    }

    private void inizializzaPartita() {
        // 1. Distribuisci 3 carte a testa
        for (int i = 0; i < 3; i++) {
            umano.aggiungiCartaInMano(mazzo.pesca());
            cpu.aggiungiCartaInMano(mazzo.pesca());
        }
        // 2. Metti 4 carte sul tavolo
        for (int i = 0; i < 4; i++) {
            tavolo.add(mazzo.pesca());
        }
    }

    public boolean nuoveManiSeNecessario() {
        // Se entrambi hanno finito le carte e il mazzo non è vuoto
        if (umano.getMano().isEmpty() && cpu.getMano().isEmpty() && mazzo.carteRimanenti() > 0) {
            for (int i = 0; i < 3; i++) {
                umano.aggiungiCartaInMano(mazzo.pesca());
                cpu.aggiungiCartaInMano(mazzo.pesca());
            }
            return true; // Nuova mano distribuita
        }
        return false; // Non serve distribuire o mazzo esaurito
    }

}

