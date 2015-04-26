class Vertex {
    int distance;
    int id;

    Vertex(int id) {
        this.id = id;
        distance = Integer.MAX_VALUE;
    }

    Vertex(int id, int distance) {
        this.id = id;
        this.distance = distance;
    }
}

class Edge {
    Vertex left, right;
    int weight;


    Edge(Vertex left, Vertex right, int weight) {
        this.left = left;
        this.right = right;
        this.weight = weight;
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

class MyPriorityQueue {
    private int index = 1;
    private Vertex[] heap;

    MyPriorityQueue(int capacity, Vertex min) {
        heap = new Vertex[capacity + 1];
        heap[index++] = min;
    }

    public void addVertex(Vertex v) {
        heap[index++] = v;
    }

    public void print() {
        for (Vertex v : heap)
            if (v != null)
                System.out.println(v.distance);
    }
}

class Graph {
    // index + 1 represents the vertex number, which has an out degree for every edge
    // it has connected to another vertex.
    private OutDegree[] adjacencyList;
    private Vertex[] vertices;
    private MyPriorityQueue pq;

    Graph(int numOfVertices, int sourceVertex) {
        adjacencyList = new OutDegree[numOfVertices];
        vertices = new Vertex[numOfVertices];

        Vertex source = new Vertex(sourceVertex, 0);
        vertices[source.id - 1] = source;
        pq = new MyPriorityQueue(numOfVertices, source);
    }

    public void addEdge(int fromVertex, int toVertex, int weight) {
        Vertex fromV = new Vertex(fromVertex);
        Vertex toV = new Vertex(toVertex);
        addVertex(fromV);
        addVertex(toV);

        Edge e = new Edge(fromV, toV, weight);
        setEdgeAdjacentTo(fromVertex, e);
        setEdgeAdjacentTo(toVertex, e);
    }

    private void addVertex(Vertex v) {
        if (vertices[v.id - 1] == null) {
            vertices[v.id - 1] = v;
            pq.addVertex(v);
        }
    }

    private void setEdgeAdjacentTo(int vertexID, Edge e) {
        if (!hasNeighbors(vertexID))
            adjacencyList[vertexID - 1] = new OutDegree(e);
        else
            placeByWeight(vertexID, e);
    }

    private boolean hasNeighbors(int vertexID) {
        return adjacencyList[vertexID - 1] != null;
    }

    private void placeByWeight(int vertexID, Edge e) {
        OutDegree current = adjacencyList[vertexID - 1];
        OutDegree previous = current;

        if (e.weight < current.edge.weight)
            adjacencyList[vertexID - 1] = new OutDegree(e, current);
        else {
            while (edgeWeightIsGreater(e, current)) {
                previous = current;
                current = current.next;
            }
            previous.next = new OutDegree(e, current);
        }
    }

    private boolean edgeWeightIsGreater(Edge e, OutDegree current) {
        return current != null && e.weight > current.edge.weight;
    }

    public OutDegree getNeighbors(int vertex) {
        return adjacencyList[vertex - 1];
    }

    public MyPriorityQueue getPriorityQueue() {
        return pq;
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
