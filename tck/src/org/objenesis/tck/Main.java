package org.objenesis.tck;

import java.io.IOException;

import org.objenesis.ObjenesisSerializer;
import org.objenesis.ObjenesisStd;

/**
 * Command line launcher for Technology Compatibility Kit (TCK).
 * 
 * @author Joe Walnes
 * @see TCK
 */
public class Main {
    public static void main(String[] args) throws IOException {
        
    	TCK tck = new TCK();
    	
    	tck.registerObjenesisInstance(new ObjenesisStd(), "Objenesis standard");
    	tck.registerObjenesisInstance(new ObjenesisSerializer(), "Objenesis serializer");

        CandidateLoader candidateLoader = new CandidateLoader(
                tck,
                Main.class.getClassLoader(),
                new CandidateLoader.LoggingErrorHandler(System.err));
        candidateLoader.loadFromResource(Main.class, "candidates/candidates.properties");

        tck.runTests(new TextReporter(System.out, System.err));
    }

}
