package algodat.ii_algorithms_for_realistic_problems.sequential_partial_sums;

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

