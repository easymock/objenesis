/*
 * Copyright 2006-2026 the original author or authors.
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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.objenesis.Objenesis;

/**
 * Reports results from TCK as tabulated text, suitable for dumping to the console or a file and
 * being read by a human.
 *
 * @author Joe Walnes
 * @author Henri Tremblay
 * @see TCK
 * @see Reporter
 */
public class TextReporter implements Reporter {

   private static class Result {

      private final Candidate candidate;

      private final Candidate.CandidateType type;

      private final boolean result;

      private final Exception exception;

      /**
       * @param candidate Candidate tested
       * @param type Type of test performed
       * @param result If the test is successful or not
       * @param exception Exception that might have occurred during the test
       */
      public Result(Candidate candidate, Candidate.CandidateType type, boolean result, Exception exception) {
         this.candidate = candidate;
         this.type = type;
         this.result = result;
         this.exception = exception;
      }
   }

   private final PrintStream summary;

   private final PrintStream log;

   private long startTime;

   private int errorCount;

   private Objenesis objenesisStandard;

   private Objenesis objenesisSerializer;

   private Candidate currentCandidate;

   private final Map<Candidate, Map<Candidate.CandidateType, Result>> results = new TreeMap<>();

   private String platformDescription;

   /**
    * @param summary Output of main report.
    * @param log Any additional information, useful for diagnostics.
    */
   public TextReporter(PrintStream summary, PrintStream log) {
      this.summary = summary;
      this.log = log;
   }

   @Override
   public void startTests(String platformDescription, Objenesis objenesisStandard, Objenesis objenesisSerializer) {
      this.platformDescription = platformDescription;
      this.objenesisStandard = objenesisStandard;
      this.objenesisSerializer = objenesisSerializer;
      this.currentCandidate = null;
      this.errorCount = 0;
      this.results.clear();
      this.startTime = System.currentTimeMillis();
   }

   @Override
   public void startTest(Candidate candidate) {
      currentCandidate = candidate;
   }

   @Override
   public void result(Candidate.CandidateType type, boolean success) {
      addResult(type, success, null);
   }

   @Override
   public void exception(Candidate.CandidateType type, Exception exception) {
      addResult(type, false, exception);
   }

   private void addResult(Candidate.CandidateType type, boolean success, Exception exception) {
      if(!success) {
         errorCount++;
      }
      Map<Candidate.CandidateType, Result> result = results.computeIfAbsent(currentCandidate, k -> new HashMap<>());
      result.put(type, new Result(currentCandidate, type, success, exception));
   }

   @Override
   public void endTests() {
      long totalTime = System.currentTimeMillis() - startTime;
      printResult(totalTime);
   }

   /**
    * Print the final summary report
    *
    * @param totalTime Time spent running the TCK
    */
   private void printResult(long totalTime) {
      // Platform
      summary.println("Running TCK on platform: " + platformDescription);
      summary.println();

      // Instantiator implementations
      summary.println("Instantiators used: ");
      summary.println("   Objenesis standard  : " + objenesisStandard.getInstantiatorOf(String.class).getClass().getName());
      summary.println("   Objenesis serializer: " + objenesisSerializer.getInstantiatorOf(String.class).getClass().getName());
      summary.println();

      Collection<String> candidateNames = new ArrayList<>();
      for(Map.Entry<Candidate, Map<Candidate.CandidateType, Result>> entry : results.entrySet()) {
         candidateNames.add(entry.getKey().getDescription());
      }

      int maxObjenesisWidth = "Objenesis serializer".length();
      int maxCandidateWidth = lengthOfLongestStringIn(candidateNames);

      // Strategy used
      summary.print(pad("", maxCandidateWidth) + ' ');
      summary.print(pad("Objenesis standard", maxObjenesisWidth) + ' ');
      summary.print(pad("Objenesis serializer", maxObjenesisWidth));
      summary.println();

      List<Result> exceptions = new ArrayList<>();

      // Candidates
      for(Map.Entry<Candidate, Map<Candidate.CandidateType, Result>> entry : results.entrySet()) {
         summary.print(pad(entry.getKey().getDescription(), maxCandidateWidth) + ' ');

         Result standardResult = entry.getValue().get(Candidate.CandidateType.STANDARD);
         Result serializationResult = entry.getValue().get(Candidate.CandidateType.SERIALIZATION);

         if(standardResult == null && serializationResult == null) {
            continue;
         }

         if(standardResult == null) {
            summary.print(pad("N/A", maxObjenesisWidth) + " ");
         }
         else {
            summary.print(pad(standardResult.result ? "Y" : "n", maxObjenesisWidth) + " ");

            if(standardResult.exception != null) {
               exceptions.add(standardResult);
            }
         }

         if(serializationResult == null) {
            summary.print(pad("N/A", maxObjenesisWidth));
         }
         else {
            summary.print(pad(serializationResult.result ? "Y" : "n", maxObjenesisWidth));

            if(serializationResult.exception != null) {
               exceptions.add(serializationResult);
            }
         }

         summary.println();
      }

      summary.println();

      // Final
      if(errorCount != 0) {

         for(Result element : exceptions) {
            log.println("--- Candidate '" + element.candidate.getDescription() + "', Type '" + element.type + "' ---");
            element.exception.printStackTrace(log);
            log.println();
         }

         log.println();

         summary.println("--- FAILED: " + errorCount + " error(s) occurred ---");
      }
      else {
         summary.println("--- SUCCESSFUL: TCK tests passed without errors in " + totalTime + " ms");
      }

      summary.println();
   }

   /**
    * Return true if the reporter has registered some errors
    *
    * @return if there was errors during execution
    */
   public boolean hasErrors() {
      return errorCount != 0;
   }

   private String pad(String text, int width) {
      if(text.length() == width) {
         return text;
      }
      else if(text.length() > width) {
         return text.substring(0, width);
      }
      else {
         StringBuilder padded = new StringBuilder(text);
         while(padded.length() < width) {
            padded.append(' ');
         }
         return padded.toString();
      }
   }

   private int lengthOfLongestStringIn(Collection<String> descriptions) {
      int result = 0;
      for(String s : descriptions) {
         result = Math.max(result, s.length());
      }
      return result;
   }
}
