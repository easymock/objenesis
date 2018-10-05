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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisSerializer;
import org.objenesis.ObjenesisStd;

import static org.junit.Assert.*;

/**
 * Integration test for Objenesis. Should pass successfully on every supported JVM for all Objenesis
 * interface implementation.
 *
 * @author Henri Tremblay
 */
public class ObjenesisTest {

   public static class JUnitReporter implements Reporter {

      private Candidate currentCandidate;

      @Override
      public void startTests(String platformDescription, Objenesis objenesisStandard, Objenesis objenesisSerializer) {
      }

      @Override
      public void startTest(Candidate candidate) {
         currentCandidate = candidate;
      }

      @Override
      public void result(Candidate.CandidateType type, boolean worked) {
         assertTrue("Instantiating " + currentCandidate + " for " + type + " failed", worked);
      }

      @Override
      public void exception(Candidate.CandidateType type, Exception exception) {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         try (PrintStream out = new PrintStream(buffer)) {
            out.println("Exception when instantiating " + currentCandidate + " for " + type + ": ");
            exception.printStackTrace(out);
            fail(buffer.toString());
         }
      }

      @Override
      public void endTests() {
      }

   }

   @Test
   public void test() {
      TCK tck = new TCK(new ObjenesisStd(), new ObjenesisSerializer(), new JUnitReporter());
      tck.runTests();
   }
}
