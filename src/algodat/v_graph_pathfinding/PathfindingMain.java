package algodat.v_graph_pathfinding;

import algodat.v_graph_pathfinding._core.DigraphReader;

public class PathfindingMain {
    public static void main(String[] args) throws Exception {
        var file = "<filepath here>";
        var digraphData = DigraphReader.readDigraph(file);
    }
}
