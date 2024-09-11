package state_machines.i_introduction.examples;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;

public class LichtschalterStateMachine {
    public enum Zustand {UNINITIALIZED, AN, AUS}

    public enum Ereignis {INITIALIZE, LICHTSCHALTER_UMLEGEN, STROMAUSFALL}

    private final Licht licht;
    private final StateMachine<Zustand, Ereignis> stateMachine;

    public LichtschalterStateMachine(Zustand startzustand, Licht licht) {
        this.licht = licht;
        this.stateMachine = createStateMachine(startzustand);
    }

    public void ausloesen(Ereignis ereignis) {
        this.stateMachine.fire(ereignis);
    }

    private StateMachine<Zustand, Ereignis> createStateMachine(Zustand startzustand) {
        StateMachineConfig<Zustand, Ereignis> config = new StateMachineConfig<>();

        config.configure(Zustand.AUS)
                .onEntry(this.licht::ausschalten)
                .permit(Ereignis.LICHTSCHALTER_UMLEGEN, Zustand.AN)
                .permit(Ereignis.STROMAUSFALL, Zustand.AUS);

        config.configure(Zustand.AN)
                .onEntry(this.licht::anschalten)
                .permit(Ereignis.LICHTSCHALTER_UMLEGEN, Zustand.AUS)
                .permit(Ereignis.STROMAUSFALL, Zustand.AUS);

        // ist hier so, damit für den Übergang in den Startzustand ebenfalls onEntry ausgelöst wird
        config.configure(Zustand.UNINITIALIZED)
                .permit(Ereignis.INITIALIZE, startzustand);

        var stateMachine = new StateMachine<>(Zustand.UNINITIALIZED, config);
        stateMachine.fire(Ereignis.INITIALIZE);

        return stateMachine;
    }
}
