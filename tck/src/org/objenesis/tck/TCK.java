package org.objenesis.tck;

import org.objenesis.InstantiatorFactory;
import org.objenesis.ObjectInstantiator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <b>Technology Compatability Kit</b> (TCK) for {@link ObjectInstantiator}s.
 * <p/>
 * This TCK accepts a set of candidate classes (class it attempts to instantiate)
 * and a set of ObjectInstantiators. It then tries instantiating every candidate
 * with every instantiator, reporting the results to a {@link Reporter}.
 *
 * <h3>Example usage</h3>
 * <pre>
 * TCK tck = new TCK();
 * // register candidate classes.
 * tck.registerCandiate(SomeClass.class, "A basic class");
 * tck.registerCandiate(SomeEvil.class, "Something evil");
 * tck.registerCandiate(NotEvil.class, "Something nice");
 * // register instantiators.
 * tck.registerInstantiator(new MyInstantiator(), "My instantiator");
 * tck.registerInstantiator(new YourInstantiator(), "Yourinstantiator");
 * // go!
 * Reporter reporter = new TextReporter(System.out, System.err);
 * tck.runTests(reporter);
 * </pre>
 *
 * @author Joe Walnes
 * @see ObjectInstantiator
 * @see Reporter
 * @see Main
 */
public class TCK {

    private List instantiators = new ArrayList();
    private List candidates = new ArrayList();
    private Map descriptions = new HashMap();

    /**
     * Register a candidate class to attempt to instantiate.
     */
    public void registerCandiate(Class candidateClass, String description) {
        candidates.add(candidateClass);
        descriptions.put(candidateClass, description);
    }

    /**
     * Register an ObjectInstantiator to use when attempting to instantiate a class.
     */
    public void registerInstantiator(Class instantiatorClass, String description) {
        instantiators.add(instantiatorClass);
        descriptions.put(instantiatorClass, description);
    }

    /**
     * Run all TCK tests.
     *
     * @param reporter Where to report the results of the test to.
     */
    public void runTests(Reporter reporter) {
        reporter.startTests(describePlatform(),
                findAllDescriptions(candidates, descriptions),
                findAllDescriptions(instantiators, descriptions));

        for (Iterator i = candidates.iterator(); i.hasNext();) {
            Class candidateClass = (Class) i.next();
            String candidateDescription = (String) descriptions.get(candidateClass);

            reporter.startCandidate(candidateDescription);
            for (Iterator j = instantiators.iterator(); j.hasNext();) {
                Class instantiatorClass = (Class) j.next();
                String instantiatorDescription = (String) descriptions.get(instantiatorClass);

                runTest(reporter, candidateClass, instantiatorClass, instantiatorDescription);
            }
            reporter.endCandidate(candidateDescription);
        }
        reporter.endTests();
    }

    private void runTest(Reporter reporter, Class candidate,
                         Class instantiatorClass, String instantiatorDescription) {
        try {
        	ObjectInstantiator instantiator = InstantiatorFactory.getInstantiator(instantiatorClass, candidate);
            Object instance = instantiator.newInstance();
            boolean success = instance != null && instance.getClass() == candidate;
            reporter.result(instantiatorDescription, success);
        } catch (Exception e) {
            reporter.exception(instantiatorDescription, e);
        }
    }

    private String[] findAllDescriptions(List keys, Map descriptions) {
        String[] results = new String[keys.size()];
        for (int i = 0; i < results.length; i++) {
            results[i] = (String)descriptions.get(keys.get(i));
        }
        return results;
    }

    /**
     * Describes the platform. Outputs Java version and vendor.
     * To change this behavior, override this method.
     */
    protected String describePlatform() {
        return "Java " + System.getProperty("java.specification.version")
                + " (" + System.getProperty("java.vm.version")
                + " " + System.getProperty("java.vm.vendor") + ")";
    }

}
