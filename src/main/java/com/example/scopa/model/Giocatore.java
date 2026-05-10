package com.example.scopa.model;

import java.util.ArrayList;
import java.util.List;

public class Giocatore {
    private String nome;
    private List<Carta> mano;           // Le carte che il giocatore può giocare
    private List<Carta> mazzettoPreso;  // Le carte accumulate per il punteggio finale
    private int scope;// Numero di scope fatte
    private int punti;

    public Giocatore(String nome) {
        this.nome = nome;
        this.mano = new ArrayList<>();
        this.mazzettoPreso = new ArrayList<>();
        this.scope = 0;
        this.punti = 0;
    }

    public void aggiungiCartaInMano(Carta c) {
        if (c != null) {
            this.mano.add(c);
        }
    }

    public void incrementaPunti(int p) {
        this.punti += p;
    }

    public void aggiungiAlMazzetto(List<Carta> carte) {
        this.mazzettoPreso.addAll(carte);
    }

    public void faiScopa() {
        this.scope++;
    }

    // Getter e Setter
    public String getNome() { return nome; }
    public List<Carta> getMano() { return mano; }
    public List<Carta> getMazzettoPreso() { return mazzettoPreso; }
    public int getScope() { return scope; }
    public int getPunti() { return punti; }
}