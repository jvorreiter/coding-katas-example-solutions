package state_machines.i_introduction.examples;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;

public class NpcStateMachine {
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

    private final StateMachine<NpcStateMachine.Zustand, NpcStateMachine.Ereignis> stateMachine;
    private int lebenspunkte;

    public NpcStateMachine() {
        lebenspunkte = 100;

        stateMachine = createStateMachine();
        herumlaufen();
    }

    public void spielerGesichtet(Object spieler) {
        stateMachine.fire(new TriggerWithParameters1<>(Ereignis.SPIELER_GESICHTET, Object.class), spieler);
    }

    public void spielerAusSichtfeldVerloren(Object letzteBekanntePosition) {
        stateMachine.fire(new TriggerWithParameters1<>(Ereignis.SPIELER_AUS_SICHTFELD_VERLOREN, Object.class), letzteBekanntePosition);
    }

    public void spielerVergessen() {
        stateMachine.fire(Ereignis.SPIELER_VERGESSEN);
    }

    public void updateIdleZeit(int sekunden) {
        if (sekunden >= 10) {
            stateMachine.fire(Ereignis.ZEIT_VERGANGEN_10_SEKUNDEN);
        }
    }

    public void updateLebenspunkte(int lebenspunkte) {
        this.lebenspunkte = lebenspunkte;

        if (lebenspunkte < 25) {
            stateMachine.fire(Ereignis.LEBENSPUNKTE_SIND_NIEDRIG);
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

    private StateMachine<Zustand, Ereignis> createStateMachine() {
        StateMachineConfig<Zustand, Ereignis> config = new StateMachineConfig<>();

        config.configure(Zustand.HERUMLAUFEN)
                .onEntry(this::herumlaufen)
                .permit(Ereignis.SPIELER_GESICHTET, Zustand.ANGREIFEN);

        config.configure(Zustand.ANGREIFEN)
                .onEntry(this::spielerAngreifen)
                .permit(Ereignis.SPIELER_AUS_SICHTFELD_VERLOREN, Zustand.NACH_SPIELER_SUCHEN)
                .permit(Ereignis.LEBENSPUNKTE_SIND_NIEDRIG, Zustand.VERSTECKEN);

        config.configure(Zustand.NACH_SPIELER_SUCHEN)
                .onEntry(this::nachSpielerSuchen)
                .permit(Ereignis.SPIELER_GESICHTET, Zustand.ANGREIFEN)
                .permit(Ereignis.SPIELER_VERGESSEN, Zustand.IDLE);

        config.configure(Zustand.VERSTECKEN)
                .onEntry(this::verstecken)
                .permit(Ereignis.SPIELER_VERGESSEN, Zustand.HERUMLAUFEN);

        config.configure(Zustand.IDLE)
                .onEntry(this::idle)
                .permit(Ereignis.ZEIT_VERGANGEN_10_SEKUNDEN, Zustand.HERUMLAUFEN);

        var stateMachine = new StateMachine<>(Zustand.HERUMLAUFEN, config);

        // Ereignis löst keinen bekannten Übergang aus -> nichts machen
        stateMachine.onUnhandledTrigger((zustand, ereignis) -> {
        });

        return stateMachine;
    }
}
