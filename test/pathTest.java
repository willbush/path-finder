import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class pathTest {
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

//    @Before
//    public void arrange() {
//        System.setOut(new PrintStream(out));
//    }

    @Test
    public void addedEdgesAreOrderedByWeight() {
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


        for (int i = 0; i < maxNodes; i++) {
            OutDegree o = g.getNeighbors(i + 1);
            System.out.println("neighbors of " + (i + 1));

            while (o != null) {
                System.out.println("edge from " + o.edge.left.id + " to "
                        + o.edge.right.id + " with weight: " + o.edge.weight);
                o = o.next;
            }
        }

        MyPriorityQueue pq = g.getPriorityQueue();
        pq.print();
    }
}
