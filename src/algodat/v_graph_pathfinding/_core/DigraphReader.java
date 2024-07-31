package algodat.v_graph_pathfinding._core;

import java.io.*;
import java.util.ArrayList;

public class DigraphReader {
    public static DigraphData readDigraph(String file) throws Exception {
        try (var fileStream = new FileInputStream(file)) {
            return readDigraph(fileStream);
        }
    }

    public static DigraphData readDigraph(InputStream inputStream) throws Exception  {
        try(var reader = new BufferedReader(new InputStreamReader(inputStream))) {
            var nodeCount = readCount(reader);

            var nodes = new ArrayList<NodeData>(nodeCount);
            for (int i = 0; i < nodeCount; i++) {
                var node = readNode(reader);
                nodes.add(node);
            }

            var connectionCount = readCount(reader);

            var connections = new ArrayList<DirectedConnection>(connectionCount);
            for (int i = 0; i < connectionCount; i++) {
                var connection = readConnection(reader);
                connections.add(connection);
            }

            return new DigraphData(nodes, connections);
        }
    }

    private static int readCount(BufferedReader reader) throws IOException {
        var line = reader.readLine();
        return Integer.parseInt(line);
    }

    private static NodeData readNode(BufferedReader reader) throws IOException {
        var lineParts = reader.readLine().split("\\s+");

        var id = Integer.parseInt(lineParts[0]);
        var latitude = Double.parseDouble(lineParts[1]);
        var longitude = Double.parseDouble(lineParts[2]);

        var coordinates = new Coordinates(latitude, longitude);

        return new NodeData(id, coordinates);
    }

    private static DirectedConnection readConnection(BufferedReader reader) throws IOException {
        var lineParts = reader.readLine().split("\\s+");

        var fromId = Integer.parseInt(lineParts[0]);
        var toId = Integer.parseInt(lineParts[1]);
        var maxSpeed = Integer.parseInt(lineParts[2]);

        return new DirectedConnection(fromId, toId, maxSpeed);
    }
}

