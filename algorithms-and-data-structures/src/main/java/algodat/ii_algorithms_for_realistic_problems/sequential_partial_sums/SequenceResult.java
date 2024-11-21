package algodat.ii_algorithms_for_realistic_problems.sequential_partial_sums;

public class SequenceResult {
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
