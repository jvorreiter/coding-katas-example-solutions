package state_machines.i_introduction.examples;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;

public class NpcStateMachine extends StateMachine<NpcStateMachine.Zustand, NpcStateMachine.Ereignis> {
    public enum Zustand {
        HERUMLAUFEN,
        ANGREIFEN,
        NACH_SPIELER_SUCHEN,
        VERSTECKEN
    }

    public enum Ereignis {
        SPIELER_GESICHTET,
        SPIELER_AUS_SICHTFELD_VERLOREN,
        SPIELER_VERGESSEN,
        LEBENSPUNKTE_SIND_NIEDRIG
    }

    public NpcStateMachine() {
        super(Zustand.HERUMLAUFEN, getConfig());
    }

    private static StateMachineConfig<Zustand, Ereignis> getConfig() {
        StateMachineConfig<Zustand, Ereignis> config = new StateMachineConfig<>();

        config.configure(Zustand.HERUMLAUFEN)
                //.onEntry(() -> herumlaufen())
                .permit(Ereignis.SPIELER_GESICHTET, Zustand.ANGREIFEN);

        config.configure(Zustand.ANGREIFEN)
                //.onEntry(() -> spielerAngreifen())
                .permit(Ereignis.SPIELER_AUS_SICHTFELD_VERLOREN, Zustand.NACH_SPIELER_SUCHEN)
                .permit(Ereignis.LEBENSPUNKTE_SIND_NIEDRIG, Zustand.VERSTECKEN);

        config.configure(Zustand.NACH_SPIELER_SUCHEN)
                //.onEntry(() -> nachSpielerSuchen())
                .permit(Ereignis.SPIELER_GESICHTET, Zustand.ANGREIFEN)
                .permit(Ereignis.SPIELER_VERGESSEN, Zustand.HERUMLAUFEN);

        config.configure(Zustand.VERSTECKEN)
                //.onEntry(() -> verstecken()))
                .permit(Ereignis.SPIELER_VERGESSEN, Zustand.HERUMLAUFEN);

        return config;
    }
}
