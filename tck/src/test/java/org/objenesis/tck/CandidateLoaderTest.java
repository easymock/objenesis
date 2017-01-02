/**
 * Copyright 2006-2017 the original author or authors.
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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Joe Walnes
 */
public class CandidateLoaderTest {

   private StringBuilder recordedEvents;
   private CandidateLoader candidateLoader;

   @Before
   public void setUp() throws Exception {

      recordedEvents = new StringBuilder();
      TCK tck = new TCK() {
         @Override
         public void registerCandidate(Class<?> candidateClass, String description) {
            recordedEvents.append("registerCandidate('").append(candidateClass).append("', '")
               .append(description).append("')\n");
         }
      };
      CandidateLoader.ErrorHandler errorHandler = new CandidateLoader.ErrorHandler() {
         public void classNotFound(String name) {
            recordedEvents.append("classNotFound('").append(name).append("')\n");
         }
      };

      candidateLoader = new CandidateLoader(tck, getClass().getClassLoader(), errorHandler);
   }

   @Test
   public void testReadsClassesAndDescriptionsFromPropertiesFile() throws IOException {
      String input = "" + "org.objenesis.tck.CandidateLoaderTest$A = A candidate\n" + "\n"
         + "# a comment and some whitespace\n" + "\n"
         + "org.objenesis.tck.CandidateLoaderTest$B = B candidate\n"
         + "org.objenesis.tck.CandidateLoaderTest$C = C candidate\n";

      candidateLoader.loadFrom(new ByteArrayInputStream(input.getBytes()));

      assertEquals(""
         + "registerCandidate('class org.objenesis.tck.CandidateLoaderTest$A', 'A candidate')\n"
         + "registerCandidate('class org.objenesis.tck.CandidateLoaderTest$B', 'B candidate')\n"
         + "registerCandidate('class org.objenesis.tck.CandidateLoaderTest$C', 'C candidate')\n",
         recordedEvents.toString());
   }

   @Test
   public void testReportsMissingClassesToErrorHandler() throws IOException {
      String input = "" + "org.objenesis.tck.CandidateLoaderTest$A = A candidate\n"
         + "org.objenesis.tck.CandidateLoaderTest$NonExistant = Dodgy candidate\n"
         + "org.objenesis.tck.CandidateLoaderTest$C = C candidate\n";

      candidateLoader.loadFrom(new ByteArrayInputStream(input.getBytes()));

      assertEquals(""
         + "registerCandidate('class org.objenesis.tck.CandidateLoaderTest$A', 'A candidate')\n"
         + "classNotFound('org.objenesis.tck.CandidateLoaderTest$NonExistant')\n"
         + "registerCandidate('class org.objenesis.tck.CandidateLoaderTest$C', 'C candidate')\n",
         recordedEvents.toString());
   }

   @Test
   public void testLoadsFromResourceInClassPath() throws IOException {
      // See CandidateLoaderTest-sample.properties.

      candidateLoader.loadFromResource(getClass(), "CandidateLoaderTest-sample.properties");

      assertEquals(""
         + "registerCandidate('class org.objenesis.tck.CandidateLoaderTest$A', 'A candidate')\n"
         + "registerCandidate('class org.objenesis.tck.CandidateLoaderTest$B', 'B candidate')\n",
         recordedEvents.toString());
   }

   @Test
   public void testThrowsIOExceptionIfResourceNotInClassPath() throws IOException {
      try {
         candidateLoader.loadFromResource(getClass(), "Blatently-Bogus.properties");
         fail("Expected exception");
      }
      catch(IOException expectedException) {
         // Good!
      }
   }

   // Sample classes.

   public static class A {
   }

   public static class B {
   }

   public static class C {
   }
}
