import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

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
        WIBUP4 program = new WIBUP4(in);
        program.run();

        String expectedOut = getOutputString("test/testData/p4d1out.txt");
        assertEquals(expectedOut, out.toString());
    }

    @Test
    public void canProcessInput2() throws IOException {
        File input = new File("test/testData/p4d2in.txt");
        InputStream in = new FileInputStream(input);
        WIBUP4 program = new WIBUP4(in);
        program.run();

        String expectedOut = getOutputString("test/testData/p4d2out.txt");
        assertEquals(expectedOut, out.toString());
    }

    @Test
    public void canProcessInput3() throws IOException {
        File input = new File("test/testData/p4d3in.txt");
        InputStream in = new FileInputStream(input);
        WIBUP4 program = new WIBUP4(in);
        program.run();

        String expectedOut = getOutputString("test/testData/p4d3out.txt");
        assertEquals(expectedOut, out.toString());
    }

    private String getOutputString(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, StandardCharsets.UTF_8);
    }
}
