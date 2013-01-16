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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.objenesis.Objenesis;

/**
 * <b>Technology Compatibility Kit</b> (TCK) for {@link Objenesis}s.
 * <p/>
 * This TCK accepts a set of candidate classes (class it attempts to instantiate) and a set of
 * Objenesis implementations. It then tries instantiating every candidate with every Objenesis
 * implementations, reporting the results to a {@link Reporter}.
 * <h3>Example usage</h3>
 * 
 * <pre>
 * TCK tck = new TCK();
 * // register candidate classes.
 * tck.registerCandidate(SomeClass.class, &quot;A basic class&quot;);
 * tck.registerCandidate(SomeEvil.class, &quot;Something evil&quot;);
 * tck.registerCandidate(NotEvil.class, &quot;Something nice&quot;);
 * // register Objenesis instances.
 * tck.registerObjenesisInstance(new ObjenesisStd(), &quot;Objenesis&quot;);
 * tck.registerObjenesisInstance(new ObjenesisSerializaer(), &quot;Objenesis for serialization&quot;);
 * // go!
 * Reporter reporter = new TextReporter(System.out, System.err);
 * tck.runTests(reporter);
 * </pre>
 * 
 * @author Joe Walnes
 * @see org.objenesis.instantiator.ObjectInstantiator
 * @see Reporter
 * @see Main
 */
public class TCK {

   private final List objenesisInstances = new ArrayList();
   private final List candidates = new ArrayList();
   private final Map descriptions = new HashMap();

   /**
    * Register a candidate class to attempt to instantiate.
    * 
    * @param candidateClass Class to attempt to instantiate
    * @param description Description of the class
    */
   public void registerCandidate(Class candidateClass, String description) {
      candidates.add(candidateClass);
      descriptions.put(candidateClass, description);
   }

   /**
    * Register an Objenesis instance to use when attempting to instantiate a class.
    * 
    * @param objenesis Tested Objenesis instance
    * @param description Description of the Objenesis instance
    */
   public void registerObjenesisInstance(Objenesis objenesis, String description) {
      objenesisInstances.add(objenesis);
      descriptions.put(objenesis, description);
   }

   /**
    * Run all TCK tests.
    * 
    * @param reporter Where to report the results of the test to.
    */
   public void runTests(Reporter reporter) {
      reporter.startTests(describePlatform(), findAllDescriptions(candidates, descriptions),
         findAllDescriptions(objenesisInstances, descriptions));

      for(Iterator i = candidates.iterator(); i.hasNext();) {
         Class candidateClass = (Class) i.next();
         String candidateDescription = (String) descriptions.get(candidateClass);

         for(Iterator j = objenesisInstances.iterator(); j.hasNext();) {
            Objenesis objenesis = (Objenesis) j.next();

            String objenesisDescription = (String) descriptions.get(objenesis);

            reporter.startTest(candidateDescription, objenesisDescription);

            runTest(reporter, candidateClass, objenesis, candidateDescription);

            reporter.endTest();
         }
      }
      reporter.endTests();
   }

   private void runTest(Reporter reporter, Class candidate, Objenesis objenesis,
      String candidateDescription) {
      try {
         Object instance = objenesis.newInstance(candidate);
         boolean success = instance != null && instance.getClass() == candidate;
         reporter.result(success);
      }
      catch(Exception e) {
         reporter.exception(e);
      }
   }

   private Collection findAllDescriptions(List keys, Map descriptions) {
      List results = new ArrayList(keys.size());
      for(int i = 0; i < keys.size(); i++) {
         results.add(descriptions.get(keys.get(i)));
      }
      return results;
   }

   /**
    * Describes the platform. Outputs Java version and vendor. To change this behavior, override
    * this method.
    * 
    * @return Description of the current platform
    */
   protected String describePlatform() {
      return "Java " + System.getProperty("java.specification.version") + " ("
         + System.getProperty("java.vm.vendor") + " " + System.getProperty("java.vm.name") + " "
         + System.getProperty("java.vm.version") + " " + System.getProperty("java.runtime.version")
         + ")";
   }

}
