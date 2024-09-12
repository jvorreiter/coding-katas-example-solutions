package state_machines.i_introduction.examples.simple;

import state_machines.i_introduction.examples.NpcStateMachine;

public class Npc {
    private enum Zustand {
        HERUMLAUFEN,
        ANGREIFEN,
        NACH_SPIELER_SUCHEN,
        VERSTECKEN,
        IDLE
    }

    private enum Ereignis {
        SPIELER_GESICHTET,
        SPIELER_AUS_SICHTFELD_VERLOREN,
        SPIELER_VERGESSEN,
        LEBENSPUNKTE_SIND_NIEDRIG,
        ZEIT_VERGANGEN_10_SEKUNDEN
    }

    private Zustand state;
    private int lebenspunkte;

    public Npc() {
        lebenspunkte = 100;
        state = Zustand.HERUMLAUFEN;
        herumlaufen();
    }

    public void spielerGesichtet(Object spieler) {
        if (state == Zustand.HERUMLAUFEN || state == Zustand.NACH_SPIELER_SUCHEN) {
            state = Zustand.ANGREIFEN;
            spielerAngreifen(spieler);
        }
    }

    public void spielerAusSichtfeldVerloren(Object letzteBekanntePosition) {
        if (state == Zustand.ANGREIFEN) {
            state = Zustand.NACH_SPIELER_SUCHEN;
            nachSpielerSuchen(letzteBekanntePosition);
        }
    }

    public void spielerVergessen() {
        if (state == Zustand.VERSTECKEN) {
            state = Zustand.HERUMLAUFEN;
            herumlaufen();
        } else if (state == Zustand.NACH_SPIELER_SUCHEN) {
            state = Zustand.IDLE;
            idle();
        }
    }

    public void updateIdleZeit(int sekunden) {
        if (sekunden >= 10 && state == Zustand.IDLE) {
            state = Zustand.HERUMLAUFEN;
            herumlaufen();
        }
    }

    public void updateLebenspunkte(int lebenspunkte) {
        this.lebenspunkte = lebenspunkte;

        if (lebenspunkte < 25 && state == Zustand.ANGREIFEN) {
            state = Zustand.VERSTECKEN;
            verstecken();
        }
    }

    private void herumlaufen() {
    }

    private void spielerAngreifen(Object spieler) {
    }

    private void verstecken() {
    }

    private void nachSpielerSuchen(Object letzteBekanntePosition) {
    }

    private void idle() {
    }
}
