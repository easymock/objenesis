package org.objenesis.tck;

import org.objenesis.ObjectInstantiator;

import java.io.IOException;

/**
 * Command line launcher for Technology Compatability Kit (TCK).
 * 
 * @author Joe Walnes
 * @see TCK
 */
public class Main {
    public static void main(String[] args) throws IOException {
        TCK tck = new TCK();

        attemptToRegisterInstantiator(tck, "org.objenesis.NewInstanceInstantiator", "NewInstance");
        attemptToRegisterInstantiator(tck, "org.objenesis.ConstructorInstantiator", "Constructor");
        attemptToRegisterInstantiator(tck, "org.objenesis.AccessibleInstantiator", "Accessible");
        attemptToRegisterInstantiator(tck, "org.objenesis.SunReflectionFactoryInstantiator", "SunReflection");
        attemptToRegisterInstantiator(tck, "org.objenesis.Sun13Instantiator", "Sun JDK 1.3");

        CandidateLoader candidateLoader = new CandidateLoader(
                tck,
                Main.class.getClassLoader(),
                new CandidateLoader.LoggingErrorHandler(System.err));
        candidateLoader.loadFromResource(Main.class, "candidates/candidates.properties");

        tck.runTests(new TextReporter(System.out, System.err));
    }

    private static void attemptToRegisterInstantiator(TCK tck, final String instantiatorClassName, String description) {
        try {
            Class cls = Class.forName(instantiatorClassName);
            ObjectInstantiator instance = (ObjectInstantiator) cls.newInstance();
            tck.registerInstantiator(instance, description);
        } catch (Exception exception) {
            tck.registerInstantiator(new ObjectInstantiator() {
                public Object instantiate(Class type) {
                    return null;
                }
            }, description);
            System.err.println("Cannot register instantiator '" + description + "' : " + exception);
        }
    }

}
