class Vertex {
    private OutDegree outDegrees;
    private OutDegree tail;
    private int id;
    Vertex previous;
    int distance, heapKey;
    public boolean isKnown;

    Vertex(int id) {
        this.id = id;
        distance = Integer.MAX_VALUE;
    }

    Vertex(int id, int distance) {
        this.id = id;
        this.distance = distance;
    }

    public void addEdge(Edge e) {
        if (outDegrees == null) {
            outDegrees = new OutDegree(e);
            tail = outDegrees;
        } else {
            tail.next = new OutDegree(e);
            tail = tail.next;
        }
    }

    public OutDegree getOutDegrees() {
        return outDegrees;
    }

    public int getID() {
        return id;
    }
}

class Edge {
    private Vertex left, right;
    private int weight;

    Edge(Vertex left, Vertex right, int weight) {
        this.left = left;
        this.right = right;
        this.weight = weight;
    }

    public Vertex getNeighbor(int vertexID) {
        if (right.getID() == vertexID)
            return left;
        else
            return right;
    }

    public int getWeight() {
        return weight;
    }
}

class OutDegree {
    OutDegree next;
    Edge edge;

    OutDegree(Edge edge) {
        this.edge = edge;
    }
}

/**
 * This priority queue is implemented using a min binary heap. Since every
 * graph starts out with a known vertex with minimum distance (the source)
 * and all other vertices are MAX_INT, buildHeap is not necessary. The heap
 * is built a vertex at a time as edges are added to the adjacency list.
 * The queue is constructed with a min (source) vertex and all other added
 * vertices are assumed to be MAX_INT.
 */
class MyPriorityQueue {
    private int elementCount, index = 1;
    private Vertex[] heap;

    MyPriorityQueue(int capacity, Vertex min) {
        elementCount = capacity;
        heap = new Vertex[capacity + 1];
        min.heapKey = index;
        heap[index++] = min;
    }

    public void addVertex(Vertex v) {
        v.heapKey = index;
        heap[index++] = v;
    }

    public Vertex deleteMin() {
        Vertex min = heap[1];
        heap[1] = heap[elementCount];
        heap[elementCount--] = null;
        percolateDown(1);
        return min;
    }

    private void percolateDown(int key) {
        int parent = key, child;
        boolean hasNext = true;

        while (hasNext) {
            child = getLesserChild(parent);

            if (child > elementCount)
                hasNext = false;
            else if (distanceOf(child) < distanceOf(parent))
                swap(child, parent);
            else
                hasNext = false;

            parent = child;
        }
    }

    private int getLesserChild(int parent) {
        int leftChild = 2 * parent;
        int rightChild = 2 * parent + 1;

        if (distanceOf(leftChild) < distanceOf(rightChild))
            return leftChild;

        return rightChild;
    }

    public void percolateUp(int key) {
        int child = key;
        int parent = key / 2;
        boolean hasNext = true;

        while (hasNext) {
            if (distanceOf(child) < distanceOf(parent)) {
                swap(child, parent);
                if (parent == 1)
                    hasNext = false;
            } else
                hasNext = false;

            child = parent;
            parent /= 2;
        }
    }

    private int distanceOf(int x) {
        if (x > elementCount)
            return Integer.MAX_VALUE;

        return heap[x].distance;
    }

    private void swap(int x, int y) {
        Vertex temp = heap[x];
        heap[x] = heap[y];
        heap[y] = temp;

        heap[x].heapKey = x;
        heap[y].heapKey = y;
    }

    public boolean isEmpty() {
        return elementCount == 0;
    }
}

class Graph {
    private Vertex[] vertices;
    private MyPriorityQueue pq;
    private int sourceVertex;

    Graph(int numOfVertices, int sourceVertex) {
        this.sourceVertex = sourceVertex;
        vertices = new Vertex[numOfVertices + 1]; // capacity + 1 so vertex id == index
        Vertex source = new Vertex(sourceVertex, 0);
        vertices[sourceVertex] = source;
        pq = new MyPriorityQueue(numOfVertices, source);
    }

    public void addEdge(int fromVertexID, int toVertexID, int weight) {
        Vertex fromV = getExistingOrNewVertex(toVertexID);
        Vertex toV = getExistingOrNewVertex(fromVertexID);
        Edge e = new Edge(fromV, toV, weight);

        addVertexAndEdge(fromV, e);
        addVertexAndEdge(toV, e);
    }

    private Vertex getExistingOrNewVertex(int v) {
        if (vertexExist(v))
            return vertices[v];
        else
            return new Vertex(v);
    }

    private void addVertexAndEdge(Vertex v, Edge e) {
        if (!vertexExist(v.getID())) {
            vertices[v.getID()] = v;
            pq.addVertex(v);
        }
        vertices[v.getID()].addEdge(e);
    }

    private boolean vertexExist(int v) {
        return vertices[v] != null;
    }

    public MyPriorityQueue getPriorityQueue() {
        return pq;
    }

    public Vertex getVertex(int v) {
        return vertices[v];
    }

    public void printDistances() {
        for (int v = 1; v < vertices.length; v++) {
            Vertex current = vertices[v].previous;
            StringBuilder sb = new StringBuilder();
            while (current != null) {
                sb.insert(0, current.getID() + " ");
                current = current.previous;
            }
            if (v == sourceVertex)
                sb.insert(0, v + " ");
            System.out.print(sb.toString() + v + " " + vertices[v].distance);
            System.out.println();
        }
    }
}

class PathFinder {
    Graph g;
    MyPriorityQueue pq;

    PathFinder(Graph g) {
        this.g = g;
        pq = g.getPriorityQueue();
    }

    /**
     * finds the shortest path using Dijkstra's algorithm
     */
    public void findShortestPaths() {
        while (!pq.isEmpty()) {
            Vertex min = pq.deleteMin();
            min.isKnown = true;
            OutDegree o = g.getVertex(min.getID()).getOutDegrees();
            while (o != null) {
                relax(min, o);
                o = o.next;
            }
        }
        g.printDistances();
    }

    private void relax(Vertex min, OutDegree o) {
        Vertex neighbor = o.edge.getNeighbor(min.getID());
        if (neighbor.isKnown)
            return;

        int currentDistance = min.distance + o.edge.getWeight();
        if (neighbor.distance > currentDistance) {
            neighbor.distance = currentDistance;
            pq.percolateUp(neighbor.heapKey);
            neighbor.previous = min;
        }
    }
}

public class WIBU4 {
}
