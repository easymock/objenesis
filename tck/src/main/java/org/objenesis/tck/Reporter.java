/**
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

import org.objenesis.Objenesis;

/**
 * Reports results from the TCK back to the user.
 * <p>
 * The sequence these methods are called is described below:
 *
 * <pre>
 * startTests(startTest(result | exception)) * endTests
 * </pre>
 *
 * @author Joe Walnes
 * @author Henri Tremblay
 * @see TCK
 * @see TextReporter
 */
public interface Reporter {

   /**
    * Report that the tests are starting. Provides information that is useful to be reported.
    *
    * @param platformDescription Description the platform being run on (i.e. JVM version, vendor, etc)
    * @param objenesisStandard Standard Objenesis instance used
    * @param objenesisSerializer Serialization Objenesis instance used
    */
   void startTests(String platformDescription, Objenesis objenesisStandard, Objenesis objenesisSerializer);

   /**
    * Report that a test between a candidate and an objenesis instance is about to start.
    *
    * @param candidate Starting to test this candidate.
    */
   void startTest(Candidate candidate);

   /**
    * Report details about what happened when performing an instantiation test or a serialization feature test.
    *
    * @param type type of test
    * @param worked Whether the test was successful or not
    */
   void result(Candidate.CandidateType type, boolean worked);

   /**
    * Report that something bad happened during the test.
    *
    * @param type type of test
    * @param exception Exception thrown
    */
   void exception(Candidate.CandidateType type, Exception exception);

   /**
    * Report that all tests have finished. Nothing will be called after this method.
    */
   void endTests();
}
