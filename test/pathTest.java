import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class pathTest {
    private static final int numOfVertices = 7;
    private Graph g = new Graph(numOfVertices, 1);

    @Before
    public void arrange() {
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
    }

    @Test
    public void canAddEdges() {
        checkNeighbors(1, new int[]{2, 3, 4});
        checkNeighbors(2, new int[]{1, 4, 5});
        checkNeighbors(3, new int[]{1, 4, 6});
        checkNeighbors(4, new int[]{1, 2, 3, 5, 6, 7});
        checkNeighbors(5, new int[]{2, 4, 7});
        checkNeighbors(6, new int[]{3, 4, 7});
        checkNeighbors(7, new int[]{4, 5, 6});
    }

    private void checkNeighbors(int v, int[] neighbors) {
        OutDegree o = g.getVertex(v).getOutDegrees();
        int index = 0;

        while (o != null) {
            assertEquals(neighbors[index], o.edge.getNeighbor(v).getID());
            o = o.next;
            index++;
        }
    }

    @Test
    public void canRemoveMin() {
        MyPriorityQueue pq = g.getPriorityQueue();

        modifyDistance(pq, 2, 22);
        modifyDistance(pq, 7, 88);
        modifyDistance(pq, 6, 77);
        modifyDistance(pq, 5, 66);
        modifyDistance(pq, 3, 55);
        modifyDistance(pq, 4, 10);

        assertEquals(0, pq.deleteMin().distance);
        assertEquals(10, pq.deleteMin().distance);
        assertEquals(22, pq.deleteMin().distance);
        assertEquals(55, pq.deleteMin().distance);
        assertEquals(66, pq.deleteMin().distance);
        assertEquals(77, pq.deleteMin().distance);
        assertEquals(88, pq.deleteMin().distance);
    }

    private void modifyDistance(MyPriorityQueue pq, int v, int d) {
        g.getVertex(v).distance = d;
        pq.percolateUp(g.getVertex(v).heapIndex);
    }
}
