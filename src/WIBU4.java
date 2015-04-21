class Vertex {
    int distance;
    int id;

    Vertex(int id) {
        this.id = id;
        distance = Integer.MAX_VALUE;
    }
}

class Edge {
    int vertexA, vertexB, weight;
    Vertex neighbor;

    Edge(int vertexA, int vertexB, int weight) {
        this.vertexA = vertexA;
        this.vertexB = vertexB;
        this.weight = weight;
        neighbor = new Vertex(vertexB);
    }
}

class OutDegree {
    OutDegree next;
    Edge edge;

    OutDegree(Edge edge) {
        this.edge = edge;
        next = null;
    }

    OutDegree(Edge edge, OutDegree next) {
        this.edge = edge;
        this.next = next;
    }
}

class Graph {
    // index + 1 represents the vertex number, which has an out degree for every edge
    // it has connected to another vertex.
    private OutDegree[] adjacencyList;

    Graph(int maxNodes, int sourceVertex) {
        adjacencyList = new OutDegree[maxNodes];
        Edge circularEdge = new Edge(sourceVertex, sourceVertex, 0);
        adjacencyList[sourceVertex - 1] = new OutDegree(circularEdge);
    }

    public void addEdge(Edge e) {
        placeEdge(e.vertexA - 1, e);
        placeEdge(e.vertexB - 1, e);
    }

    private void placeEdge(int vertex, Edge e) {
        if (!hasNeighbors(vertex))
            adjacencyList[vertex] = new OutDegree(e);
        else
            placeByWeight(vertex, e);
    }

    private boolean hasNeighbors(int vertex) {
        return adjacencyList[vertex] != null;
    }

    private void placeByWeight(int vertex, Edge e) {
        OutDegree current = adjacencyList[vertex];
        OutDegree previous = current;
        if (e.weight < current.edge.weight)
            adjacencyList[vertex] = new OutDegree(e, current);
        else {
            while (edgeWeightIsGreaterThanCurrent(e, current)) {
                previous = current;
                current = current.next;
            }
            previous.next = new OutDegree(e, current);
        }
    }

    private boolean edgeWeightIsGreaterThanCurrent(Edge e, OutDegree current) {
        return current != null && e.weight > current.edge.weight;
    }

    public OutDegree getNeighbors(int vertex) {
        return adjacencyList[vertex - 1];
    }
}

class PathFinder {
    Graph graph;

    PathFinder(Graph g) {
        graph = g;
    }

    public void printShortestPaths() {
        System.out.println("1 1 0");
    }
}

public class WIBU4 {
}
