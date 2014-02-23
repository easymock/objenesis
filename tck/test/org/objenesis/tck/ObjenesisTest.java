/**
 * Copyright 2006-2014 the original author or authors.
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

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

import org.junit.Test;
import org.objenesis.ObjenesisSerializer;
import org.objenesis.ObjenesisStd;

/**
 * Integration test for Objenesis. Should pass successfully on every supported JVM for all Objenesis
 * interface implementation.
 * 
 * @author Henri Tremblay
 */
public class ObjenesisTest {

   public static class JUnitReporter implements Reporter {

      private String currentObjenesis;

      private String currentCandidate;

      public void startTests(String platformDescription, Map<String, Object> allCandidates,
         Map<String, Object> allInstantiators) {
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

   @Test
   public void testObjenesisStd() throws Exception {
      Main.runStandardTest(new ObjenesisStd(), new JUnitReporter());
   }

   @Test
   public void testObjenesisSerializer() throws Exception {
      Main.runSerializerTest(new ObjenesisSerializer(), new JUnitReporter());
   }

   @Test
   public void testObjenesisSerializerParentConstructorCalled() throws Exception {
      boolean result = Main.runParentConstructorTest(new ObjenesisSerializer());
      assertTrue(result);
   }
}
