package org.objenesis.tck;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import junit.framework.TestCase;

import org.objenesis.ObjenesisException;
import org.objenesis.ObjenesisSerializer;
import org.objenesis.ObjenesisStd;
import org.objenesis.tck.candidates.SerializableWithAncestorThrowingException;

/**
 * Integration test for Objenesis. Should pass successfully on every supported JVM for all Objenesis
 * interface implementation.
 * 
 * @author Henri Tremblay
 */
public class ObjenesisTest extends TestCase {

   public static class ErrorHandler implements CandidateLoader.ErrorHandler {
      public void classNotFound(String name) {
         fail("Class not found : " + name);
      }
   }

   public static class JUnitReporter implements Reporter {

      private String currentObjenesis;

      private String currentCandidate;

      public void startTests(String platformDescription, Collection allCandidates,
         Collection allInstantiators) {
      }

      public void startTest(String candidateDescription, String objenesisDescription) {
         currentCandidate = candidateDescription;
         currentObjenesis = objenesisDescription;
      }

      public void endObjenesis(String description) {
      }

      public void endTests() {
      }

      public void exception(Exception exception) {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         PrintStream out = new PrintStream(buffer);
         out.println("Exception when instantiating " + currentCandidate + " with "
            + currentObjenesis + ": ");
         exception.printStackTrace(out);
         fail(buffer.toString());
      }

      public void result(boolean instantiatedObject) {
         assertTrue("Instantiating " + currentCandidate + " with " + currentObjenesis + " failed",
            instantiatedObject);
      }

      public void endTest() {
      }
   }

   private TCK tck = null;

   private CandidateLoader candidateLoader = null;

   protected void setUp() throws Exception {
      super.setUp();

      tck = new TCK();

      candidateLoader = new CandidateLoader(tck, getClass().getClassLoader(), new ErrorHandler());
   }

   protected void tearDown() throws Exception {
      candidateLoader = null;
      tck = null;
      super.tearDown();
   }

   public void testObjenesisStd() throws Exception {
      candidateLoader.loadFromResource(getClass(), "candidates/candidates.properties");
      tck.registerObjenesisInstance(new ObjenesisStd(), "Objenesis standard");
      tck.runTests(new JUnitReporter());
   }

   public void testObjenesisSerializer() throws Exception {
      candidateLoader.loadFromResource(getClass(), "candidates/serializable-candidates.properties");
      tck.registerObjenesisInstance(new ObjenesisSerializer(), "Objenesis serializer");
      tck.runTests(new JUnitReporter());
   }

   public void testObjenesisSerializerParentConstructorCalled() throws Exception {
      try {
    	  new ObjenesisSerializer().newInstance(SerializableWithAncestorThrowingException.class);
    	  fail("Parent constructor not called");
      } catch(ObjenesisException e) {
    	  assertTrue(e.getCause() instanceof InvocationTargetException);
      }
   }
}
