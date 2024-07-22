package algodat.iv_graphs._core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GraphStdinInitializer {
    /**
     * Initialisiert einen Graphen aus dem Standardinput (meist Konsole).<br>
     * Erwartet Eingabe in folgendem Format:<br>
     * 1. Zeile: Anzahl Knoten<br>
     * danach beliebig viele Zeilen: 2 Knoten-IDs (0-indiziert), die über eine Kante verbunden sind<br>
     * <br>
     * Beispiel Dreiecksgraph:<br>
     * 3<br>
     * 0 1<br>
     * 1 2<br>
     * 2 0<br>
     * @return den initialisierten algodat.iv_graphs._core.Graph
     */
    public static Graph fromStdin() {
        try {
            var reader = new BufferedReader(new InputStreamReader(System.in));

            int nodeCount = readNodeCount(reader);
            var graph = new Graph(nodeCount);

            int edgeCount = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("exit")) {
                    break;
                }

                var ids = line.split("\\s+");
                if (ids.length != 2) {
                    throw new RuntimeException("Invalid graph input, expected one connection per line");
                }

                var id0 = Integer.parseInt(ids[0]);
                var id1 = Integer.parseInt(ids[1]);

                var node0 = graph.getNode(id0);
                var node1 = graph.getNode(id1);
                var added = node0.addNeighbor(node1);

                if (added) {
                    edgeCount++;
                }
            }

            System.out.printf("Initialized graph with %s nodes and %s edges%n", nodeCount, edgeCount);

            return graph;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int readNodeCount(BufferedReader reader) throws IOException {
        var line = reader.readLine();
        return Integer.parseInt(line);
    }
}
