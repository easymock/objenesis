package org.objenesis.tck;

import java.io.IOException;

import org.objenesis.Objenesis;
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

		TextReporter reporter = new TextReporter(System.out, System.err);

		runTest(new ObjenesisStd(), reporter, "Objenesis std",
				"candidates/candidates.properties");
		runTest(new ObjenesisSerializer(), reporter, "Objenesis serializer",
				"candidates/serializable-candidates.properties");
		
		reporter.printResult();
	}

	private static void runTest(Objenesis objenesis, Reporter reporter,
			String description, String candidates) throws IOException {
		TCK tck = new TCK();
		tck.registerObjenesisInstance(objenesis, description);

		CandidateLoader candidateLoader = new CandidateLoader(tck, Main.class
				.getClassLoader(), new CandidateLoader.LoggingErrorHandler(
				System.err));
		
		candidateLoader.loadFromResource(Main.class, candidates);

		tck.runTests(reporter);
	}

}
