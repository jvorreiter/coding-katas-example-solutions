package algodat.v_graph_pathfinding._core;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SolutionWriter {
    /**
     * Schreibt eine Lösungsdatei mit dem gefundenen Pfad zwischen den zwei Knoten, damit diese auf der Weboberfläce für die Visualisierung verwendet werden kann.
     * @param fileName Pfad der Zieldatei
     * @param nodeIdsInPath Knoten-IDs der besuchten Knoten in der Reihenfolgen vom Start bis zum Zielknoten. MUSS DIE IDs VOM START- UND ZIELKNOTEN EBENFALLS ENTHALTEN!
     */
    public void writeSolution(String fileName, List<Integer> nodeIdsInPath) throws IOException {
        var startNodeId = nodeIdsInPath.getFirst();
        var targetNodeId = nodeIdsInPath.getLast();

        try(var writer = new FileWriter(fileName, StandardCharsets.UTF_8)) {
            writer.write(String.format("%d->%d%n", startNodeId, targetNodeId));

            var pathList = String.join(" ", nodeIdsInPath.stream().map(id -> id.toString()).toList());
            writer.write(pathList + '\n');
        }
    }
}
