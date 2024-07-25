package algodat.ii_algorithms_for_realistic_problems.sequential_partial_sums;

import java.util.ArrayList;
import java.util.List;

public class SequentialPartialSumFinder {
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
