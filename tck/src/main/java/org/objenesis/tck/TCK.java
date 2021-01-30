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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.objenesis.Objenesis;
import org.objenesis.strategy.PlatformDescription;
import org.objenesis.tck.features.Feature;

/**
 * <b>Technology Compatibility Kit</b> (TCK) for {@link Objenesis}s.
 * <p>
 * This TCK tests Objenesis implementations against a set of candidate classes (class it attempts to instantiate),
 * reporting the results to a {@link Reporter}.
 *
 * <h3>Example usage</h3>
 *
 * <pre>
 * TextReporter reporter = new TextReporter(System.out, System.err);
 * TCK tck = new TCK(new ObjenesisStd(), new ObjenesisSerializer(), reporter);
 * tck.runTests(reporter);
 * reporter.printResults();
 * </pre>
 *
 * @author Joe Walnes
 * @author Henri Tremblay
 * @see org.objenesis.instantiator.ObjectInstantiator
 * @see Reporter
 * @see Main
 */
public class TCK {

   private final Objenesis objenesisStandard;
   private final Objenesis objenesisSerializer;
   private final Reporter reporter;

   private final List<Candidate> candidates = new ArrayList<>();

   /**
    * @param objenesisStandard Objenesis instance used to instantiate classes the standard way (no constructor called)
    * @param objenesisSerializer Objenesis instance used to instantiate classes in a serialization compliant way (first not serializable constructor called)
    * @param reporter Where to report the results of the tests to
    */
   public TCK(Objenesis objenesisStandard, Objenesis objenesisSerializer, Reporter reporter) {
      this.objenesisStandard = objenesisStandard;
      this.objenesisSerializer = objenesisSerializer;
      this.reporter = reporter;

      try {
         loadCandidates();
      }
      catch(IOException e) {
         throw new RuntimeException(e);
      }

      Collections.sort(candidates);
   }

   protected void loadCandidates() throws IOException {
      CandidateLoader candidateLoader = new CandidateLoader(this, new CandidateLoader.LoggingErrorHandler(System.err));
      candidateLoader.loadFromResource("org/objenesis/tck/candidates/standard-candidates.properties",
         Candidate.CandidateType.STANDARD);
      candidateLoader.loadFromResource("org/objenesis/tck/candidates/serializable-candidates.properties",
         Candidate.CandidateType.SERIALIZATION);
   }

   /**
    * Register a candidate class to attempt to instantiate.
    *
    * @param candidateClass Class to attempt to instantiate
    * @param description Description of the class
    */
   public void registerCandidate(Class<?> candidateClass, String description, Candidate.CandidateType type) {
      Candidate candidate = new Candidate(candidateClass, description, type);
      int index = candidates.indexOf(candidate);
      if(index >= 0) {
         Candidate existingCandidate = candidates.get(index);
         if(!description.equals(existingCandidate.getDescription())) {
            throw new IllegalStateException("Two different descriptions for candidate " + candidateClass.getName());
         }
         existingCandidate.getTypes().add(type);
      }
      else {
         candidates.add(candidate);
      }
   }

   /**
    * Run all TCK tests.
    */
   public void runTests() {
      reporter.startTests(describePlatform(), objenesisStandard, objenesisSerializer);

      for(Candidate candidate : candidates) {
         reporter.startTest(candidate);

         if(candidate.getTypes().contains(Candidate.CandidateType.STANDARD)) {
            runTest(reporter, candidate.getClazz(), objenesisStandard, Candidate.CandidateType.STANDARD);
         }
         if(candidate.getTypes().contains(Candidate.CandidateType.SERIALIZATION)) {
            runTest(reporter, candidate.getClazz(), objenesisSerializer, Candidate.CandidateType.SERIALIZATION);
         }
      }

      reporter.endTests();
   }

   private void runTest(Reporter reporter, Class<?> candidate, Objenesis objenesis, Candidate.CandidateType type) {
      if(Feature.class.isAssignableFrom(candidate)) {
         runFeature(reporter, candidate, objenesis, type);
      }
      else {
         runCandidate(reporter, candidate, objenesis, type);
      }
   }

   private void runFeature(Reporter reporter, Class<?> clazz, Objenesis objenesis, Candidate.CandidateType type) {
      try {
         @SuppressWarnings("unchecked") Constructor<Feature> constructor = (Constructor<Feature>) clazz.getConstructor();
         Feature feature = constructor.newInstance();
         boolean compliant = feature.isCompliant(objenesis);
         reporter.result(type, compliant);
      }
      catch(Exception e) {
         reporter.exception(type, e);
      }
   }

   private void runCandidate(Reporter reporter, Class<?> candidate, Objenesis objenesis, Candidate.CandidateType type) {
      try {
         Object instance = objenesis.newInstance(candidate);
         boolean success = instance != null && instance.getClass() == candidate;
         reporter.result(type, success);
      }
      catch(Exception e) {
         reporter.exception(type, e);
      }
   }

   /**
    * Describes the platform. Outputs Java version and vendor. To change this behavior, override
    * this method.
    *
    * @return Description of the current platform
    */
   protected String describePlatform() {
      return PlatformDescription.describePlatform();
   }

}
