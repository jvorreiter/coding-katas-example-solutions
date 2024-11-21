package algodat.i_recursion_and_basic_data_structures.data_structures.doubly_linked_list;

public class DoublyLinkedListNode<T> {
    private final T value;

    private DoublyLinkedListNode<T> previous;
    private DoublyLinkedListNode<T> next;

    public DoublyLinkedListNode(T value) {
        this.value = value;
    }

    public T getValue() {
        return this.value;
    }

    public DoublyLinkedListNode<T> getPrevious() {
        return previous;
    }

    public void setPrevious(DoublyLinkedListNode<T> previous) {
        this.previous = previous;
    }

    public DoublyLinkedListNode<T> getNext() {
        return next;
    }

    public void setNext(DoublyLinkedListNode<T> next) {
        this.next = next;
    }
}
