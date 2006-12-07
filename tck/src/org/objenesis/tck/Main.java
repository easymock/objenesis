package org.objenesis.tck;

import org.objenesis.ObjectInstantiator;

import java.io.IOException;

/**
 * Command line launcher for Technology Compatibility Kit (TCK).
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
        attemptToRegisterInstantiator(tck, "org.objenesis.ObjectInputStreamInstantiator", "ObjectInputStream");
        attemptToRegisterInstantiator(tck, "org.objenesis.SunReflectionFactoryInstantiator", "SunReflection");
        attemptToRegisterInstantiator(tck, "org.objenesis.SunReflectionFactorySerializationInstantiator", "SunReflection for Serialization");
        attemptToRegisterInstantiator(tck, "org.objenesis.Sun13Instantiator", "Sun JDK 1.3");
        attemptToRegisterInstantiator(tck, "org.objenesis.GCJInstantiator", "GCJ");

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
            tck.registerInstantiator(cls, description);
        } catch (Exception exception) {
            tck.registerInstantiator(NullInstantiator.class, description);
            System.err.println("Cannot register instantiator '" + description + "' : " + exception);
        }
    }
    
    private static class NullInstantiator implements ObjectInstantiator {
    	public NullInstantiator(Class type) {			
		}
    	
    	public Object newInstance() {    	
    		return null;
    	}    
    }

}
