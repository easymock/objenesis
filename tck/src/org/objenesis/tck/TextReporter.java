package org.objenesis.tck;

import java.io.PrintStream;

/**
 * Reports results from TCK as tabulated text, suitable for dumping to the console or a file
 * and being read by a human.
 *
 * @author Joe Walnes
 * @see TCK
 * @see Reporter
 */
public class TextReporter implements Reporter {

    private final PrintStream summary;
    private final PrintStream log;

    private int maxInstantiatorWidth;
    private int maxCandidateWidth;
    private String currentCandidate;

    /**
     * @param summary Output of main report.
     * @param log     Any additional information, useful for diagnostics.
     */
    public TextReporter(PrintStream summary, PrintStream log) {
        this.summary = summary;
        this.log = log;
    }

    public void startTests(String platformDescription, String[] allCandidates, String[] allInstantiators) {
        summary.println("Running TCK on platform: " + platformDescription);
        summary.println();

        maxInstantiatorWidth = lengthOfLongestStringIn(allInstantiators);
        maxCandidateWidth = lengthOfLongestStringIn(allCandidates);

        StringBuffer header = new StringBuffer();
        header.append(pad("", maxCandidateWidth)).append(' ');
        for (int i = 0; i < allInstantiators.length; i++) {
            String instantiator = allInstantiators[i];
            header.append(pad(instantiator, maxInstantiatorWidth)).append(' ');
        }
        summary.println(header);
    }

    public void startCandidate(String description) {
        summary.print(pad(description, maxCandidateWidth) + " ");
        currentCandidate = description;
    }

    public void result(String instantiatorDescription, boolean instantiatedObject) {
        summary.print(pad(instantiatedObject ? "Y" : "n", maxInstantiatorWidth) + " ");
    }

    public void exception(String instantiatorDescription, Exception exception) {
        summary.print(pad("!", maxInstantiatorWidth) + " ");
        log.println("--- Candidate '" + currentCandidate +
                "', Insantiator '" + instantiatorDescription + "' ---");
        exception.printStackTrace(log);
    }

    public void endCandidate(String description) {
        summary.println();
    }

    public void endTests() {
    }

    private String pad(String text, int width) {
        if (text.length() == width) {
            return text;
        } else if (text.length() > width) {
            return text.substring(0, width);
        } else {
            StringBuffer padded = new StringBuffer(text);
            while (padded.length() < width) {
                padded.append(' ');
            }
            return padded.toString();
        }
    }

    private int lengthOfLongestStringIn(String[] allCandidates) {
        int result = 0;
        for (int i = 0; i < allCandidates.length; i++) {
            int len = allCandidates[i].length();
            if (len > result) {
                result = len;
            }
        }
        return result;
    }

}
