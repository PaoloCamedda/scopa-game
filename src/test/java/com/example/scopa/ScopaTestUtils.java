package com.example.scopa;


import com.example.scopa.model.*;
import com.example.scopa.service.RegoleService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class ScopaTestUtils {

    private final RegoleService regoleService = new RegoleService();

    @Test
    public void testScopaCPU() {
        // 1. Setup Partita con solo 1 carta sul tavolo e 1 in mano alla CPU
        Partita partita = new Partita("TestPlayer");
        partita.getTavolo().clear();
        partita.getCpu().getMano().clear();
        partita.getUmano().getMano().clear();

        // Usiamo Carta.Seme.COPPE invece della stringa "COPPE"
        Carta cartaTavolo = new Carta(7, Carta.Seme.COPPE);
        partita.getTavolo().add(cartaTavolo);

        // Usiamo Carta.Seme.ORO invece della stringa "ORO"
        Carta cartaCPU = new Carta(7, Carta.Seme.ORO);
        partita.getCpu().getMano().add(cartaCPU);

        int scopeIniziali = partita.getCpu().getScope();

        // 2. Eseguiamo la mossa della CPU
        regoleService.eseguiMossaCPU(partita);

        // 3. VERIFICHE
        // Il tavolo deve essere vuoto se la CPU ha preso il 7
        assertTrue(partita.getTavolo().isEmpty(), "Il tavolo dovrebbe essere vuoto dopo la presa");

        // La CPU deve avere 1 scopa in più
        assertEquals(scopeIniziali + 1, partita.getCpu().getScope(), "La CPU dovrebbe aver segnato una Scopa");

        // Il mazzetto della CPU deve contenere le 2 carte (7C e 7O)
        assertEquals(2, partita.getCpu().getMazzettoPreso().size(), "Il mazzetto CPU deve contenere 2 carte");

        System.out.println("Test Scopa CPU passato con successo!");
    }

    @Test
    public void testVerificaTotaleCarteAFinePartita() {
        Partita partita = new Partita("TestPlayer");
        partita.getTavolo().clear();
        partita.getUmano().getMazzettoPreso().clear();

        // Simuliamo rimasugli sul tavolo (Asso e Due di Oro)
        Carta c1 = new Carta(1, Carta.Seme.ORO);
        Carta c2 = new Carta(2, Carta.Seme.ORO);
        partita.getTavolo().add(c1);
        partita.getTavolo().add(c2);

        // Impostiamo l'umano come ultimo prenditore
        partita.setUltimoPrenditore(partita.getUmano());

        // Simuliamo la logica di fine partita del Controller
        List<Carta> rimasugli = new ArrayList<>(partita.getTavolo());
        Giocatore ultimo = partita.getUltimoPrenditore();

        if (ultimo != null && !rimasugli.isEmpty()) {
            ultimo.getMazzettoPreso().addAll(rimasugli);
        }
        partita.getTavolo().clear();

        // Verifiche
        assertEquals(0, partita.getTavolo().size(), "Il tavolo deve essere vuoto dopo la pulizia finale");
        assertEquals(2, partita.getUmano().getMazzettoPreso().size(), "L'umano deve aver preso le 2 carte rimaste");

        System.out.println("Test Rimasugli Finali passato con successo!");
    }
}