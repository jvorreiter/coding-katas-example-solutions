package algodat.ii_algorithms_for_realistic_problems.sequential_partial_sums;

public class SequenceWindow {
    private final int[] sequence;
    private final int maxWindowSize;
    private int startIndex = 0;
    private int size = 0;
    private int windowSum = 0;

    public SequenceWindow(int[] sequence, int maxWindowSize) {
        this.sequence = sequence;
        this.maxWindowSize = maxWindowSize;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void moveWindow(int steps) {
        this.startIndex += steps;
    }

    public boolean advance() {
        // Fenster hat das Ende der Sequence erreicht
        if (this.startIndex + this.size >= this.sequence.length) {
            return false;
        }

        // Fenstergröße ist noch kleiner als Maximalgröße, also "expandieren" wir nur nach rechts
        // hier muss dann also nur der Wert am neuen rechte Index zur Fenstersumme gerechnet werden (#2)
        if (this.size < this.maxWindowSize) {
            this.size++;
        }
        // Fenster wird um einen Index nach rechts verschoben
        // hier muss der linke Wert, der aus dem Fenster verschwindet, von der Summe abgezogen (#1),
        // und im Anschluss der Wert am neuen rechte Index zur Fenstersumme gerechnet werden (#2)
        else {
            this.startIndex++;

            var indexRemovedFromWindow = this.startIndex - 1;
            this.windowSum -= this.sequence[indexRemovedFromWindow]; // #1
        }

        var newIndexInWindow = this.startIndex + this.size - 1;
        this.windowSum += this.sequence[newIndexInWindow]; // #2

        return true;
    }

    public int getWindowSum() {
        return windowSum;
    }

    public int getSize() {
        return size;
    }

    public int getEndIndex() {
        return startIndex + size - 1;
    }

    public void resetSize() {
        this.size = 0;
        this.windowSum = 0;
    }
}
