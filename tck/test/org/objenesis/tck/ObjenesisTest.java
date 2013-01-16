/**
 * Copyright 2006-2013 the original author or authors.
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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Collection;

import junit.framework.TestCase;

import org.objenesis.ObjenesisSerializer;
import org.objenesis.ObjenesisStd;

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
   
   static class MockSuperClass {
	   private final boolean superConstructorCalled;
	   public MockSuperClass() {
		   superConstructorCalled = true;
	   }
	   public boolean isSuperConstructorCalled() {
		   return superConstructorCalled;
	   }
   }
   
   static class MockClass extends MockSuperClass implements Serializable {
      private static final long serialVersionUID = 1L;
      private final boolean constructorCalled;
	   public MockClass() {
		   super();
		   constructorCalled = true;
	   }
	   public boolean isConstructorCalled() {
		   return constructorCalled;
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
   	  Object result = new ObjenesisSerializer().newInstance(MockClass.class);
   	  assertEquals(MockClass.class, result.getClass());
   	  MockClass mockObject = (MockClass) result;
   	  assertTrue(mockObject.isSuperConstructorCalled());
   	  assertFalse(mockObject.isConstructorCalled());   	  
   }
}
