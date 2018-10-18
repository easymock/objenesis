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
package org.objenesis.tck.android;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisSerializer;
import org.objenesis.ObjenesisStd;
import org.objenesis.tck.Candidate;
import org.objenesis.tck.Reporter;
import org.objenesis.tck.TCK;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * Test case running the entire tck on android.
 *
 * @author Henri Tremblay
 */
public class ObjenesisTest extends AndroidTestCase {

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
      public void exception(Candidate.CandidateType type, Exception exception) {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         try (PrintStream out = new PrintStream(buffer)) {
            out.println("Exception when instantiating " + currentCandidate + " for " + type + ": ");
            exception.printStackTrace(out);
            fail(buffer.toString());
         }
      }

      @Override
      public void result(Candidate.CandidateType type, boolean worked) {
         assertTrue("Instantiating " + currentCandidate + " for " + type + " failed", worked);
      }

      @Override
      public void endTests() {
      }

   }

   @SmallTest
   public void testObjenesis() throws Exception {
      TCK tck = new TCK(new ObjenesisStd(), new ObjenesisSerializer(), new JUnitReporter());
      tck.runTests();
   }
}
