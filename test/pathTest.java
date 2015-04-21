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
//
//        System.setOut(new PrintStream(out));
//    }

    @Test
    public void addedEdgesAreOrderedByWeight() {
        Graph g = new Graph(7, 1);
        g.addEdge(new Edge(1, 2, 2));
        g.addEdge(new Edge(1, 4, 1));
        g.addEdge(new Edge(2, 5, 10));
        g.addEdge(new Edge(2, 4, 3));
        g.addEdge(new Edge(5, 7, 6));
        g.addEdge(new Edge(3, 1, 4));
        g.addEdge(new Edge(3, 6, 5));
        g.addEdge(new Edge(4, 3, 2));
        g.addEdge(new Edge(7, 6, 1));
        g.addEdge(new Edge(4, 5, 2));
        g.addEdge(new Edge(4, 7, 4));
        g.addEdge(new Edge(4, 6, 8));


        for (int i = 0; i < 7; i++) {
            OutDegree o = g.getNeighbors(i + 1);
            System.out.println("neighbors of " + (i + 1));

            while (o != null) {
                System.out.println("edge from " + o.edge.vertexA + " to "
                        + o.edge.vertexB + " with weight: " + o.edge.weight);
                o = o.next;
            }
        }
    }
}
