package bankinfo.app;

import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.testng.Assert.*;

public class SmokeTestTest {

    @Test
    public void main_shouldPrintLoadedAccounts() throws Exception {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8.name()));

            SmokeTest.main(new String[0]);

        } finally {
            System.setOut(originalOut);
        }

        String output = out.toString(StandardCharsets.UTF_8.name());

        assertNotNull(output);
        assertTrue(output.contains("Loaded accounts:"));
        assertTrue(output.contains("client="));
        assertTrue(output.contains("branch="));
        assertTrue(output.contains("accountType="));
    }
}