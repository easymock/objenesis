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
package org.objenesis.gae;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.objenesis.Objenesis;
import org.objenesis.tck.Candidate;
import org.objenesis.tck.Reporter;
import org.objenesis.tck.TCK;

import javax.servlet.jsp.JspWriter;

/**
 * Reports results from TCK in an HTML table.
 *
 * @author Henri Tremblay
 * @see TCK
 * @see Reporter
 */
public class JspReporter implements Reporter {

   private static class Result {

      final Candidate candidate;

      final Candidate.CandidateType type;

      final boolean result;

      final Exception exception;

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

   private final PrintWriter summary;

   private final PrintWriter log;

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
   public JspReporter(JspWriter summary, JspWriter log) {
      this.summary = new PrintWriter(summary);
      this.log = new PrintWriter(log);
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
      summary.println("<p>Running TCK on platform: " + platformDescription + "</p>");

      // Instantiator implementations
      summary.println("<p>Instantiators used:<br>");
      summary.println("   Objenesis standard: " + objenesisStandard.getInstantiatorOf(String.class).getClass().getName() + "<br>");
      summary.println("   Objenesis serializer: " + objenesisSerializer.getInstantiatorOf(String.class).getClass().getName() + "<br>");
      summary.println("</p>");

      Collection<String> candidateNames = new ArrayList<>();
      for(Map.Entry<Candidate, Map<Candidate.CandidateType, Result>> entry : results.entrySet()) {
         candidateNames.add(entry.getKey().getDescription());
      }

      // Strategy used
      summary.println("<table><tr><th></th>");
      summary.print("<th>Objenesis standard</th>");
      summary.print("<th>Objenesis serializer</th>");
      summary.println("</tr>");

      List<Result> exceptions = new ArrayList<>();

      // Candidates
      for(Map.Entry<Candidate, Map<Candidate.CandidateType, Result>> entry : results.entrySet()) {
         summary.print("<tr><td>" + entry.getKey().getDescription()  + "</td>");

         Result standardResult = entry.getValue().get(Candidate.CandidateType.STANDARD);
         Result serializationResult = entry.getValue().get(Candidate.CandidateType.SERIALIZATION);

         if(standardResult == null && serializationResult == null) {
            continue;
         }

         if(standardResult == null) {
            summary.print("<td style=\"text-align: center\">N/A</td>");
         }
         else {
            summary.print("<td style=\"text-align: center\">" + (standardResult.result ? "Y" : "n") + "</td>");

            if(standardResult.exception != null) {
               exceptions.add(standardResult);
            }
         }

         if(serializationResult == null) {
            summary.print("<td style=\"text-align: center\">N/A</td>");
         }
         else {
            summary.print("<td style=\"text-align: center\">" + (serializationResult.result ? "Y" : "n") + "</td>");

            if(serializationResult.exception != null) {
               exceptions.add(serializationResult);
            }
         }

         summary.println();
      }

      summary.println("</table>");

      // Final
      if(errorCount != 0) {

         for(Result element : exceptions) {
            log.println("<pre>--- Candidate '" + element.candidate.getDescription() + "', Type '" + element.type + "' ---");
            element.exception.printStackTrace(log);
            log.println("</pre>");
         }

         log.println();

         summary.println("<p>--- FAILED: " + errorCount + " error(s) occurred ---</p>");
      }
      else {
         summary.println("<p>--- SUCCESSFUL: TCK tests passed without errors in " + totalTime + " ms</p>");
      }
   }

}
