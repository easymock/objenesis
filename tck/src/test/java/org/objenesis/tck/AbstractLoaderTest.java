/*
 * Copyright 2006-2021 the original author or authors.
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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Henri Tremblay
 * @author Joe Walnes
 */
public class AbstractLoaderTest {

   private StringBuilder recordedEvents;
   private AbstractLoader loader;

   @Before
   public void setUp() {
      recordedEvents = new StringBuilder();
      AbstractLoader.ErrorHandler errorHandler = name ->
         recordedEvents.append("classNotFound('").append(name).append("')\n");
      loader = new AbstractLoader(getClass().getClassLoader(), errorHandler) {
         @Override
         protected void handlePropertyEntry(Class<?> clazz, String description,
            Candidate.CandidateType type) {
            recordedEvents.append("registerCandidate('class " + clazz.getName() + "', '" + description + "')\n");
         }
      };
   }

   @Test
   public void testReadsClassesAndDescriptionsFromPropertiesFile() throws IOException {
      String input = "" + "org.objenesis.tck.AbstractLoaderTest$A = A candidate\n" + "\n"
         + "# a comment and some whitespace\n" + "\n"
         + "org.objenesis.tck.AbstractLoaderTest$B = B candidate\n"
         + "org.objenesis.tck.AbstractLoaderTest$C = C candidate\n";

      loader.loadFrom(new ByteArrayInputStream(input.getBytes()), Candidate.CandidateType.STANDARD);

      assertEquals(""
         + "registerCandidate('class org.objenesis.tck.AbstractLoaderTest$A', 'A candidate')\n"
         + "registerCandidate('class org.objenesis.tck.AbstractLoaderTest$B', 'B candidate')\n"
         + "registerCandidate('class org.objenesis.tck.AbstractLoaderTest$C', 'C candidate')\n",
         recordedEvents.toString());
   }

   @Test
   public void testReportsMissingClassesToErrorHandler() throws IOException {
      String input = "" + "org.objenesis.tck.AbstractLoaderTest$A = A candidate\n"
         + "org.objenesis.tck.AbstractLoaderTest$NonExistent = Dodgy candidate\n"
         + "org.objenesis.tck.AbstractLoaderTest$C = C candidate\n";

      loader.loadFrom(new ByteArrayInputStream(input.getBytes()), Candidate.CandidateType.STANDARD);

      assertEquals(""
         + "registerCandidate('class org.objenesis.tck.AbstractLoaderTest$A', 'A candidate')\n"
         + "classNotFound('org.objenesis.tck.AbstractLoaderTest$NonExistent')\n"
         + "registerCandidate('class org.objenesis.tck.AbstractLoaderTest$C', 'C candidate')\n",
         recordedEvents.toString());
   }

   @Test
   public void testLoadsFromResourceInClassPath() throws IOException {
      // See CandidateLoaderTest-sample.properties.

      loader.loadFromResource("org/objenesis/tck/CandidateLoaderTest-sample.properties", Candidate.CandidateType.STANDARD);

      assertEquals(""
         + "registerCandidate('class org.objenesis.tck.AbstractLoaderTest$A', 'A candidate')\n"
         + "registerCandidate('class org.objenesis.tck.AbstractLoaderTest$B', 'B candidate')\n",
         recordedEvents.toString());
   }

   @Test
   public void testThrowsIOExceptionIfResourceNotInClassPath() {
      assertThrows(IOException.class, () -> loader.loadFromResource( "Blatantly-Bogus.properties", Candidate.CandidateType.STANDARD));
   }

   // Sample classes.

   public static class A {
   }

   public static class B {
   }

   public static class C {
   }
}
