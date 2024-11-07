package algodat.v_graph_pathfinding;

import algodat.v_graph_pathfinding._core.*;

import java.util.*;

public class PathfindingMain {
    public static void main(String[] args) throws Exception {
        var file = "./input-files/algodat/v-pathfinding/berlin.digraph";
        var digraphData = DigraphReader.readDigraph(file);

        // Graph-Objekt hier initialisieren
        var digraph = Digraph.from(digraphData);

        var startNodeId = 91207;
        var startNode = digraph.nodes.get(startNodeId);
        var targetNodeId = 339398;
        var targetNode = digraph.nodes.get(targetNodeId);

        // einen (kürzesten?) Pfad zwischen Start- und Zielknoten finden
        //var path = Pathfinding.getBreadthFirstSearchPath(startNode, targetNode);
        //var path = Pathfinding.getDijkstraPath(startNode, targetNode);
        //var path = Pathfinding.getAStarPath(startNode, targetNode, (node, target) -> node.coordinates.getDistanceInMeters(target.coordinates));
        var path = Pathfinding.getAStarPathIncludingSpeed(startNode, targetNode,
                (node, target) -> node.coordinates.getDistanceInMeters(target.coordinates) / 500.0);
        if(path.isEmpty()) {
            System.out.println("No path found");
            return;
        }


        // hier Liste der Knoten-IDs des Lösungswegs übergeben, in der Reihenfolge vom Start- bis zum Zielknoten
        var solutionPathNodeIds = path.get().stream().mapToInt(node -> node.id).boxed().toList();

        // schreibt Lösungsdatei für die Weboberfläche
        //var solutionFile = String.format("Pfad-von-%d-zu-%d-bfs.txt", startNodeId, targetNodeId);
        //var solutionFile = String.format("Pfad-von-%d-zu-%d-dijkstra.txt", startNodeId, targetNodeId);
        //var solutionFile = String.format("Pfad-von-%d-zu-%d-astar.txt", startNodeId, targetNodeId);
        var solutionFile = String.format("Pfad-von-%d-zu-%d-astar-with-speed.txt", startNodeId, targetNodeId);
        new SolutionWriter().writeSolution(solutionFile, solutionPathNodeIds);
    }
}

interface AStarHeuristic {
    double getHeuristicValue(DigraphNode node, DigraphNode target);
}

class Pathfinding {
    /**
     * Nutzt eine einfache Breitensuche (ohne Berücksichtigung von Entfernungen), um einen optimalen Pfad von Start- zu Zielknoten zu finden.
     * Optimal ist hier aufgrund der fehlenden Entfernungen die Anzahl besuchter Knoten im Pfad, der zurückgegebene Pfad besucht also so wenig Knoten wie möglich.
     * @param start
     * @param target
     * @return
     */
    public static Optional<List<DigraphNode>> getBreadthFirstSearchPath(DigraphNode start, DigraphNode target) {
        Queue<DigraphNode> queue = new ArrayDeque<>();
        queue.add(start);

        var nodePredecessors = new HashMap<DigraphNode, DigraphNode>();
        nodePredecessors.put(start, null); // Startknoten ist auf jeden Fall scohn bekannt und darf keinen Vorgänger haben
        var hasFoundPath = false;

        while(!queue.isEmpty()) {
            var node = queue.remove();

            if(node.equals(target)) {
                hasFoundPath = true;
                break;
            }

            for(var connection : node.outConnections) {
                var neighbor = connection.to();

                // Nachbar wurde schon gesehen = ist in Queue => kann übersprungen werden
                if(nodePredecessors.containsKey(neighbor)) {
                    continue;
                }

                nodePredecessors.put(neighbor, node);
                queue.add(neighbor);
            }
        }

        if(!hasFoundPath) {
            return Optional.empty();
        }

        var path = new ArrayList<DigraphNode>();
        var currentPathNode = target;
        while (currentPathNode != null) {
            path.addFirst(currentPathNode);
            currentPathNode = nodePredecessors.get(currentPathNode);
        }

        return Optional.of(path);
    }

