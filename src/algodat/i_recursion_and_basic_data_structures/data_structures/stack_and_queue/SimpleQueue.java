package algodat.i_recursion_and_basic_data_structures.data_structures.stack_and_queue;

import java.lang.reflect.Array;

public class SimpleQueue<T> {
    private final T[] array;
    private final int maxSize;
    private int size;

    public SimpleQueue(int maxSize, Class<T> elementType) {
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

    public T peek() {
        if(this.size == 0) {
            throw new IllegalStateException("No elements in collection");
        }

        return this.array[this.size - 1];
    }

    public T remove() {
        if(this.size == 0) {
            throw new IllegalStateException("No elements in collection");
        }

        // erstes Element zwischenspeichern
        var result = this.array[0];
        // alle nachfolgenden Elemente um einen Index nach vorn verschieben
        for (int i = 0; i < this.size - 1; i++) {
            this.array[i] = this.array[i+1];
        }

        // ehemals letzten belegten Index leeren und Größe um 1 verringer
        this.array[this.size - 1] = null;
        this.size--;

        return result;
    }
}
