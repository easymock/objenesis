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
package org.objenesis.tck.android;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collection;

import org.objenesis.ObjenesisSerializer;
import org.objenesis.ObjenesisStd;
import org.objenesis.tck.Main;
import org.objenesis.tck.Reporter;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * Test case running the entire tck on android.
 * 
 * @author Henri Tremblay
 */
public class ObjenesisTest extends AndroidTestCase {

   public static class JUnitReporter implements Reporter {

      private String currentObjenesis;

      private String currentCandidate;

      public void startTests(String platformDescription, Collection<String> allCandidates,
         Collection<String> allInstantiators) {
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

   @SmallTest
   public void testObjenesisStd() throws Exception {
      Main.runStandardTest(new ObjenesisStd(), new JUnitReporter());
   }

   @SmallTest
   public void testObjenesisSerializer() throws Exception {
      Main.runSerializerTest(new ObjenesisSerializer(), new JUnitReporter());
   }

   @SmallTest
   public void testObjenesisSerializerParentConstructorCalled() throws Exception {
      boolean result = Main.runParentConstructorTest(new ObjenesisSerializer());
      assertTrue(result);
   }

}