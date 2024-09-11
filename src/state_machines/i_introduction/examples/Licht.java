package state_machines.i_introduction.examples;

public class Licht {
    private boolean istAn;
    
    public void anschalten() {
        this.istAn = true;
    }
    
    public void ausschalten() {
        this.istAn = false;
    }
}
