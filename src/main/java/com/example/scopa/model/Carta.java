package com.example.scopa.model;

import java.util.Objects;

public class Carta {
    private final int valore; // Da 1 a 10
    private final Seme seme;
    private final String id; // Utile per associare l'immagine nel frontend (es. ORO_7)

    public enum Seme {
        ORO, COPPE, SPADE, BASTONI
    }

    public Carta(int valore, Seme seme) {
        this.valore = valore;
        this.seme = seme;
        this.id = seme.name() + "_" + valore;
    }

    // Getter
    public int getValore() { return valore; }
    public Seme getSeme() { return seme; }
    public String getId() { return id; }

    @Override
    public String toString() {
        return valore + " di " + seme;
    }

    // Fondamentale per i confronti nelle liste (es. se il tavolo contiene questa carta)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Carta carta = (Carta) o;
        return valore == carta.valore && seme == carta.seme;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valore, seme);
    }
}