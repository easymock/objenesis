package org.objenesis.tck;

import junit.framework.TestCase;

import org.objenesis.ObjenesisSerializer;
import org.objenesis.ObjenesisStd;

/**
 * @author Administrator
 *
 */
public class ObjenesisTest extends TestCase {

    public static class ErrorHandler implements CandidateLoader.ErrorHandler {
        public void classNotFound(String name) {
            fail("Class not found : " + name);
        }
    }
 
    public static class JUnitReporter implements Reporter {

    	public void startCandidate(String description) {
    	}

    	public void startTests(String platformDescription, String[] allCandidates,
    			String[] allInstantiators) {
    	}
    	
    	public void endCandidate(String description) {
    	}

    	public void endTests() {
    	}

    	public void exception(String instantiatorDescription, Exception exception) {
    		fail(instantiatorDescription);
    	}

    	public void result(String instantiatorDescription, boolean instantiatedObject) {
    		assertTrue(instantiatorDescription, instantiatedObject);
    	}
    }
    
    private TCK tck = null;

	protected void setUp() throws Exception {
		super.setUp();
		
    	tck = new TCK();
    	
        CandidateLoader candidateLoader = new CandidateLoader(
                tck,
                getClass().getClassLoader(),
                new ErrorHandler());
        candidateLoader.loadFromResource(getClass(), "candidates/candidates.properties");		
	}

	protected void tearDown() throws Exception {
		tck = null;
		super.tearDown();
	}

	public void testObjenesisStd() throws Exception {
    	tck.registerObjenesisInstance(new ObjenesisStd(), "Objenesis standard");    	
        tck.runTests(new JUnitReporter());		
	}

	public void testObjenesisSerializer() throws Exception {
		tck.registerObjenesisInstance(new ObjenesisSerializer(), "Objenesis serializer");    	
        tck.runTests(new JUnitReporter());		
	}
}
