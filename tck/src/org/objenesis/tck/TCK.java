/**
 * Copyright 2006-2015 the original author or authors.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objenesis.Objenesis;
import org.objenesis.strategy.PlatformDescription;

/**
 * <b>Technology Compatibility Kit</b> (TCK) for {@link Objenesis}s.
 * <p>
 * This TCK accepts a set of candidate classes (class it attempts to instantiate) and a set of
 * Objenesis implementations. It then tries instantiating every candidate with every Objenesis
 * implementations, reporting the results to a {@link Reporter}.
 * 
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

   private final List<Objenesis> objenesisInstances = new ArrayList<Objenesis>();
   private final List<Class<?>> candidates = new ArrayList<Class<?>>();
   private final Map<Object, String> descriptions = new HashMap<Object, String>();

   /**
    * Register a candidate class to attempt to instantiate.
    * 
    * @param candidateClass Class to attempt to instantiate
    * @param description Description of the class
    */
   public void registerCandidate(Class<?> candidateClass, String description) {
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

      for(Class<?> candidateClass : candidates) {
         String candidateDescription = descriptions.get(candidateClass);

         for(Objenesis objenesis : objenesisInstances) {
            String objenesisDescription = descriptions.get(objenesis);

            reporter.startTest(candidateDescription, objenesisDescription);

            runTest(reporter, candidateClass, objenesis);

            reporter.endTest();
         }
      }
      reporter.endTests();
   }

   private void runTest(Reporter reporter, Class<?> candidate, Objenesis objenesis) {
      try {
         Object instance = objenesis.newInstance(candidate);
         boolean success = instance != null && instance.getClass() == candidate;
         reporter.result(success);
      }
      catch(Exception e) {
         reporter.exception(e);
      }
   }

   /**
    * Return the human readable description for list of TCK items (Objenesis instances or test
    * candidates)
    * 
    * @param keys list of items for which we are searching for a description
    * @param descriptions all descriptions
    * @return map of items with their description. Will contain one entry per entry in the original
    *         key list
    */
   private Map<String, Object> findAllDescriptions(List<?> keys, Map<?, String> descriptions) {
      Map<String, Object> results = new HashMap<String, Object>(keys.size());
      for(Object o : keys) {
         results.put(descriptions.get(o), o);
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
      String desc = "Java " + PlatformDescription.SPECIFICATION_VERSION + " ("
         + "vendor=\"" + PlatformDescription.VENDOR + "\", "
         + "vendor version=" + PlatformDescription.VENDOR_VERSION + ", "
         + "JVM name=\"" + PlatformDescription.JVM_NAME + "\", "
         + "JVM version=" + PlatformDescription.VM_VERSION + ", "
         + "JVM info=" + PlatformDescription.VM_INFO;
      
      // Add the API level is it's an Android platform
      int androidVersion = PlatformDescription.ANDROID_VERSION;
      if(androidVersion != 0) {
         desc += ", API level=" + PlatformDescription.ANDROID_VERSION;
      }
      desc += ")";

      return desc;
   }

}
