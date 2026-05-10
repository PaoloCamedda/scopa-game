package com.example.scopa;

import com.example.scopa.model.*;
import com.example.scopa.service.RegoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ScopaIntegrationTest {

    private RegoleService regoleService;
    private Partita partita;

    @BeforeEach
    void setup() {
        regoleService = new RegoleService();
        partita = new Partita("Paolo");
        // Puliamo il tavolo e le mani per i test specifici
        partita.getTavolo().clear();
        partita.getUmano().getMano().clear();
        partita.getCpu().getMano().clear();
    }

    @Test
    void testScopaGiocatoreUmano() {
        // Scenario: 7 sul tavolo, io gioco un 7
        partita.getTavolo().add(new Carta(7, Carta.Seme.ORO));
        partita.getUmano().getMano().add(new Carta(7, Carta.Seme.BASTONI));

        Carta giocata = partita.getUmano().getMano().remove(0);
        List<List<Carta>> prese = regoleService.calcolaPresePossibili(giocata, partita.getTavolo());

        // Eseguiamo la presa (Logica che sta nel controller)
        if (!prese.isEmpty()) {
            List<Carta> daPrendere = prese.get(0);
            partita.getTavolo().removeAll(daPrendere);
            partita.getUmano().aggiungiAlMazzetto(daPrendere);
            partita.getUmano().getMazzettoPreso().add(giocata);
            if (partita.getTavolo().isEmpty()) {
                partita.getUmano().faiScopa();
            }
        }

        assertTrue(partita.getTavolo().isEmpty(), "Il tavolo dovrebbe essere vuoto");
        assertEquals(1, partita.getUmano().getScope(), "Dovrebbe esserci 1 scopa");
        assertEquals(2, partita.getUmano().getMazzettoPreso().size(), "Il mazzetto dovrebbe avere 2 carte");
    }

    @Test
    void testScopaCPU() {
        // Scenario: Tavolo ha 2 e 3. CPU ha un 5.
        partita.getTavolo().add(new Carta(2, Carta.Seme.COPPE));
        partita.getTavolo().add(new Carta(3, Carta.Seme.SPADE));
        partita.getCpu().getMano().add(new Carta(5, Carta.Seme.ORO));

        // Facciamo giocare la CPU
        regoleService.eseguiMossaCPU(partita);

        assertTrue(partita.getTavolo().isEmpty(), "La CPU doveva pulire il tavolo");
        assertEquals(1, partita.getCpu().getScope(), "La CPU doveva fare scopa");
        assertEquals(3, partita.getCpu().getMazzettoPreso().size(), "Mazzetto CPU: 2 prese + 1 giocata");
    }

    @Test
    void testSempliceScarto() {
        // Scenario: Tavolo ha un 10. Io gioco un 2.
        partita.getTavolo().add(new Carta(10, Carta.Seme.ORO));
        Carta due = new Carta(2, Carta.Seme.BASTONI);

        List<List<Carta>> prese = regoleService.calcolaPresePossibili(due, partita.getTavolo());

        if (prese.isEmpty()) {
            partita.getTavolo().add(due);
        }

        assertEquals(2, partita.getTavolo().size(), "Il tavolo dovrebbe avere 2 carte ora");
        assertTrue(partita.getTavolo().contains(due));
    }
}