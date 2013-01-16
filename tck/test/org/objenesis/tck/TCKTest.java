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

import java.util.Collection;

import junit.framework.TestCase;

import org.objenesis.Objenesis;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 * @author Joe Walnes
 * @author Henri Tremblay
 */
public class TCKTest extends TestCase {

   public static class StubbedInstantiator1 implements Objenesis {
      public Object newInstance(Class clazz) {
         return null;
      }

      public ObjectInstantiator getInstantiatorOf(Class clazz) {
         return null;
      }
   }

   public static class StubbedInstantiator2 implements Objenesis {
      public Object newInstance(Class clazz) {
         return null;
      }

      public ObjectInstantiator getInstantiatorOf(Class clazz) {
         return null;
      }
   }

   public void testReportsAllCandidatesAndInstantiatorCombinationsToReporter() {
      // Given... a TCK with some candidate classes: A, B and C.
      TCK tck = new TCK();

      tck.registerCandidate(CandidateA.class, "Candidate A");
      tck.registerCandidate(CandidateB.class, "Candidate B");
      tck.registerCandidate(CandidateC.class, "Candidate C");

      // And... two ObjectInstantiators registered
      tck.registerObjenesisInstance(new StubbedInstantiator1(), "Instantiator1");
      tck.registerObjenesisInstance(new StubbedInstantiator2(), "Instantiator2");

      // When... the TCK tests are run
      Reporter reporter = new RecordingReporter();
      tck.runTests(reporter);

      // Expect... the reporter to have received a sequence of calls
      // notifying it of what the TCK is doing.
      assertEquals("" + "startTests()\n" + "startTest('Candidate A', 'Instantiator1')\n"
         + "result(false)\n" + "endTest()\n" + "startTest('Candidate A', 'Instantiator2')\n"
         + "result(false)\n" + "endTest()\n" + "startTest('Candidate B', 'Instantiator1')\n"
         + "result(false)\n" + "endTest()\n" + "startTest('Candidate B', 'Instantiator2')\n"
         + "result(false)\n" + "endTest()\n" + "startTest('Candidate C', 'Instantiator1')\n"
         + "result(false)\n" + "endTest()\n" + "startTest('Candidate C', 'Instantiator2')\n"
         + "result(false)\n" + "endTest()\n" + "endTests()\n", reporter.toString());
   }

   public void testReportsSuccessIfCandidateCanBeInstantiated() {
      // Given... a TCK with some candidate classes: A, B and C.
      TCK tck = new TCK();

      tck.registerCandidate(CandidateA.class, "Candidate A");
      tck.registerCandidate(CandidateB.class, "Candidate B");

      // And... a single ObjectInsantiator registered that can instantiate
      // A but not B.
      tck.registerObjenesisInstance(new SelectiveInstantiator(), "instantiator");

      // When... the TCK tests are run
      Reporter reporter = new RecordingReporter();
      tck.runTests(reporter);

      // Expect... the reporter to be notified that A succeeded
      // but B failed.
      assertEquals("" + "startTests()\n" + "startTest('Candidate A', 'instantiator')\n" + // A
         "result(true)\n" + // true
         "endTest()\n" + "startTest('Candidate B', 'instantiator')\n" + // B
         "result(false)\n" + // false
         "endTest()\n" + "endTests()\n", reporter.toString());
   }

   // Some sample classes used for testing.

   public static class SelectiveInstantiator implements Objenesis {
      public Object newInstance(Class clazz) {
         return clazz == CandidateA.class ? new CandidateA() : null;
      }

      public ObjectInstantiator getInstantiatorOf(Class clazz) {
         return null;
      }
   }

   public static class CandidateA {
   }

   public static class CandidateB {
   }

   public static class CandidateC {
   }

   /**
    * A poor man's mock. Using a recording test double to verify interactions between the TCK and
    * the Recorder. <p/> Note: This test case could be simplified by using a mock object library.
    * However, I wanted to simplify dependencies - particularly as in the future, the mock libraries
    * may depend on objenesis - getting into an awkward cyclical dependency situation. -Joe.
    */
   private static class RecordingReporter implements Reporter {

      private final StringBuffer log = new StringBuffer();

      public void startTests(String platformDescription, Collection allCandidates,
         Collection allInstantiators) {
         log.append("startTests()\n");
      }

      public void startTest(String candidateDescription, String objenesisDescription) {
         log.append("startTest('").append(candidateDescription).append("', '").append(
            objenesisDescription).append("')\n");
      }

      public void result(boolean instantiatedObject) {
         log.append("result(").append(instantiatedObject).append(")\n");
      }

      public void exception(Exception exception) {
         log.append("exception()\n");
      }

      public void endTest() {
         log.append("endTest()\n");
      }

      public void endTests() {
         log.append("endTests()\n");
      }

      public String toString() {
         return log.toString();
      }
   }
}
