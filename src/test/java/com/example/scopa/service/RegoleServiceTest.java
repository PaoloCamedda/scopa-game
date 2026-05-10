package com.example.scopa.service;

import com.example.scopa.model.Carta;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RegoleServiceTest {

    private final RegoleService regoleService = new RegoleService();

    @Test
    void testPresaConSomma() {
        RegoleService service = new RegoleService();

        // Prepariamo un tavolo pulito
        List<Carta> tavolo = new ArrayList<>();
        tavolo.add(new Carta(2, Carta.Seme.ORO));
        tavolo.add(new Carta(3, Carta.Seme.COPPE));

        Carta giocata = new Carta(5, Carta.Seme.BASTONI);

        List<List<Carta>> prese = service.calcolaPresePossibili(giocata, tavolo);

        // DEBUG: Stampa cosa vede il test
        System.out.println("Numero combinazioni trovate: " + prese.size());
        for(List<Carta> comb : prese) {
            System.out.println("Combinazione: " + comb);
        }

        // Asserzioni corrette
        assertFalse(prese.isEmpty(), "Dovrebbe trovare almeno una combinazione");

        // Se ti aspettavi 1 sola combinazione (il 2 e il 3), scrivi così:
        assertEquals(1, prese.size(), "Dovrebbe esserci una sola combinazione possibile");

        // Controlla che quella combinazione contenga effettivamente le 2 carte del tavolo
        assertEquals(2, prese.get(0).size(), "La combinazione deve contenere 2 carte");
    }
}