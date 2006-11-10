package org.objenesis.tck;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;

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

        ByteArrayOutputStream expectedSummaryBuffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(expectedSummaryBuffer);
        out.println("Running TCK on platform: Some platform");
        out.println();
   		out.println("            instantiator1 instantiator2 instantiator3 ");
        out.println("candidate A n             n             Y             ");
        out.println("candidate B Y             n             Y             ");
        out.println("candidate C !             n             Y             ");

        assertEquals(expectedSummaryBuffer.toString(), summaryBuffer.toString());
    }

}
