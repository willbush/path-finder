import java.util.Scanner;

class Vertex {
    private OutDegree outDegrees; // a singly linked list of outDegrees for the vertex
    private final int id; // vertices are identified by the set of natural numbers.
    Vertex previous; // previous vertex Dijkstra has visited
    int distance; // distance from the source vertex (sum of edge weights between this and source).
    int heapIndex; // heapIndex is the current location of the vertex in the heap.
    boolean isKnown; // used to flag that Dijkstra has added it to the known set.

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
    the Kruskal output requires sorted neighbors.
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

/**
 * Each vertex has some number of outDegrees. Each out degree has an edge which connects
 * the two vertices. The out degree abstraction allows vertices to be stored in the
 * graph as an adjacency list.
 */
class OutDegree {
    OutDegree next; // next outDegree of the vertex
    Edge edge;

    OutDegree(Edge e) {
        edge = e;
    }

    OutDegree(Edge e, OutDegree next) {
        edge = e;
        this.next = next;
    }
}

class Edge {
    private final Vertex left, right;
    private final int weight;
    Edge next; // next sorted (by weight) edge
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

    public int getLeftID() {
        return left.getID();
    }

    public int getRightID() {
        return right.getID();
    }
}

/**
 * This priority queue is implemented using a binary min heap. Since every
 * graph starts out with a source vertex with minimum distance and all other
 * vertices are MAX_INT, buildHeap is not necessary. The heap
 * is built a vertex at a time as edges are added to the adjacency list.
 * The queue is constructed with a min (source) vertex and all other added
 * vertices must be MAX_INT.
 */
class MyPriorityQueue {
    private int elementCount, index = 1;
    private Vertex[] heap;

    MyPriorityQueue(int capacity, Vertex min) {
        elementCount = capacity;
        heap = new Vertex[capacity + 1];

        min.heapIndex = index;
        heap[index++] = min;
    }

    public void addVertex(Vertex v) {
        if (v.distance != Integer.MAX_VALUE)
            throw new IllegalArgumentException("Added vertex must have Integer.MAX_VALUE for distance");

        v.heapIndex = index;
        heap[index++] = v;
    }

    public Vertex deleteMin() {
        Vertex min = heap[1];
        swap(1, elementCount--);
        percolateDown(1);
        return min;
    }

    private void percolateDown(int index) {
        int parent = index, child;
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

    public void percolateUp(int index) {
        int child = index;
        int parent = index / 2;
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

        heap[x].heapIndex = x;
        heap[y].heapIndex = y;
    }

    public boolean isEmpty() {
        return elementCount == 0;
    }
}

class Graph {
    private final Vertex[] vertices;
    private final MyPriorityQueue pq;
    private final int sourceVertex;
    private final int capacity;
    private Edge sortedEdges; // singly linked list of sorted edges for Kruskal's MST algorithm.

    Graph(int numOfVertices, int sourceVertex) {
        capacity = numOfVertices;
        this.sourceVertex = sourceVertex;
        vertices = new Vertex[numOfVertices + 1]; // capacity + 1 so vertex id == index

        Vertex source = new Vertex(sourceVertex, 0);
        vertices[sourceVertex] = source;
        pq = new MyPriorityQueue(numOfVertices, source);
    }

    /*
    addEdge is used to build the graph and the heap at the same time.
     */
    public void addEdge(int fromVertexID, int toVertexID, int weight) {
        Vertex fromV = getExistingOrNewVertex(toVertexID);
        Vertex toV = getExistingOrNewVertex(fromVertexID);
        Edge e = new Edge(fromV, toV, weight);

        addToSortedEdgeList(e);
        addVertexAndEdge(fromV, e);
        addVertexAndEdge(toV, e);
    }

    private Vertex getExistingOrNewVertex(int v) {
        if (vertexExist(v))
            return vertices[v];
        else
            return new Vertex(v);
    }

    private void addToSortedEdgeList(Edge e) {
        if (sortedEdges == null)
            sortedEdges = e;
        else
            placeByWeight(e);
    }

    private void placeByWeight(Edge e) {
        Edge current = sortedEdges;
        Edge previous = current;

        if (e.getWeight() <= sortedEdges.getWeight()) {
            e.next = sortedEdges;
            sortedEdges = e;
        } else {
            while (current != null && e.getWeight() > current.getWeight()) {
                previous = current;
                current = current.next;
            }
            e.next = current;
            previous.next = e;
        }
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
            if (v == sourceVertex)
                System.out.print(vertices[v].getID() + " ");

            recurseToSourceAndPrint(vertices[v]);
            System.out.println(vertices[v].distance);
        }
    }

