class Vertex {
    private OutDegree outDegrees;
    private OutDegree tail;
    Vertex previous;
    int id, distance, heapKey;

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
}

class Edge {
    private Vertex left, right;
    int weight;

    Edge(Vertex left, Vertex right, int weight) {
        this.left = left;
        this.right = right;
        this.weight = weight;
    }

    public Vertex getNeighbor(int vertexID) {
        if (right.id == vertexID)
            return left;
        else
            return right;
    }
}

class OutDegree {
    OutDegree next;
    Edge edge;

    OutDegree(Edge edge) {
        this.edge = edge;
    }
}

class MyPriorityQueue {
    private int elementCount, index = 1;
    private Vertex[] heap;

    MyPriorityQueue(int capacity, Vertex min) {
        elementCount = capacity;
        heap = new Vertex[capacity + 1];
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
    }

    public boolean isEmpty() {
        return elementCount == 0;
    }

    public void print() {
        for (Vertex v : heap)
            if (v != null)
                System.out.println(v.distance);
    }
}

class Graph {
    private Vertex[] vertices;
    private MyPriorityQueue pq;

    Graph(int numOfVertices, int sourceVertex) {
        vertices = new Vertex[numOfVertices + 1]; // capacity + 1 so vertex id == index
        Vertex source = new Vertex(sourceVertex, 0);
        vertices[sourceVertex] = source;
        pq = new MyPriorityQueue(numOfVertices, source);
    }

    public void addEdge(int fromVertexID, int toVertexID, int weight) {
        Vertex fromV = new Vertex(fromVertexID);
        Vertex toV = new Vertex(toVertexID);
        Edge e = new Edge(fromV, toV, weight);

        addVertexAndEdge(fromV, e);
        addVertexAndEdge(toV, e);
    }

    private void addVertexAndEdge(Vertex v, Edge e) {
        if (vertices[v.id] == null) {
            vertices[v.id] = v;
            pq.addVertex(v);
        }
        vertices[v.id].addEdge(e);
    }

    public MyPriorityQueue getPriorityQueue() {
        return pq;
    }

    public Vertex getVertex(int v) {
        return vertices[v];
    }
}

class PathFinder {
    Graph g;
    MyPriorityQueue pq;

    PathFinder(Graph g) {
        this.g = g;
        pq = g.getPriorityQueue();
        findShortestPaths();
    }

    private void findShortestPaths() {
        while (!pq.isEmpty()) {
            Vertex min = pq.deleteMin();
            OutDegree o = g.getVertex(min.id).getOutDegrees();
            while (o != null) {
                relax(min, o);
                o = o.next;
            }
        }
    }

    private void relax(Vertex min, OutDegree o) {
        Vertex neighbor = o.edge.getNeighbor(min.id);
        int currentDistance = min.distance + o.edge.weight;

        if (neighbor.distance > currentDistance) {
            neighbor.distance = currentDistance;
            pq.percolateUp(neighbor.heapKey);
            neighbor.previous = min;
        }
    }
}

public class WIBU4 {
}
