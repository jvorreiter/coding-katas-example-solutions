package algodat.i_recursion_and_basic_data_structures.data_structures.doubly_linked_list;

public class SimpleDoublyLinkedList<T> {
    private DoublyLinkedListNode<T> head;
    private DoublyLinkedListNode<T> tail;

    public int size() {
        // kein Element in der Kette
        if (this.head == null) {
            return 0;
        }

        // vom ersten Element aus die Kette lang gehen und mitzählen,
        // wie viele Elemente besucht werden, bis das letzte Element (ohne Nachfolger) gefunden wurde
        var size = 0;
        var node = this.head;
        while (node != null) {
            size++;
            node = node.getNext();
        }

        return size;
    }

    public void add(T element) {
        var node = new DoublyLinkedListNode<>(element);

        // es wird das erste Element hinzugefügt: Head und Tail sind das einzige Element
        if (this.head == null) {
            this.head = node;
            this.tail = node;
        } else {
            // ansonsten wird das neue Element an den alten Tail angehangen und ist danach neuer Tail
            this.tail.setNext(node);
            node.setPrevious(this.tail);

            this.tail = node;
        }
    }

    private DoublyLinkedListNode<T> getNode(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Index must be positive or zero.");
        }

        if (this.head == null) {
            throw new IllegalArgumentException("Index out of range");
        }

        var node = this.head;
        for (int i = 0; i < index; i++) {
            node = node.getNext();

            if (node == null) {
                throw new IllegalArgumentException("Index out of range");
            }
        }

        return node;
    }

    public T get(int index) {
        return this.getNode(index).getValue();
    }

    public void removeFirst() {
        this.remove(0);
    }

    public void removeLast() {
        if (this.tail == null) {
            return;
        }

        if(this.tail == this.head) {
            this.tail = null;
            this.head = null;
            return;
        }

        var newTail = this.tail.getPrevious();
        this.tail = newTail;

        if(newTail != null) {
            newTail.setNext(null);
        }
    }

    public void remove(int index) {
        var node = this.getNode(index);

        // Beispiel: A <-> G <-> Z
        // Wird G aus der Kette gelöscht, so muss der Vorgänger von G auf den Nachfolger von G zeigen und andersherum.
        // Hier kann G aber keine Vorgänger oder Nachfolger haben, dann müssen Head/Tail der Liste aktualisieren.
        if(node.getPrevious() != null) {
            node.getPrevious().setNext(node.getNext());
        } else {
            this.head = node.getNext();
        }

        if(node.getNext() != null) {
            node.getNext().setPrevious(node.getPrevious());
        } else {
            this.tail = node.getPrevious();
        }
    }
}
