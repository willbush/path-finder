import java.util.Scanner;

class Vertex {
    private OutDegree outDegrees; // a singly linked list of outDegrees for the vertex
    private int id;
    Vertex previous; // previous is used by Dijkstra's and Prim's algorithms
    // distance is used by both Dijkstra and Prim and is the key in the heap which has priority.
    int distance, heapIndex; // heapIndex is the current location of the vertex in the heap.
    boolean isKnown; // used to flag that Dijkstra or Prim has added it to the known set.

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
    the spec output requires sorted neighbors.
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
    private Vertex from, to;
    private int weight;
    Edge next;
    boolean isMinimumSpanning;

    Edge(Vertex from, Vertex to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public Vertex getNeighbor(int vertexID) {
        if (to.getID() == vertexID)
            return from;
        else
            return to;
    }

    public int getWeight() {
        return weight;
    }
}

/**
 * This priority queue is implemented using a binary min heap. Since every
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
        elementCount = capacity;
        heap = new Vertex[capacity + 1];

        minID = min.getID();
        min.heapIndex = index;
        heap[index++] = min;
    }

    public void addVertex(Vertex v) {
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

    /*
    resets the heap to its original state after all items were added.
     */
    public void reset() {
        int minIndex = -1;
        elementCount = capacity;

        for (int v = 1; v < heap.length; v++) {
            heap[v].isKnown = false;
            heap[v].previous = null;
            if (heap[v].getID() == minID) {
                minIndex = heap[v].heapIndex;
                heap[v].distance = 0;
            } else
                heap[v].distance = Integer.MAX_VALUE;
        }
        swap(1, minIndex); // reset min vertex (i.e. the source) to top of the heap
    }
}

class Graph {
    private Vertex[] vertices;
    private MyPriorityQueue pq;
    private int sourceVertex;
    private Edge head;

    Graph(int numOfVertices, int sourceVertex) {
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

    private void addToSortedEdgeList(Edge e) {
        if (head == null)
            head = e;
        else
            placeByWeight(e);
    }

    private void placeByWeight(Edge e) {
        Edge current = e;
        Edge previous = current;

        if (e.getWeight() < current.getWeight()) {
            head = e;
            head.next = current;
        } else {
            while (e.getWeight() > current.getWeight()) {
                previous = current;
                current = current.next;
            }
            previous.next = e;
            previous.next.next = current;
        }
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
                if (o.edge.isMinimumSpanning) {
                    System.out.println(v + " " + o.edge.getNeighbor(v).getID());
                    o.edge.isMinimumSpanning = false;
                }
                o = o.next;
            }
        }
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
    private int setsRemaining;
    private int rootOfLastUnion;


    public UnionFind(int size) {
        if (size > 0) {
            rootOfLastUnion = 0;
            sets = new int[size];
            setsRemaining = size;
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
            setsRemaining--;
            rootOfLastUnion = parentRoot;
        }
    }

    public void printLastUnionRootAndSize() {
        System.out.println(rootOfLastUnion + " " + getTotalMembers(rootOfLastUnion));
    }

    private int getTotalMembers(int root) {
        return -sets[root];
    }

    public void printSets() {
        for (int value : sets)
            System.out.print(value + " ");

        System.out.println();
    }

    public int getSetsRemaining() {
        return setsRemaining;
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
    }

    public void printResults() {
        g.printDistances();
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
}

class TreeSpanner {
    private int totalSpanningTreeLength = 0;
    private MyPriorityQueue pq;
    private Graph g;

    TreeSpanner(Graph g) {
        this.g = g;
        pq = g.getPriorityQueue();
    }

    /**
     * finds the minimum spanning tree using Prim's algorithm
     */
    public void findMinimumSpanningTree() {
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
        int weight = o.edge.getWeight();

        if (neighbor.isKnown) {
            if (neighborSharesSpanningEdge(min, neighbor)) {
                o.edge.isMinimumSpanning = true;
                totalSpanningTreeLength += weight;
            }
            return;
        }

        if (weight < neighbor.distance) {
            neighbor.distance = weight;
            pq.percolateUp(neighbor.heapIndex);
            neighbor.previous = min;
        }
    }

    private boolean neighborSharesSpanningEdge(Vertex min, Vertex neighbor) {
        return min.previous != null && min.previous.getID() == neighbor.getID();
    }

    public void printResults() {
        g.printSpanningTree();
        System.out.println("Minimal spanning tree length = " + totalSpanningTreeLength);
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
            addEdge(tokens);
            tokens = input.nextLine().split(" ");
        }
        runDijkstraAndPrim();
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

    private void runDijkstraAndPrim() {
        PathFinder pf = new PathFinder(g);
        pf.findShortestPaths();
        pf.printResults();
        System.out.println();

        g.getPriorityQueue().reset();
        TreeSpanner ts = new TreeSpanner(g);
        ts.findMinimumSpanningTree();
        ts.printResults();
    }

    public static void main(String[] args) {
        WIBUP4 program = new WIBUP4(System.in);
        program.run();
    }
}
