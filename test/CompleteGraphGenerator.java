import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CompleteGraphGenerator {
    private final int numOfVertices;
    private final int numOfEdges;
    private final List<Integer> randWeights;
    private final List<String> edges;

    CompleteGraphGenerator(int numOfVertices) {
        this.numOfVertices = numOfVertices;
        numOfEdges = (numOfVertices * (numOfVertices - 1)) / 2;
        randWeights = new ArrayList<>(numOfEdges);
        edges = new ArrayList<>(numOfEdges);

        createUniqueShuffledWeights();
        generateShuffledEdges();
    }

    private void createUniqueShuffledWeights() {
        for (int weight = 1; weight < numOfEdges + 1; weight++)
            randWeights.add(weight);

        Collections.shuffle(randWeights);
    }

    private void generateShuffledEdges() {
        int weightIndex = 0;

        for (int v1 = 1; v1 < numOfVertices + 1; v1++) {
            for (int v2 = v1; v2 < numOfVertices + 1; v2++) {
                if (v1 == v2)
                    continue;

                String s = v1 + " " + v2 + " " + randWeights.get(weightIndex++);
                edges.add(s);
            }
        }
        Collections.shuffle(edges);
    }

    public void printEdgeList() {
        Random r = new Random();
        int sourceVertex = r.nextInt(numOfVertices) + 1; //interval [1, numOfVertices]
        System.out.println(numOfVertices + " " + sourceVertex);

        for (String edge : edges)
            System.out.println(edge);

        System.out.println(0 + " " + 0 + " " + 0); // print sentinel
    }

    public static void main(String[] args) {
        CompleteGraphGenerator cg = new CompleteGraphGenerator(Integer.valueOf(args[0]));
        cg.printEdgeList();
    }
}
