package org.objenesis.tck;

/**
 * Reports results from the TCK back to the user.
 * <p>The sequence these methods are called is described below:</p>
 * <pre>
 * startTests
 * (
 *   startCandidate
 *   ( result | exception ) *
 *   endCandidate
 * ) *
 * endTests
 * </pre>
 *
 * @author Joe Walnes
 * @see TCK
 * @see TextReporter
 */
public interface Reporter {

    /**
     * Report that the tests are starting. Provides information that is useful to be reported.
     *
     * @param platformDescription Description the platform being run on (i.e. JVM version, vendor, etc).
     * @param allCandidates Descriptions of all candidates being used in tests.
     * @param allInstantiators Descriptions of all ObjectInstantiators being used in tests.
     */
    void startTests(String platformDescription, String[] allCandidates, String[] allInstantiators);

    /**
     * Report that tests are about to commence on a particular candidate of class.
     *
     * @param description Description of the candidate class.
     */
    void startCandidate(String description);

    /**
     * Report details about what happened when an ObjectInstantiator tried to instantiate the current candidate.
     *
     * @param instantiatorDescription Description of the ObjectInstantiator.
     * @param instantiatedObject      Whether the ObjectInstantiator successfully instantiated the candidate class.
     */
    void result(String instantiatorDescription, boolean instantiatedObject);

    /**
     * Report that something bad happened.
     *
     * @param instantiatorDescription Description of instantiator that caused the exception.
     * @param exception               Exception thrown by instantiator.
     */
    void exception(String instantiatorDescription, Exception exception);

    /**
     * Report that tests have been completed for a particular candidate of clas.
     *
     * @param description Description of the candidate class.
     */
    void endCandidate(String description);

    /**
     * Report that all tests have finished. Nothing will be called after this method.
     */
    void endTests();

}
