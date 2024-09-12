package state_machines.i_introduction.examples.simple;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import state_machines.i_introduction.examples.Licht;
import state_machines.i_introduction.examples.NpcStateMachine;

public class Lichtschalter {
    private boolean istAn;
    private final Licht licht;

    public Lichtschalter(boolean istAn, Licht licht) {
        this.istAn = istAn;
        this.licht = licht;

        if(this.istAn) {
            this.licht.anschalten();
        }
    }

    public void schalterUmlegen() {
        this.istAn = !this.istAn;

        if(this.istAn) {
            this.licht.anschalten();
        } else {
            this.licht.ausschalten();
        }
    }

    public void stromausfall() {
        this.istAn = false;

        this.licht.ausschalten();
    }
}


