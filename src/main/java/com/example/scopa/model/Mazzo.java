package com.example.scopa.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mazzo {
    private List<Carta> carte;

    public Mazzo() {
        this.carte = new ArrayList<>();
        // Genera le 40 carte (10 valori per 4 semi)
        for (Carta.Seme seme : Carta.Seme.values()) {
            for (int i = 1; i <= 10; i++) {
                carte.add(new Carta(i, seme));
            }
        }
        mescola();
    }

    public void mescola() {
        Collections.shuffle(carte);
    }

    public Carta pesca() {
        if (carte.isEmpty()) {
            return null;
        }
        return carte.remove(0); // Rimuove e restituisce la prima carta
    }

    public int carteRimanenti() {
        return carte.size();
    }
}