    /**
     * Nutzt den Dijkstra-Algorithmus, um einen entfernungsoptimalen Pfad von Start- zu Zielknoten zu finden.
     * @param start
     * @param target
     * @return
     */
    public static Optional<List<DigraphNode>> getDijkstraPath(DigraphNode start, DigraphNode target) {
        var distanceToStartNode = new HashMap<DigraphNode, Double>();
        distanceToStartNode.put(start, 0.0);

        PriorityQueue<DigraphNode> queue = new PriorityQueue<>(Comparator.comparing(distanceToStartNode::get));
        queue.add(start);

        var visitedNodes = new HashSet<DigraphNode>();

        var nodePredecessors = new HashMap<DigraphNode, DigraphNode>();
        nodePredecessors.put(start, null); // Startknoten ist auf jeden Fall schon bekannt und darf keinen Vorgänger haben

        var hasFoundPath = false;

        while(!queue.isEmpty()) {
            var node = queue.remove();
            visitedNodes.add(node);

            var distanceToNode = distanceToStartNode.get(node);

            if(node.equals(target)) {
                hasFoundPath = true;
                break;
            }


            for(var connection : node.outConnections) {
                var neighbor = connection.to();

                // Nachbar überspringen, wenn bereits besucht
                if(visitedNodes.contains(neighbor)) {
                    continue;
                }

                // Weg über node zu neighbor ist länger als anderer bekannter Weg => ignorieren
                var newDistance = distanceToNode + connection.to().coordinates.getDistanceInMeters(connection.from().coordinates);
                if(distanceToStartNode.containsKey(neighbor) && newDistance >= distanceToStartNode.get(neighbor)) {
                    continue;
                }

                // falls kürzerer Weg möglich => Vorgänger und Prio/Entfernung anpassen
                distanceToStartNode.put(neighbor, newDistance);
                nodePredecessors.put(neighbor, node);

                // Prio/Position in Queue aktualisieren
                queue.remove(neighbor);
                queue.add(neighbor);
            }
        }

        if(!hasFoundPath) {
            return Optional.empty();
        }

        var path = new ArrayList<DigraphNode>();
        var currentPathNode = target;
        while (currentPathNode != null) {
            path.addFirst(currentPathNode);
            currentPathNode = nodePredecessors.get(currentPathNode);
        }

        return Optional.of(path);
    }

    /**
     * Nutzt den A*-Algorithmus, um einen entfernungsoptimalen Pfad von Start- zu Zielknoten zu finden.
     * Unterschied zum Dijkstra ist hier, dass die zuätzliche Heuristikfunktion eine bessere Wahl der Knoten ermöglicht
     * und somit im Vergleich zum Dijkstra eine kürzere Laufzeit und weniger Speicherauslastung bei gleichem Ergebnis erreicht wird.
     * Gibt die Heuristikfunktion den Wert 0 zurück, ist die Auswahl der Knoten identisch zum Dijkstra-Algorithmus.
     *
     * Wichtig: Die Heuristikfunktion muss eine Untergrenze des Zielwertes zurückgeben, ansonsten findet der Algorithmus kein optimales Ergebnis!
     * @param start
     * @param target
     * @return
     */
    public static Optional<List<DigraphNode>> getAStarPath(DigraphNode start, DigraphNode target, AStarHeuristic heuristic) {
        var distanceToStartNode = new HashMap<DigraphNode, Double>();
        distanceToStartNode.put(start, 0.0);

        var distanceToStartNodePlusHeuristic = new HashMap<DigraphNode, Double>();
        distanceToStartNodePlusHeuristic.put(start, 0.0);

        PriorityQueue<DigraphNode> queue = new PriorityQueue<>(Comparator.comparing(distanceToStartNodePlusHeuristic::get));
        queue.add(start);

        var visitedNodes = new HashSet<DigraphNode>();

        var nodePredecessors = new HashMap<DigraphNode, DigraphNode>();
        nodePredecessors.put(start, null); // Startknoten ist auf jeden Fall schon bekannt und darf keinen Vorgänger haben

        var hasFoundPath = false;

        while(!queue.isEmpty()) {
            var node = queue.remove();
            visitedNodes.add(node);

            var distanceToNode = distanceToStartNode.get(node);

            if(node.equals(target)) {
                hasFoundPath = true;
                break;
            }


            for(var connection : node.outConnections) {
                var neighbor = connection.to();

                // Nachbar überspringen, wenn bereits besucht
                if(visitedNodes.contains(neighbor)) {
                    continue;
                }

                // Weg über node zu neighbor ist länger als anderer bekannter Weg => ignorieren
                var newDistance = distanceToNode + connection.to().coordinates.getDistanceInMeters(connection.from().coordinates);
                if(distanceToStartNode.containsKey(neighbor) && newDistance >= distanceToStartNode.get(neighbor)) {
                    continue;
                }

                // falls kürzerer Weg möglich => Vorgänger und Prio/Entfernung anpassen
                distanceToStartNode.put(neighbor, newDistance);
                distanceToStartNodePlusHeuristic.put(neighbor, newDistance + heuristic.getHeuristicValue(neighbor, target));
                nodePredecessors.put(neighbor, node);

                // Prio/Position in Queue aktualisieren
                queue.remove(neighbor);
                queue.add(neighbor);
            }
        }

        if(!hasFoundPath) {
            return Optional.empty();
        }

        var path = new ArrayList<DigraphNode>();
        var currentPathNode = target;
        while (currentPathNode != null) {
            path.addFirst(currentPathNode);
            currentPathNode = nodePredecessors.get(currentPathNode);
        }

        return Optional.of(path);
    }

