import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class InputTest {
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    @Before
    public void arrange() {
        System.setOut(new PrintStream(out));
    }

    @Test
    public void canProcessInput1() throws IOException {
        File input = new File("test/testData/p4d1in.txt");
        InputStream in = new FileInputStream(input);
        Main program = new Main(in);
        program.run();

        String expectedOut = getOutputString("test/testData/p4d1out.txt");
        assertEquals(expectedOut, out.toString());
    }

    @Test
    public void canProcessInput2() throws IOException {
        File input = new File("test/testData/p4d2in.txt");
        InputStream in = new FileInputStream(input);
        Main program = new Main(in);
        program.run();

        String expectedOut = getOutputString("test/testData/p4d2out.txt");
        assertEquals(expectedOut, out.toString());
    }

    @Test
    public void canProcessInput3() throws IOException {
        File input = new File("test/testData/p4d3in.txt");
        InputStream in = new FileInputStream(input);
        Main program = new Main(in);
        program.run();

        String expectedOut = getOutputString("test/testData/p4d3out.txt");
        assertEquals(expectedOut, out.toString());
    }

    @Test
    public void canProcessInput4() throws IOException {
        File input = new File("test/testData/p4d4in.txt");
        InputStream in = new FileInputStream(input);
        Main program = new Main(in);
        program.run();

        String expectedOut = getOutputString("test/testData/p4d4out.txt");
        assertEquals(expectedOut, out.toString());
    }

    @Test
    public void canProcessInput5() throws IOException {
        File input = new File("test/testData/p4d5in.txt");
        InputStream in = new FileInputStream(input);
        Main program = new Main(in);
        program.run();

        String expectedOut = getOutputString("test/testData/p4d5out.txt");
        assertEquals(expectedOut, out.toString());
    }

    @Test
    public void canProcessRandomCompleteGraph() {
        Random r = new Random();
        CompleteGraphGenerator cg = new CompleteGraphGenerator(r.nextInt(10) + 1); // interval [1, 10]
        cg.printEdgeList();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Main program = new Main(in);
        program.run();
    }

    private String getOutputString(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, StandardCharsets.UTF_8);
    }
}
