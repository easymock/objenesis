package org.objenesis.tck;

import junit.framework.TestCase;
import org.objenesis.ObjectInstantiator;

public class TCKTest extends TestCase {

    public void testReportsAllCandidatesAndInstantiatorCombinationsToReporter() {
        // Given... a TCK with some candidate classes: A, B and C.
        TCK tck = new TCK();

        tck.registerCandiate(CandidateA.class, "Candidate A");
        tck.registerCandiate(CandidateB.class, "Candidate B");
        tck.registerCandiate(CandidateC.class, "Candidate C");

        // And... two ObjectInstantiators registered
        class StubbedInstantiator implements ObjectInstantiator {
            public Object instantiate(Class type) {
                return null;
            }
        }
        tck.registerInstantiator(new StubbedInstantiator(), "Instantiator1");
        tck.registerInstantiator(new StubbedInstantiator(), "Instantiator2");

        // When... the TCK tests are run
        Reporter reporter = new RecordingReporter();
        tck.runTests(reporter);

        // Expect... the reporter to have received a sequence of calls
        //           notifying it of what the TCK is doing.
        assertEquals("" +
                "startTests()\n" +
                "startCandidate('Candidate A')\n" +
                "result('Instantiator1', false);\n" +
                "result('Instantiator2', false);\n" +
                "endCandidate('Candidate A')\n" +
                "startCandidate('Candidate B')\n" +
                "result('Instantiator1', false);\n" +
                "result('Instantiator2', false);\n" +
                "endCandidate('Candidate B')\n" +
                "startCandidate('Candidate C')\n" +
                "result('Instantiator1', false);\n" +
                "result('Instantiator2', false);\n" +
                "endCandidate('Candidate C')\n" +
                "endTests()\n",
                reporter.toString());
    }

    public void testReportsSuccessIfCandidateCanBeInstantiated() {
        // Given... a TCK with some candidate classes: A, B and C.
        TCK tck = new TCK();

        tck.registerCandiate(CandidateA.class, "Candidate A");
        tck.registerCandiate(CandidateB.class, "Candidate B");

        // And... a single ObjectInsantiator registered that can instantiate
        //        A but not B.
        tck.registerInstantiator(new ObjectInstantiator() {
            public Object instantiate(Class type) {
                return type == CandidateA.class ? new CandidateA() : null;
            }
        }, "instantiator");

        // When... the TCK tests are run
        Reporter reporter = new RecordingReporter();
        tck.runTests(reporter);

        // Expect... the reporter to be notified that A succeeded
        //           but B failed.
        assertEquals("" +
                "startTests()\n" +
                "startCandidate('Candidate A')\n" +  // A
                "result('instantiator', true);\n" +  // true
                "endCandidate('Candidate A')\n" +
                "startCandidate('Candidate B')\n" +  // B
                "result('instantiator', false);\n" + // false
                "endCandidate('Candidate B')\n" +
                "endTests()\n",
                reporter.toString());
    }

    // Some sample classes used for testing.

    public static class CandidateA {
    }

    public static class CandidateB {
    }

    public static class CandidateC {
    }

    /**
     * A poor man's mock. Using a recording test double to verify
     * interactions between the TCK and the Recorder.
     * <p/>
     * Note: This test case could be simplified by using a mock
     * object library. However, I wanted to simplify
     * dependencies - particularly as in the future, the
     * mock libraries may depend on objenesis - getting
     * into an awkward cyclical dependency situation.
     * -Joe.
     */
    private static class RecordingReporter implements Reporter {

        private StringBuffer log = new StringBuffer();

        public void startTests(String platformDescription, String[] allCandidates, String[] allInstantiators) {
            log.append("startTests()\n");
        }

        public void startCandidate(String description) {
            log.append("startCandidate('").append(description).append("')\n");
        }

        public void result(String instantiatorDescription, boolean instantiatedObject) {
            log.append("result('").append(instantiatorDescription)
                    .append("', ").append(instantiatedObject).append(");\n");
        }

        public void exception(String instantiatorDescription, Exception exception) {
            log.append("exception('").append(instantiatorDescription).append("')\n");
        }

        public void endCandidate(String description) {
            log.append("endCandidate('").append(description).append("')\n");
        }

        public void endTests() {
            log.append("endTests()\n");
        }

        public String toString() {
            return log.toString();
        }
    }
}
