import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputTest {

    @Test
    public void canProcessInput1() throws IOException {
        File input = new File("test/testData/p4d1in.txt");
        InputStream in = new FileInputStream(input);
        WIBUP4 program = new WIBUP4(in);
        program.run();
        System.out.println();
    }

    @Test
    public void canProcessInput2() throws IOException {
        File input = new File("test/testData/p4d2in.txt");
        InputStream in = new FileInputStream(input);
        WIBUP4 program = new WIBUP4(in);
        program.run();
        System.out.println();
    }

    @Test
    public void canProcessInput3() throws IOException {
        File input = new File("test/testData/p4d3in.txt");
        InputStream in = new FileInputStream(input);
        WIBUP4 program = new WIBUP4(in);
        program.run();
        System.out.println();
    }
}
