package org.objenesis.tck;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TextReporterTest extends TestCase {

    private TextReporter textReporter;
    private ByteArrayOutputStream summaryBuffer;

    protected void setUp() throws Exception {
        super.setUp();
        summaryBuffer = new ByteArrayOutputStream();
        ByteArrayOutputStream logBuffer = new ByteArrayOutputStream();
        textReporter = new TextReporter(new PrintStream(summaryBuffer), new PrintStream(logBuffer));
    }

    public void testReportsSuccessesInTabularFormat() {
        textReporter.startTests("Some platform",
                new String[]{"candidate A", "candidate B", "candidate C"},
                new String[]{"instantiator1", "instantiator2", "instantiator3"}
        );

        textReporter.startCandidate("candidate A");
        textReporter.result("instantiator1", false);
        textReporter.result("instantiator2", false);
        textReporter.result("instantiator3", true);
        textReporter.endCandidate("candidate A");

        textReporter.startCandidate("candidate B");
        textReporter.result("instantiator1", true);
        textReporter.result("instantiator2", false);
        textReporter.result("instantiator3", true);
        textReporter.endCandidate("candidate B");

        textReporter.startCandidate("candidate C");
        textReporter.exception("instantiator1", new RuntimeException("Problem"));
        textReporter.result("instantiator2", false);
        textReporter.result("instantiator3", true);
        textReporter.endCandidate("candidate C");

        textReporter.endTests();

        String expected = "" +
                "Running TCK on platform: Some platform\n" +
                "\n" +
                "            instantiator1 instantiator2 instantiator3 \n" +
                "candidate A n             n             Y             \n" +
                "candidate B Y             n             Y             \n" +
                "candidate C !             n             Y             \n";

        assertEquals(expected, summaryBuffer.toString());
    }

}
