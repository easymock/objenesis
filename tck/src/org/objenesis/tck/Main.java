package org.objenesis.tck;

import java.io.IOException;
import java.io.Serializable;

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

   private static class MockSuperClass {
      private final boolean superConstructorCalled;

      public MockSuperClass() {
         superConstructorCalled = true;
      }

      public boolean isSuperConstructorCalled() {
         return superConstructorCalled;
      }
   }

   private static class MockClass extends MockSuperClass implements Serializable {
      private static final long serialVersionUID = 1L;
      
      private final boolean constructorCalled;

      public MockClass() {
         constructorCalled = true;
      }

      public boolean isConstructorCalled() {
         return constructorCalled;
      }
   }

   /**
    * Main class of the TCK. Can also be called as a normal method from an application server.
    * 
    * @param args No parameters are required
    * @throws IOException When the TCK fails to read properties' files.
    */
   public static void main(String[] args) throws IOException {

      TextReporter reporter = new TextReporter(System.out, System.err);

      runTest(new ObjenesisStd(), reporter, "Objenesis std", "candidates/candidates.properties");
      runTest(new ObjenesisSerializer(), reporter, "Objenesis serializer",
         "candidates/serializable-candidates.properties");

      boolean result = runParentConstructorTest();

      reporter.printResult(result);
   }

   private static boolean runParentConstructorTest() {
      try {
         Object result = new ObjenesisSerializer().newInstance(MockClass.class);
         MockClass mockObject = (MockClass) result;
         return mockObject.isSuperConstructorCalled() && !mockObject.isConstructorCalled();
      }
      catch(Exception e) {
         System.err.println("--- Not serializable parent constructor called ---");
         e.printStackTrace(System.err);
         return false;
      }
   }

   private static void runTest(Objenesis objenesis, Reporter reporter, String description,
      String candidates) throws IOException {
      TCK tck = new TCK();
      tck.registerObjenesisInstance(objenesis, description);

      CandidateLoader candidateLoader = new CandidateLoader(tck, Main.class.getClassLoader(),
         new CandidateLoader.LoggingErrorHandler(System.err));

      candidateLoader.loadFromResource(Main.class, candidates);

      tck.runTests(reporter);
   }

}
