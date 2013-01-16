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

/**
 * Reports results from the TCK back to the user.
 * <p>
 * The sequence these methods are called is described below:
 * </p>
 * 
 * <pre>
 * startTests(startObjenesis(result | exception) * endObjenesis) * endTests
 * </pre>
 * 
 * @author Joe Walnes
 * @see TCK
 * @see TextReporter
 */
public interface Reporter {

   /**
    * Report that the tests are starting. Provides information that is useful to be reported.
    * 
    * @param platformDescription Description the platform being run on (i.e. JVM version, vendor,
    *        etc).
    * @param allCandidates Descriptions (String) of all candidates being used in tests.
    * @param allObjenesisInstances Descriptions of all Objenesis instances being used in tests.
    */
   void startTests(String platformDescription, Collection allCandidates,
      Collection allObjenesisInstances);

   /**
    * Report that a test between a candidate and an objenesis instance if about to start.
    * 
    * @param candidateDescription Description of the candidate class.
    * @param objenesisDescription Description of the objenesis instance.
    */
   void startTest(String candidateDescription, String objenesisDescription);

   /**
    * Report details about what happened when an Objenesis instance tried to instantiate the current
    * candidate.
    * 
    * @param instantiatedObject Whether the ObjectInstantiator successfully instantiated the
    *        candidate class.
    */
   void result(boolean instantiatedObject);

   /**
    * Report that something bad happened during the test.
    * 
    * @param exception Exception thrown by instantiator.
    */
   void exception(Exception exception);

   /**
    * Report that tests have been completed for a particular Objenesis instance and candidate.
    */
   void endTest();

   /**
    * Report that all tests have finished. Nothing will be called after this method.
    */
   void endTests();
}
