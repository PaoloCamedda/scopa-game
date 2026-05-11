package com.example.scopa;

import com.example.scopa.model.*;
import com.example.scopa.service.RegoleService;
import com.example.scopa.service.RegoleService.*;
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

    @Test
    public void testCalcoloPunteggi() {
        // 1. Inizializziamo i giocatori
        Giocatore umano = new Giocatore("Paolo");
        Giocatore cpu = new Giocatore("CPU");
        Partita partitaSimulata = new Partita("TestPartita");

        umano = partitaSimulata.getUmano();
        cpu = partitaSimulata.getCpu();

        // 2. Simuliamo le prese dell'UMANO (Puntiamo a fargli vincere Denari e Primiera)
        // Diamo all'umano il Settebello e un po' di ori
        umano.getMazzettoPreso().add(new Carta(7, Carta.Seme.ORO));    // Settebello!
        umano.getMazzettoPreso().add(new Carta(6, Carta.Seme.ORO));
        umano.getMazzettoPreso().add(new Carta(1, Carta.Seme.ORO));    // Asso Oro
        umano.getMazzettoPreso().add(new Carta(5, Carta.Seme.ORO));
        umano.getMazzettoPreso().add(new Carta(4, Carta.Seme.ORO));
        umano.getMazzettoPreso().add(new Carta(3, Carta.Seme.ORO));    // 6 Ori totali (Vince Denari)

        // Aggiungiamo carte per la Primiera dell'umano (7 di altri semi)
        umano.getMazzettoPreso().add(new Carta(7, Carta.Seme.SPADE));   // 21 punti
        umano.getMazzettoPreso().add(new Carta(7, Carta.Seme.COPPE));   // 21 punti
        umano.getMazzettoPreso().add(new Carta(6, Carta.Seme.BASTONI)); // 18 punti
        // Totale Primiera Umano: 21 (Oro) + 21 (Spade) + 21 (Coppe) + 18 (Bastoni) = 81

        // 3. Simuliamo le prese della CPU (Le diamo più carte totali per farle vincere "Carte")
        for (int i = 1; i <= 25; i++) {
            cpu.getMazzettoPreso().add(new Carta(2, Carta.Seme.BASTONI)); // 25 carte totali
        }
        // Totale Primiera CPU: molto bassa (solo un 2 di bastoni)

        // 4. Eseguiamo il calcolo
        calcolaPunteggioFinale(partitaSimulata);

        // 5. Stampiamo i risultati
        System.out.println("--- RISULTATI TEST ---");
        System.out.println("Punti Umano: " + umano.getPunti() + " (Dovrebbe avere: Settebello, Denari, Primiera = 3)");
        System.out.println("Punti CPU: " + cpu.getPunti() + " (Dovrebbe avere: Carte = 1)");
    }

    public void calcolaPunteggioFinale(Partita partita) {
        Giocatore u = partita.getUmano();
        Giocatore c = partita.getCpu();

        // 1. Settebello (7 di Oro)
        if (controllaSettebello(u)) u.incrementaPunti(1);
        else c.incrementaPunti(1);

        // 2. Maggior numero di carte (almeno 21 carte)
        if (u.getMazzettoPreso().size() > 20) u.incrementaPunti(1);
        else if (c.getMazzettoPreso().size() > 20) c.incrementaPunti(1);

        // 3. Denari (maggior numero di carte di Oro - almeno 6)
        long denariU = u.getMazzettoPreso().stream()
                .filter(card -> card.getSeme().equals("ORO")).count();
        long denariC = c.getMazzettoPreso().stream()
                .filter(card -> card.getSeme().equals("ORO")).count();

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
    private int calcolaValorePrimiera(Giocatore g) {
        // Mappa dei valori della Primiera (secondo le regole classiche)
        // 7 -> 21 | 6 -> 18 | Asso -> 16 | 5 -> 15 | 4 -> 14 | 3 -> 13 | 2 -> 12 | Figure -> 10
        int[] valoriPrimiera = {0, 16, 12, 13, 14, 15, 18, 21, 10, 10, 10};
        // l'indice 1 è l'Asso, 7 è il sette, 8-9-10 sono le figure

        int[] miglioriPerSeme = {0, 0, 0, 0}; // 0:ORO, 1:COPPE, 2:SPADE, 3:BASTONI

        for (Carta c : g.getMazzettoPreso()) {
            int semeIndex = 0;
            switch (c.getSeme()) {
                case ORO:      semeIndex = 0; break;
                case COPPE:    semeIndex = 1; break;
                case SPADE:    semeIndex = 2; break;
                case BASTONI:  semeIndex = 3; break;
            }

            int valoreAttuale = valoriPrimiera[c.getValore()];
            if (valoreAttuale > miglioriPerSeme[semeIndex]) {
                miglioriPerSeme[semeIndex] = valoreAttuale;
            }
        }

        // Se mancano carte di un seme, tecnicamente la Primiera è incompleta,
        // ma la somma dei migliori valori determina comunque il vincitore.
        int totale = 0;
        for (int v : miglioriPerSeme) totale += v;
        return totale;
    }

    private boolean controllaSettebello(Giocatore g) {
        return g.getMazzettoPreso().stream()
                .anyMatch(c -> c.getValore() == 7 && c.getSeme().equals("ORO"));
    }
}