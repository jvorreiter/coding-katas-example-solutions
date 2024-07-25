package algodat.i_recursion_and_basic_data_structures.data_structures.array_list;

import java.lang.reflect.Array;

public class SimpleArrayListMain {
    public static void main(String[] args) {
        var arrayList = new SimpleArrayList<>(100, Integer.class);

        System.out.println("--- ARRAY-LISTE ---");
        System.out.println("Anzahl Elemente in der Liste: " + arrayList.size());
        arrayList.add(0);
        System.out.println("Anzahl Elemente in der Liste: " + arrayList.size());
        System.out.println("Element am Index 0: " + arrayList.get(0));
        arrayList.add(100);
        System.out.println("Anzahl Elemente in der Liste: " + arrayList.size());
        System.out.println("Element am Index 1: " + arrayList.get(1));
        arrayList.add(1000);
        System.out.println("Anzahl Elemente in der Liste: " + arrayList.size());
        System.out.println("Element am Index 2: " + arrayList.get(2));
        arrayList.remove(1);
        System.out.println("Anzahl Elemente in der Liste: " + arrayList.size());
        System.out.println("Element am Index 1: " + arrayList.get(1));
        arrayList.removeFirst();
        System.out.println("Anzahl Elemente in der Liste: " + arrayList.size());
        System.out.println("Element am Index 0: " + arrayList.get(0));
        arrayList.removeLast();
        System.out.println("Anzahl Elemente in der Liste: " + arrayList.size());
    }
}

