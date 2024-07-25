package algodat.i_recursion_and_basic_data_structures.data_structures.stack_and_queue;

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


