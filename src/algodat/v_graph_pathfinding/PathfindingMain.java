package algodat.v_graph_pathfinding;

import algodat.v_graph_pathfinding._core.DigraphReader;
import algodat.v_graph_pathfinding._core.SolutionWriter;

import java.util.ArrayList;

public class PathfindingMain {
    public static void main(String[] args) throws Exception {
        var file = "<.digraph-Dateipfad hier>";
        var digraphData = DigraphReader.readDigraph(file);

        //TODO: Graph-Objekt hier initialisieren

        var startNodeId = -1;
        var targetNodeId = -1;

        //TODO: einen (kürzesten?) Pfad zwischen Start- und Zielknoten finden


        //TODO: hier Liste der Knoten-IDs des Lösungswegs übergeben, in der Reihenfolge vom Start- bis zum Zielknoten
        var solutionPathNodeIds = new ArrayList<Integer>();

        // schreibt Lösungsdatei für die Weboberfläche
        var solutionFile = String.format("Pfad-von-%d-zu-%d.txt", startNodeId, targetNodeId);
        new SolutionWriter().writeSolution(solutionFile, solutionPathNodeIds);
    }
}
