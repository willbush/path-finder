import java.util.Scanner;

class Vertex {
    private OutDegree outDegrees;
    private int id;
    Vertex previous;
    int distance, heapKey;
    boolean isKnown;

    Vertex(int id) {
        this.id = id;
        distance = Integer.MAX_VALUE;
    }

    Vertex(int id, int distance) {
        this.id = id;
        this.distance = distance;
    }

    public void addEdge(Edge e) {
        if (outDegrees == null)
            outDegrees = new OutDegree(e);
        else
            placeByID(e);
    }

    /*
    Inserting by ID from least to greatest instead of just inserting at the tail since
    Kruskal's spanning tree output requires sorted neighbors.
     */
    private void placeByID(Edge e) {
        OutDegree current = outDegrees;
        OutDegree previous = current;

        if (e.getNeighbor(id).getID() < current.edge.getNeighbor(id).getID())
            outDegrees = new OutDegree(e, current);
        else {
            while (idIsGreaterThanCurrent(e, current)) {
                previous = current;
                current = current.next;
            }
            previous.next = new OutDegree(e, current);
        }
    }

    private boolean idIsGreaterThanCurrent(Edge e, OutDegree current) {
        return current != null && e.getNeighbor(id).getID() > current.edge.getNeighbor(id).getID();
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
    boolean isMinimumSpanning;

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

    OutDegree(Edge e) {
        edge = e;
    }

    OutDegree(Edge e, OutDegree next) {
        edge = e;
        this.next = next;
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
    private final int minID, capacity;

    MyPriorityQueue(int capacity, Vertex min) {
        this.capacity = capacity;
        minID = min.getID();
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
        swap(1, elementCount--);
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
        boolean hasNext = (parent > 0);

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

    /*
    resets the heap to its original state after all items were added.
     */
    public void reset() {
        int minHeapKey = -1;
        elementCount = capacity;
        for (int v = 1; v < capacity + 1; v++) {
            if (heap[v] != null) {
                heap[v].isKnown = false;
                heap[v].previous = null;
                if (heap[v].getID() == minID) {
                    minHeapKey = heap[v].heapKey;
                    heap[v].distance = 0;
                } else
                    heap[v].distance = Integer.MAX_VALUE;
            }
        }
        swap(1, minHeapKey); // reset min vertex (i.e. the source) to top of the heap
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

    public void printSpanningTree() {
        for (int v = 1; v < vertices.length; v++) {
            OutDegree o = vertices[v].getOutDegrees();

            while (o != null) {
                Vertex neighbor = o.edge.getNeighbor(v);
                if (o.edge.isMinimumSpanning) {
                    System.out.println(v + " " + neighbor.getID());
                    o.edge.isMinimumSpanning = false;
                }
                o = o.next;
            }
        }
    }
}

class PathFinder {
    private Graph g;
    private MyPriorityQueue pq;

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

class TreeSpanner {
    private Graph g;
    private MyPriorityQueue pq;

    TreeSpanner(Graph g) {
        this.g = g;
        pq = g.getPriorityQueue();
    }

    public void findMinimumSpanningTree() {
        while (!pq.isEmpty()) {
            Vertex min = pq.deleteMin();
            min.isKnown = true;
            OutDegree o = g.getVertex(min.getID()).getOutDegrees();
            while (o != null) {
                relaxDistance(min, o);
                o = o.next;
            }
        }
        g.printSpanningTree();
    }

    private void relaxDistance(Vertex min, OutDegree o) {
        Vertex neighbor = o.edge.getNeighbor(min.getID());
        if (neighbor.isKnown) {
            if (neighborSharesSpanningEdge(min, neighbor))
                o.edge.isMinimumSpanning = true;

            return;
        }

        int weight = o.edge.getWeight();
        if (weight < neighbor.distance) {
            neighbor.distance = weight;
            pq.percolateUp(neighbor.heapKey);
            neighbor.previous = min;
        }
    }

    private boolean neighborSharesSpanningEdge(Vertex min, Vertex neighbor) {
        return min.previous != null && min.previous.getID() == neighbor.getID();
    }
}

public class WIBUP4 {
    private Scanner input;
    private Graph g;

    public WIBUP4(java.io.InputStream in) {
        input = new Scanner(in);
    }

    public void run() {
        initializeWithFirstLine();
        String[] tokens;
        tokens = input.nextLine().split(" ");

        while (inputHasNext(tokens)) {
            processEdges(tokens);
            tokens = input.nextLine().split(" ");
        }
        runPathFinder();
    }

    private void initializeWithFirstLine() {
        String[] tokens = input.nextLine().split(" ");
        int numOfVertices = Integer.valueOf(tokens[0]);
        int sourceVertex = Integer.valueOf(tokens[1]);

        g = new Graph(numOfVertices, sourceVertex);
    }

    /*
    Input has next until input is terminated with 3 zeros (0 0 0).
     */
    private boolean inputHasNext(String[] tokens) {
        for (String s : tokens)
            if (!s.equals("0"))
                return true;

        return false;
    }

    private void processEdges(String[] tokens) {
        int fromVertexID = Integer.valueOf(tokens[0]);
        int toVertexID = Integer.valueOf(tokens[1]);
        int weight = Integer.valueOf(tokens[2]);

        g.addEdge(fromVertexID, toVertexID, weight);
    }

    private void runPathFinder() {
        PathFinder pf = new PathFinder(g);
        TreeSpanner ts = new TreeSpanner(g);
        pf.findShortestPaths();
        g.getPriorityQueue().reset();
        System.out.println();
        ts.findMinimumSpanningTree();
    }

    public static void main(String[] args) {
        WIBUP4 program = new WIBUP4(System.in);
        program.run();
    }
}
