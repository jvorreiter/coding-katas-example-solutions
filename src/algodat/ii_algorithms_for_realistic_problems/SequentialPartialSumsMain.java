package algodat.ii_algorithms_for_realistic_problems;

import java.util.ArrayList;
import java.util.List;

public class SequentialPartialSumsMain {
    public static void main(String[] args) {
        int maximumSequenceLength = 4;
        int limit = 2000;

        //int[] sequence = {50, 500, 700, 1000, -30, 600, 1500, 7};
        int[] sequence = {50, 500, 700, 1000, -30, 600, 1500, 7, 2000, 2001, 1999, 1, 1, 1999, 2, 2000, -2000, 501, 500, 500, 500, 501, 2000};

        var finder = new SequentialPartialSumFinder();
        var foundSequences = finder.findSequencesWithSumThatExceedsLimit(maximumSequenceLength, limit, sequence);

        printSequences(limit, maximumSequenceLength, sequence, foundSequences);
    }

    private static void printSequences(int limit, int maximumSequenceLength, int[] sequence, List<SequenceResult> foundSequences) {
        System.out.printf("Maximale Anzahl von Transaktionen: %d\n", maximumSequenceLength);
        System.out.printf("Grenzwert: %d\n", limit);
        System.out.println();

        int[] charOffsets = new int[sequence.length + 1];
        for (int i = 0; i < sequence.length; i++) {
            var valueString = Integer.toString(sequence[i]);

            if (i > 0) {
                System.out.print(' ');
            }
            System.out.print(valueString);

            charOffsets[i + 1] = 1 + valueString.length() + charOffsets[i];
        }
        System.out.println();

        for(var foundSequence : foundSequences) {
            var startOffset = charOffsets[foundSequence.getStartIndex()];
            if(startOffset > 0) {
                System.out.printf("%" +startOffset + "s", "");
            }

            var printLength = charOffsets[foundSequence.getStartIndex() + foundSequence.getLength()] - startOffset - 1;
            System.out.print(String.format("%" +printLength + "s", "").replace(' ', '^'));
            System.out.printf(" (Wert: %d)", foundSequence.getValue());
            System.out.println();
        }
    }
}

class SequenceResult {
    private final int startIndex;
    private final int length;
    private final int value;

    public SequenceResult(int startIndex, int length, int value) {
        this.startIndex = startIndex;
        this.length = length;
        this.value = value;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getLength() {
        return length;
    }

    public int getValue() {
        return value;
    }
}

class SequenceWindow {
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

class SequentialPartialSumFinder {
    public List<SequenceResult> findSequencesWithSumThatExceedsLimit(int maximumSequenceLength, int limit, int[] sequence) {
        var window = new SequenceWindow(sequence, maximumSequenceLength);

        var results = new ArrayList<SequenceResult>();

        while (window.advance()) {

            var windowSum = window.getWindowSum();
            if (windowSum <= limit) {
                // Grenzwert im Bereich nicht überschritten
                continue;
            }

            // Grenzwert wurde überschritten, jetzt müssen wir also den kleinstmöglichen Bereich finden, für den das gilt
            // Dafür können wir einfach vom höchsten Index des aktuellen Bereichs nach unten iterieren, bis das Limit erreicht (oder die Fenstergröße) überschritten wurde
            var endIndex = window.getEndIndex();
            var limitExcessStart = endIndex;
            var sum = 0;
            for (int i = endIndex; i >= endIndex - maximumSequenceLength && i >= 0; i--) {
                limitExcessStart = i;
                sum += sequence[i];

                if (sum > limit) {
                    break;
                }
            }

            // gefundenen Sequenzbereich zum Ergebnis hinzufügen
            var resultLength = endIndex - limitExcessStart + 1;
            var resultSequence = new SequenceResult(limitExcessStart, resultLength, sum);
            results.add(resultSequence);

            // Fenster um Fenstergröße nach rechts schieben und Fenstergröße auf 0 zurücksetzen
            System.out.println(window.getStartIndex());
            window.moveWindow(window.getSize());
            window.resetSize();
        }

        return results;
    }
}
