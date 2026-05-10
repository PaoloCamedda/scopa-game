package com.example.scopa;

import com.example.scopa.model.*;
import com.example.scopa.service.RegoleService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PartitaCompletaTest {

    @Test
    void simulaPartitaIntera() {
        RegoleService regoleService = new RegoleService();
        Partita partita = new Partita("TestPlayer");

        int turniTotali = 0;
        int limiteSicurezza = 100; // Per evitare loop infiniti se qualcosa va storto

        System.out.println("--- Inizio Simula Partita ---");

        // Continua finché qualcuno ha carte in mano o il mazzo non è vuoto
        while ((!partita.getUmano().getMano().isEmpty() || partita.getMazzo().carteRimanenti() > 0)
                && turniTotali < limiteSicurezza) {

            // 1. Turno Umano: Gioca sempre la prima carta (indice 0)
            if (!partita.getUmano().getMano().isEmpty()) {
                Carta giocata = partita.getUmano().getMano().remove(0);
                var prese = regoleService.calcolaPresePossibili(giocata, partita.getTavolo());

                if (prese.isEmpty()) {
                    partita.getTavolo().add(giocata);
                } else {
                    var daPrendere = prese.get(0);
                    partita.getTavolo().removeAll(daPrendere);
                    partita.getUmano().aggiungiAlMazzetto(daPrendere);
                    partita.getUmano().getMazzettoPreso().add(giocata);
                }
            }

            // 2. Turno CPU
            if (!partita.getCpu().getMano().isEmpty()) {
                regoleService.eseguiMossaCPU(partita);
            }

            // 3. Controllo ridistribuzione
            partita.nuoveManiSeNecessario();

            turniTotali++;
        }

        // --- ASSERZIONI FINALI ---

        // Alla fine, le mani devono essere vuote
        assertTrue(partita.getUmano().getMano().isEmpty(), "L'umano dovrebbe aver finito le carte");
        assertTrue(partita.getCpu().getMano().isEmpty(), "La CPU dovrebbe aver finito le carte");
        assertEquals(0, partita.getMazzo().carteRimanenti(), "Il mazzo dovrebbe essere esaurito");

        // Calcoliamo i punti
        regoleService.calcolaPunteggioFinale(partita);

        System.out.println("Partita finita in " + turniTotali + " turni.");
        System.out.println("Carte prese Umano: " + partita.getUmano().getMazzettoPreso().size());
        System.out.println("Carte prese CPU: " + partita.getCpu().getMazzettoPreso().size());
        System.out.println("Punti finali Umano: " + partita.getUmano().getPunti());
        System.out.println("Punti finali CPU: " + partita.getCpu().getPunti());

        // La somma delle carte nei mazzetti + quelle rimaste a terra deve fare 40
        int totaleCarte = partita.getUmano().getMazzettoPreso().size() +
                partita.getCpu().getMazzettoPreso().size() +
                partita.getTavolo().size();

        assertEquals(40, totaleCarte, "Il totale delle carte tra mazzetti e tavolo deve essere 40");
    }
}