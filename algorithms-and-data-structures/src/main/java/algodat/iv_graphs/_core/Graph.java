package algodat.iv_graphs._core;

public class Graph {
    private final Node[] nodes;

    public Graph(int nodeCount) {
        this.nodes = new Node[nodeCount];

        for (int i = 0; i < nodeCount; i++) {
            this.nodes[i] = new Node(i);
        }
    }

    public Node getNode(int id) {
        if(id < 0 || id > nodes.length) {
            throw new RuntimeException();
        }

        return this.nodes[id];
    }
}
