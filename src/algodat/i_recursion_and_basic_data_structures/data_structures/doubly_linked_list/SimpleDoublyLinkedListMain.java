package algodat.i_recursion_and_basic_data_structures.data_structures.doubly_linked_list;

public class SimpleDoublyLinkedListMain {
    public static void main(String[] args) {
        var linkedList = new SimpleDoublyLinkedList<Integer>();

        System.out.println("--- DOPPELT VERKETTETE LISTE ---");
        System.out.println("Anzahl Elemente in der Liste: " + linkedList.size());
        linkedList.add(0);
        System.out.println("Anzahl Elemente in der Liste: " + linkedList.size());
        System.out.println("Element am Index 0: " + linkedList.get(0));
        linkedList.add(100);
        System.out.println("Anzahl Elemente in der Liste: " + linkedList.size());
        System.out.println("Element am Index 1: " + linkedList.get(1));
        linkedList.add(1000);
        System.out.println("Anzahl Elemente in der Liste: " + linkedList.size());
        System.out.println("Element am Index 2: " + linkedList.get(2));
        linkedList.remove(1);
        System.out.println("Anzahl Elemente in der Liste: " + linkedList.size());
        System.out.println("Element am Index 1: " + linkedList.get(1));
        linkedList.removeFirst();
        System.out.println("Anzahl Elemente in der Liste: " + linkedList.size());
        System.out.println("Element am Index 0: " + linkedList.get(0));
        linkedList.removeLast();
        System.out.println("Anzahl Elemente in der Liste: " + linkedList.size());
    }
}

