package algodat.i_recursion_and_basic_data_structures.data_structures.array_list;

import java.lang.reflect.Array;

public class SimpleArrayList<T> {
    private final T[] array;
    private final int maxSize;
    private int size;

    public SimpleArrayList(int maxSize, Class<T> elementType) {
        this.maxSize = maxSize;

        // Array.newInstance wird hier für die Erstellung eines Arrays mit generischem Typen verwendet.
        // Alternativ könnte man auch ein Object-Array verwenden und beim Zugriff die Elemente in den Datentypen T casten.
        this.array = (T[]) Array.newInstance(elementType, maxSize);
    }

    public int size() {
        return this.size;
    }

    public void add(T element) {
        if (size == maxSize) {
            throw new IllegalStateException("Max size reached");
        }

        this.array[this.size] = element;
        this.size++;
    }

    public T get(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Index must be positive or zero.");
        }

        if (index >= this.size) {
            throw new IllegalArgumentException("Index out of range");
        }

        return this.array[index];
    }

    public void removeFirst() {
        this.remove(0);
    }

    public void removeLast() {
        this.remove(this.size - 1);
    }

    public void remove(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Index must be positive or zero.");
        }

        if (index >= this.size) {
            throw new IllegalArgumentException("Index out of range");
        }

        // das Element am Index wird entfernt, indem wir die nachfolgenden Elemente
        // um einen Index nach unten verschieben
        for (int i = index; i < this.size - 1; i++) {
            this.array[i] = this.array[i + 1];
        }

        // das ursprünglich letzte Element steht dann nun an der vorletzten Stelle
        // und die letzte Stelle kann geleert werden
        this.array[this.size - 1] = null;

        // zuletzt muss die Größe um 1 reduziert werden
        this.size--;
    }
}