    /**
     * Nutzt den A*-Algorithmus, um einen zeitoptimalen Pfad von Start- zu Zielknoten zu finden.
     * Hier wird die Maximalgeschwindigkeit der Verbindungen zwischen den Knoten berücksichtigt, um einen Pfad zu finden, der eventuell
     * länger ist, aber in kürzerer Zeit zurückgelegt werden kann.
     * @param start
     * @param target
     * @return
     */
    public static Optional<List<DigraphNode>> getAStarPathIncludingSpeed(DigraphNode start, DigraphNode target, AStarHeuristic heuristic) {
        var timeToStartNode = new HashMap<DigraphNode, Double>();
        timeToStartNode.put(start, 0.0);

        var timeToStartNodePlusHeuristic = new HashMap<DigraphNode, Double>();
        timeToStartNodePlusHeuristic.put(start, 0.0);

        PriorityQueue<DigraphNode> queue = new PriorityQueue<>(Comparator.comparing(timeToStartNodePlusHeuristic::get));
        queue.add(start);

        var visitedNodes = new HashSet<DigraphNode>();

        var nodePredecessors = new HashMap<DigraphNode, DigraphNode>();
        nodePredecessors.put(start, null); // Startknoten ist auf jeden Fall schon bekannt und darf keinen Vorgänger haben

        var hasFoundPath = false;

        while(!queue.isEmpty()) {
            var node = queue.remove();
            visitedNodes.add(node);

            var timeToNode = timeToStartNode.get(node);

            if(node.equals(target)) {
                hasFoundPath = true;
                break;
            }


            for(var connection : node.outConnections) {
                var neighbor = connection.to();

                // Nachbar überspringen, wenn bereits besucht
                if(visitedNodes.contains(neighbor)) {
                    continue;
                }

                // Weg über node zu neighbor ist länger als anderer bekannter Weg => ignorieren
                var connectionTime = connection.to().coordinates.getDistanceInMeters(connection.from().coordinates)
                        / connection.maxSpeedKmh();
                var newTime = timeToNode + connectionTime;
                if(timeToStartNode.containsKey(neighbor) && newTime >= timeToStartNode.get(neighbor)) {
                    continue;
                }

                // falls kürzerer Weg möglich => Vorgänger und Prio/Entfernung anpassen
                timeToStartNode.put(neighbor, newTime);
                timeToStartNodePlusHeuristic.put(neighbor, newTime + heuristic.getHeuristicValue(neighbor, target));
                nodePredecessors.put(neighbor, node);

                // Prio/Position in Queue aktualisieren
                queue.remove(neighbor);
                queue.add(neighbor);
            }
        }

        if(!hasFoundPath) {
            return Optional.empty();
        }

        var path = new ArrayList<DigraphNode>();
        var currentPathNode = target;
        while (currentPathNode != null) {
            path.addFirst(currentPathNode);
            currentPathNode = nodePredecessors.get(currentPathNode);
        }

        return Optional.of(path);
    }
}

class Digraph {
    public final Map<Integer, DigraphNode> nodes = new HashMap<>();
    public final List<DigraphConnection> connections = new ArrayList<>();

    private Digraph() {}

    public static Digraph from(DigraphData data) {
        var digraph = new Digraph();

        for(var nodeData : data.nodes()) {
            var digraphNode = new DigraphNode(nodeData.coordinates(), nodeData.id());
            digraph.nodes.put(digraphNode.id, digraphNode);
        }

        for(var connectionData : data.connections()) {
            var fromNode = digraph.nodes.get(connectionData.fromNodeId());
            var toNode = digraph.nodes.get(connectionData.toNodeId());

            var digraphConnection = new DigraphConnection(fromNode, toNode, connectionData.maxSpeedKmh());
            digraph.connections.add(digraphConnection);
            fromNode.outConnections.add(digraphConnection);
        }

        return digraph;
    }
}

class DigraphNode {
    public final int id;
    public final Coordinates coordinates;
    public final List<DigraphConnection> outConnections = new ArrayList<>();

    public DigraphNode(Coordinates coordinates, int id) {
        this.coordinates = coordinates;
        this.id = id;
    }
}

record DigraphConnection(DigraphNode from, DigraphNode to, int maxSpeedKmh) {}