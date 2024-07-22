package algodat.iv_graphs._core;

import java.util.HashSet;
import java.util.List;

public class Node {
    private final int id;
    private final HashSet<Node> neighbors = new HashSet<>();

    public Node(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public boolean addNeighbor(Node node) {
        if(node == null) {
            throw new RuntimeException();
        }

        if(this.equals(node)) {
            return false;
        }

        this.neighbors.add(node);
        return node.neighbors.add(this);
    }

    public List<Node> getNeighbors() {
        return neighbors.stream().toList();
    }
}

