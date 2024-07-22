package algodat.i_recursion_and_basic_data_strucutures;

import java.lang.reflect.Array;

public class StackAndQueueMain {
    public static void main(String[] args) {
        {
            var stack = new SimpleStack<>(100, Integer.class);

            System.out.println("--- STACK ---");
            System.out.println("Anzahl Elemente im Stack: " + stack.size());
            stack.push(0);
            System.out.println("Anzahl Elemente im Stack: " + stack.size());
            System.out.println("Oberestes Element im Stack: " + stack.peek());
            stack.push(1);
            System.out.println("Anzahl Elemente im Stack: " + stack.size());
            System.out.println("Oberestes Element im Stack: " + stack.peek());
            stack.push(2);
            System.out.println("Anzahl Elemente im Stack: " + stack.size());
            System.out.println("Oberestes Element im Stack: " + stack.peek());

            while (stack.size() > 0) {
                System.out.println("Zuletzt entferntes Element: " + stack.pop());
            }
            System.out.println("Anzahl Elemente im Stack: " + stack.size());
        }

        {
            var queue = new SimpleQueue<>(100, Integer.class);
            System.out.println("--- QUEUE ---");
            System.out.println("Anzahl Elemente in der Queue: " + queue.size());
            queue.add(0);
            System.out.println("Anzahl Elemente in der Queue: " + queue.size());
            System.out.println("Oberestes Element in der Queue: " + queue.peek());
            queue.add(1);
            System.out.println("Anzahl Elemente in der Queue: " + queue.size());
            System.out.println("Oberestes Element in der Queue: " + queue.peek());
            queue.add(2);
            System.out.println("Anzahl Elemente in der Queue: " + queue.size());
            System.out.println("Oberestes Element in der Queue: " + queue.peek());

            while (queue.size() > 0) {
                System.out.println("Zuletzt entferntes Element: " + queue.remove());
            }
            System.out.println("Anzahl Elemente in der Queue: " + queue.size());
        }
    }
}


class SimpleStack<T> {
    private final T[] array;
    private final int maxSize;
    private int size;

    public SimpleStack(int maxSize, Class<T> elementType) {
        this.maxSize = maxSize;

        // Array.newInstance wird hier für die Erstellung eines Arrays mit generischem Typen verwendet.
        // Alternativ könnte man auch ein Object-Array verwenden und beim Zugriff die Elemente in den Datentypen T casten.
        this.array = (T[]) Array.newInstance(elementType, maxSize);
    }

    public int size() {
        return this.size;
    }

    public void push(T element) {
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

    public T pop() {
        if(this.size == 0) {
            throw new IllegalStateException("No elements in collection");
        }

        // letztes Element aus dem Array entfernen, Größe um 1 verringern, und ehemals letztes Element zurückgeben
        var result = this.array[this.size - 1];
        this.array[this.size - 1] = null;
        this.size--;

        return result;
    }
}


class SimpleQueue<T> {
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