    private void recurseToSourceAndPrint(Vertex v) {
        if (v == null)
            return;

        recurseToSourceAndPrint(v.previous);
        System.out.print(v.getID() + " ");
    }

    public void printSpanningTree() {
        for (int v = 1; v < vertices.length; v++) {
            OutDegree o = vertices[v].getOutDegrees();

            while (o != null) {
                if (o.edge.isMinimumSpanning) {
                    System.out.println(v + " " + o.edge.getNeighbor(v).getID());
                    o.edge.isMinimumSpanning = false;
                }
                o = o.next;
            }
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public Edge getSortedEdges() {
        return sortedEdges;
    }
}

class UnionFind {
    /*
    The union data structure is implemented by an array. Positive numbers are directed edges
    whose value points to the parent vertex key value. Note that the key values are the same
    as the index of the vertex. Negative numbers represent root vertices and the absolute value
    of that negative number represents the number of vertices connected to that root through unions.
     */
    private int[] sets;

    public UnionFind(int size) {
        if (size > 0) {
            sets = new int[size];
            initializeSets();
        } else
            throw new IllegalArgumentException("size must be greater than zero");
    }

    private void initializeSets() {
        for (int i = 0; i < sets.length; i++)
            sets[i] = -1;
    }

    public void union(int x, int y) {
        if (x == y) return; // nothing to union

        int xRoot = find(x);
        int yRoot = find(y);

        if (getTotalMembers(yRoot) <= getTotalMembers(xRoot))
            connectRoots(yRoot, xRoot);
        else
            connectRoots(xRoot, yRoot);
    }

    /**
     * @return root vertex
     */
    public int find(int element) {
        return findAndPathCompress(element);
    }

    private int findAndPathCompress(int element) {
        if (sets[element] < 0)
            return element;

        return sets[element] = findAndPathCompress(sets[element]);
    }

    private void connectRoots(int childRoot, int parentRoot) {
        if (childRoot != parentRoot) {
            sets[parentRoot] += sets[childRoot];
            sets[childRoot] = parentRoot;
        }
    }

    private int getTotalMembers(int root) {
        return -sets[root];
    }
}

class PathFinder {
    private Graph g;
    private MyPriorityQueue pq;
    private int totalSpanningTreeLength = 0;

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
    }

    private void relax(Vertex min, OutDegree o) {
        Vertex neighbor = o.edge.getNeighbor(min.getID());
        if (neighbor.isKnown)
            return;

        int currentDistance = min.distance + o.edge.getWeight();
        if (neighbor.distance > currentDistance) {
            neighbor.distance = currentDistance;
            pq.percolateUp(neighbor.heapIndex);
            neighbor.previous = min;
        }
    }

    /**
     * finds the minimum spanning tree using Kruskal's algorithm
     */
    public void findMinimumSpanningTree() {
        UnionFind u = new UnionFind(g.getCapacity() + 1);
        Edge e = g.getSortedEdges();

        while (e != null) {
            int x = e.getLeftID();
            int y = e.getRightID();

            if (u.find(x) != u.find(y)) {
                e.isMinimumSpanning = true;
                u.union(x, y);
                totalSpanningTreeLength += e.getWeight();
            }
            e = e.next;
        }
    }

    public void printResults() {
        g.printDistances();
        System.out.println();
        g.printSpanningTree();
        System.out.println("Minimal spanning tree length = " + totalSpanningTreeLength);
    }
}

public class WIBUP4 {
    private final Scanner input;
    private Graph g;

    public WIBUP4(java.io.InputStream in) {
        input = new Scanner(in);
    }

    public void run() {
        initializeWithFirstLine();
        String[] tokens;
        tokens = input.nextLine().split(" ");

        while (inputHasNext(tokens)) {
            addEdge(tokens);
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

    private void addEdge(String[] tokens) {
        int fromVertexID = Integer.valueOf(tokens[0]);
        int toVertexID = Integer.valueOf(tokens[1]);
        int weight = Integer.valueOf(tokens[2]);

        g.addEdge(fromVertexID, toVertexID, weight);
    }

    private void runPathFinder() {
        PathFinder pf = new PathFinder(g);
        pf.findShortestPaths();
        pf.findMinimumSpanningTree();
        pf.printResults();
    }

    public static void main(String[] args) {
        WIBUP4 program = new WIBUP4(System.in);
        program.run();
    }
}
