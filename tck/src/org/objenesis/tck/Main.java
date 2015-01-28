/**
 * Copyright 2006-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

      @SuppressWarnings("unused")
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

      boolean result = run(reporter);

      reporter.printResult(result);

      if(reporter.hasErrors()) {
         System.exit(1);
      }
   }

   /**
    * Run the full test suite using standard Objenesis instances
    * 
    * @param reporter result are recorded in the reporter
    * @return if the parent constructor test was successful
    */
   public static boolean run(Reporter reporter) {
      runStandardTest(new ObjenesisStd(), reporter);
      runSerializerTest(new ObjenesisSerializer(), reporter);

      boolean result = runParentConstructorTest(new ObjenesisSerializer());
      return result;
   }

   /**
    * Run the serializing suite on the provided Objenesis instance
    * 
    * @param reporter result are recorded in the reporter
    * @param objenesis Objenesis instance to test
    */
   public static void runSerializerTest(Objenesis objenesis, Reporter reporter) {
      runTest(objenesis, reporter, "Objenesis serializer",
         "candidates/serializable-candidates.properties");
   }

   /**
    * Run the standard suite on the provided Objenesis instance
    * 
    * @param reporter result are recorded in the reporter
    * @param objenesis Objenesis instance to test
    */
   public static void runStandardTest(Objenesis objenesis, Reporter reporter) {
      runTest(objenesis, reporter, "Objenesis std", "candidates/candidates.properties");
   }

   /**
    * A special test making sure the first none serializable class no-args constructor is called
    * 
    * @param objenesis Objenesis instance to test
    * @return if the test was successful
    */
   public static boolean runParentConstructorTest(Objenesis objenesis) {
      try {
         Object result = objenesis.newInstance(MockClass.class);
         MockClass mockObject = (MockClass) result;
         return mockObject.isSuperConstructorCalled() && !mockObject.isConstructorCalled();
      }
      catch(Exception e) {
         System.err.println("--- Not serializable parent constructor called ---");
         e.printStackTrace(System.err);
         return false;
      }
   }

   /**
    * Run a suite of tests (candidates) on the Objenesis instance, sending the results to the
    * reporter
    * 
    * @param objenesis Objenesis instance to test
    * @param reporter result are recorded in the reporter
    * @param description description of the ran suite
    * @param candidates property file containing a list of classes to test (key) and their
    *        description (value)
    */
   public static void runTest(Objenesis objenesis, Reporter reporter, String description,
      String candidates) {
      TCK tck = new TCK();
      tck.registerObjenesisInstance(objenesis, description);

      CandidateLoader candidateLoader = new CandidateLoader(tck, Main.class.getClassLoader(),
         new CandidateLoader.LoggingErrorHandler(System.err));

      try {
         candidateLoader.loadFromResource(Main.class, candidates);
      }
      catch(IOException e) {
         throw new RuntimeException(e);
      }

      tck.runTests(reporter);
   }

}
