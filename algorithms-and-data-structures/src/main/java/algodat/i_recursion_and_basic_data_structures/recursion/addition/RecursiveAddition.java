package algodat.i_recursion_and_basic_data_structures.recursion.addition;

public class RecursiveAddition {

    public static int Add(int a, int b) {
        // Abbruchbedingung: wenn beide Variablen 0 sind, ist die Summe 0
        if (a == 0 && b == 0) {
            return 0;
        }

        // ist a = 0, so addieren wir rekursiv 1 bis b "aufgebraucht" ist
        if (a == 0) {
            return 1 + Add(0, b - 1);
        }

        // ist a > 0, so addieren wir rekursiv 1 bis a "aufgebraucht" ist
        return 1 + Add(a - 1, b);
    }
}
