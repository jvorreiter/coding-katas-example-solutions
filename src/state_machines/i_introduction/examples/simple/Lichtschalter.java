package state_machines.i_introduction.examples.simple;

import state_machines.i_introduction.examples.Licht;

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


