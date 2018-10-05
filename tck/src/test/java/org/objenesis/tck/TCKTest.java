/*
 * Copyright 2006-2018 the original author or authors.
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

import org.junit.Test;
import org.objenesis.Objenesis;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.tck.features.Feature;

import static org.junit.Assert.*;

/**
 * @author Joe Walnes
 * @author Henri Tremblay
 */
public class TCKTest {

   public static class StubbedInstantiator implements Objenesis {
      public <T> T newInstance(Class<T> clazz) {
         return null;
      }

      public <T> ObjectInstantiator<T> getInstantiatorOf(Class<T> clazz) {
         return null;
      }
   }

   @Test
   public void testReportsAllCandidatesAndInstantiatorCombinationsToReporter() {
      Reporter reporter = new RecordingReporter();

      // Given... a TCK with some candidate classes: A, B and C.
      TCK tck = new TCK(new StubbedInstantiator(), new StubbedInstantiator(), reporter) {
         @Override
         protected void loadCandidates() throws IOException {
            registerCandidate(CandidateA.class, "Candidate A", Candidate.CandidateType.STANDARD);
            registerCandidate(CandidateB.class, "Candidate B", Candidate.CandidateType.STANDARD);
            registerCandidate(CandidateB.class, "Candidate B", Candidate.CandidateType.SERIALIZATION);
            registerCandidate(CandidateC.class, "Candidate C", Candidate.CandidateType.STANDARD);

         }
      };

      // When... the TCK tests are run
      tck.runTests();

      // Expect... the reporter to have received a sequence of calls
      // notifying it of what the TCK is doing.
      assertEquals("" + "startTests()\n"
         + "startTest('Candidate A')\nresult(STANDARD, false)\n"
         + "startTest('Candidate B')\nresult(STANDARD, false)\nresult(SERIALIZATION, false)\n"
         + "startTest('Candidate C')\nresult(STANDARD, false)\n"
         + "endTests()\n", reporter.toString());
   }

   @Test
   public void testReportsSuccessIfCandidateCanBeInstantiated() {
      // When... the TCK tests are run
      Reporter reporter = new RecordingReporter();

      // Given... a TCK with some candidate classes: A, B and C.
      TCK tck = new TCK(new SelectiveInstantiator(), new SelectiveInstantiator(), reporter) {
         @Override
         protected void loadCandidates() throws IOException {
            registerCandidate(CandidateA.class, "Candidate A", Candidate.CandidateType.STANDARD);
            registerCandidate(CandidateA.class, "Candidate A", Candidate.CandidateType.SERIALIZATION);
            registerCandidate(CandidateB.class, "Candidate B", Candidate.CandidateType.STANDARD);
            registerCandidate(CandidateB.class, "Candidate B", Candidate.CandidateType.SERIALIZATION);
            registerCandidate(CandidateC.class, "Candidate C", Candidate.CandidateType.STANDARD);
            registerCandidate(CandidateD.class, "Candidate D", Candidate.CandidateType.SERIALIZATION);
         }
      };
      tck.runTests();

      // Expect... the reporter to be notified that A succeeded
      // but B failed.
      assertEquals("" + "startTests()\n"
         + "startTest('Candidate A')\nresult(STANDARD, true)\nresult(SERIALIZATION, true)\n"
         + "startTest('Candidate B')\nresult(STANDARD, false)\nresult(SERIALIZATION, false)\n"
         + "startTest('Candidate C')\nexception(STANDARD)\n"
         + "startTest('Candidate D')\nresult(SERIALIZATION, true)\n"
         + "endTests()\n", reporter.toString());
   }

   // Some sample classes used for testing.

   public static class SelectiveInstantiator implements Objenesis {
      public <T> T newInstance(Class<T> clazz) {
         if(clazz == CandidateA.class) {
            return clazz.cast(new CandidateA());
         }
         if(clazz == CandidateC.class) {
            throw new RuntimeException("fail");
         }
         return null;
      }

      public <T> ObjectInstantiator<T> getInstantiatorOf(Class<T> clazz) {
         return null;
      }
   }

   public static class CandidateA {
   }

   public static class CandidateB {
   }

   public static class CandidateC {
   }

   public static class CandidateD implements Feature {
      @Override
      public boolean isCompliant(Objenesis objenesis) {
         return true;
      }
   }

   /**
    * A poor man's mock. Using a recording test double to verify interactions between the TCK and
    * the Recorder.
    * <p>
    * Note: This test case could be simplified by using a mock object library. However, I wanted to simplify
    * dependencies - particularly as in the future, the mock libraries
    * may depend on objenesis - getting into an awkward cyclical dependency situation. -Joe.
    */
   private static class RecordingReporter implements Reporter {

      private final StringBuilder log = new StringBuilder();

      @Override
      public void startTests(String platformDescription, Objenesis objenesisStandard, Objenesis objenesisSerializer) {
         log.append("startTests()\n");
      }

      @Override
      public void startTest(Candidate candidate) {
         log.append("startTest('").append(candidate.getDescription()).append("')\n");
      }

      @Override
      public void result(Candidate.CandidateType type, boolean worked) {
         log.append("result(").append(type).append(", ").append(worked).append(")\n");
      }

      @Override
      public void exception(Candidate.CandidateType type, Exception exception) {
         log.append("exception(").append(type).append(")\n");
      }

      @Override
      public void endTests() {
         log.append("endTests()\n");
      }

      @Override
      public String toString() {
         return log.toString();
      }
   }
}
