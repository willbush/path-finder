import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertEquals;

public class pathTest {
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

//    @Before
//    public void arrange() {
//        System.setOut(new PrintStream(out));
//    }

    @Test
    public void addedEdgesAreOrderedByWeight() {
        int numOfVertices = 7;
        Graph g = new Graph(numOfVertices, 1);
        g.addEdge(1, 2, 2);
        g.addEdge(1, 4, 1);
        g.addEdge(2, 5, 10);
        g.addEdge(2, 4, 3);
        g.addEdge(5, 7, 6);
        g.addEdge(3, 1, 4);
        g.addEdge(3, 6, 5);
        g.addEdge(4, 3, 2);
        g.addEdge(7, 6, 1);
        g.addEdge(4, 5, 2);
        g.addEdge(4, 7, 4);
        g.addEdge(4, 6, 8);


        for (int v = 1; v <= numOfVertices; v++) {
            OutDegree o = g.getVertex(v).getOutDegrees();
            System.out.println("neighbors of " + (v));

            while (o != null) {
                System.out.println("neighbor: " + o.edge.getNeighbor(v).id
                        + " edge weight: " + o.edge.weight);
                o = o.next;
            }
        }
    }

    @Test
    public void canRemoveMin() {
        // construct graph
        int maxNodes = 7;
        Graph g = new Graph(maxNodes, 1);
        g.addEdge(1, 2, 2);
        g.addEdge(1, 4, 1);
        g.addEdge(2, 5, 10);
        g.addEdge(2, 4, 3);
        g.addEdge(5, 7, 6);
        g.addEdge(3, 1, 4);
        g.addEdge(3, 6, 5);
        g.addEdge(4, 3, 2);
        g.addEdge(7, 6, 1);
        g.addEdge(4, 5, 2);
        g.addEdge(4, 7, 4);
        g.addEdge(4, 6, 8);

        MyPriorityQueue pq = g.getPriorityQueue();

        g.getVertex(2).distance = 22;
        pq.percolateUp(g.getVertex(2).heapKey);
        g.getVertex(7).distance = 88;
        pq.percolateUp(g.getVertex(7).heapKey);
        g.getVertex(6).distance = 77;
        pq.percolateUp(g.getVertex(6).heapKey);
        g.getVertex(5).distance = 66;
        pq.percolateUp(g.getVertex(5).heapKey);
        g.getVertex(3).distance = 55;
        pq.percolateUp(g.getVertex(3).heapKey);

        assertEquals(0, pq.deleteMin().distance);
        assertEquals(22, pq.deleteMin().distance);
        assertEquals(55, pq.deleteMin().distance);
        assertEquals(66, pq.deleteMin().distance);
        assertEquals(77, pq.deleteMin().distance);
        assertEquals(88, pq.deleteMin().distance);
    }
}
