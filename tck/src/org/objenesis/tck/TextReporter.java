package org.objenesis.tck;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Reports results from TCK as tabulated text, suitable for dumping to the console or a file
 * and being read by a human.
 *
 * @author Joe Walnes
 * @author Henri Tremblay
 * @see TCK
 * @see Reporter
 */
public class TextReporter implements Reporter {

	private static class ExceptionEntry implements Comparable {
		
		String objenesisDescription;
		String candidateDescription;
		Exception exception;
		
		public ExceptionEntry(String objenesisDescription, String candidateDescription, Exception exception) {
			this.objenesisDescription = objenesisDescription;
			this.candidateDescription = candidateDescription;
			this.exception = exception;
		}

		public int compareTo(Object o) {
			ExceptionEntry entry = (ExceptionEntry) o;
			int result = objenesisDescription.compareTo(entry.objenesisDescription);
			if(result != 0) {
				return result;
			}
			return candidateDescription.compareTo(entry.candidateDescription);
		}
	}
	
    private final PrintStream summary;
    private final PrintStream log;

    private int maxObjenesisWidth;
    private int maxCandidateWidth;
    private String currentObjenesis;
    private String currentCandidate;
    
    private int errorCount;    
    private List exceptions;
    private long startTime;
    private boolean firstTime; 

    /**
     * @param summary Output of main report.
     * @param log     Any additional information, useful for diagnostics.
     */
    public TextReporter(PrintStream summary, PrintStream log) {
        this.summary = summary;
        this.log = log;
    }

    public void startTests(String platformDescription, String[] allCandidates, String[] allInstantiators) {
    	
    	errorCount = 0;
    	exceptions = new ArrayList();  
    	currentCandidate = "xxx";
    	firstTime = true;
    	
        summary.println("Running TCK on platform: " + platformDescription);
        summary.println();

        maxObjenesisWidth = lengthOfLongestStringIn(allInstantiators);
        maxCandidateWidth = lengthOfLongestStringIn(allCandidates);

        StringBuffer header = new StringBuffer();
        header.append(pad("", maxCandidateWidth)).append(' ');
        for (int i = 0; i < allInstantiators.length; i++) {
            String desc = allInstantiators[i];
            header.append(pad(desc, maxObjenesisWidth)).append(' ');
        }
        summary.println(header);
        
        startTime = System.currentTimeMillis();
    }

    public void startTest(String candidateDescription, String objenesisDescription) {
    	if(!candidateDescription.equals(currentCandidate)) {
    		currentCandidate = candidateDescription;
    		if(firstTime) {
    			firstTime = false;
    		}
    		else {
    			summary.println();
    		}
    		summary.print(pad(currentCandidate, maxCandidateWidth) + " ");
    	}
        
        currentObjenesis = objenesisDescription;
    }

    public void result(boolean instantiatedObject) {
    	if(!instantiatedObject) {
    		errorCount++;
    	}
        summary.print(pad(instantiatedObject ? "Y" : "n", maxObjenesisWidth) + " ");
    }

    public void exception(Exception exception) {
    	result(false);
    	exceptions.add(new ExceptionEntry(currentObjenesis, currentCandidate, exception));        
    }

	public void endTest() {      
    }

    public void endTests() {
    	
    	long finalTime = System.currentTimeMillis() - startTime;
    	
    	summary.println();
    	summary.println();
    	
    	if(errorCount != 0) {
    		
        	// Print all exceptions
        	Collections.sort(exceptions);
        	
        	for (Iterator it = exceptions.iterator(); it.hasNext();) {
    			ExceptionEntry element = (ExceptionEntry) it.next();
    	        log.println("--- Candidate '" + element.candidateDescription +
    	                "', Instantiator '" + element.objenesisDescription + "' ---");
    	        element.exception.printStackTrace(log);			
    		}
        	
        	log.println();           	
        	
        	summary.println("--- FAILED: " + errorCount + " error(s) occured ---");
    	}
    	else {
    		summary.println("--- SUCCESSFUL: TCK tests passed without errors in " + finalTime + " ms");
    	}
    	
    	summary.println();    	
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
