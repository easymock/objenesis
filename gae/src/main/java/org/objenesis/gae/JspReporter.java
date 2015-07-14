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
package main.java.org.objenesis.gae;

import org.objenesis.Objenesis;
import org.objenesis.tck.Reporter;
import org.objenesis.tck.TCK;

import javax.servlet.jsp.JspWriter;
import java.io.PrintWriter;
import java.util.*;

/**
 * Reports results from TCK in an HTML table.
 *
 * @author Henri Tremblay
 * @see TCK
 * @see Reporter
 */
public class JspReporter implements Reporter {

   private static class Result {

      String objenesisDescription;

      String candidateDescription;

      boolean result;

      Exception exception;

      /**
       * @param objenesisDescription Description of the tested Objenesis instance
       * @param candidateDescription Description of the tested candidate
       * @param result If the test is successful or not
       * @param exception Exception that might have occured during the test
       */
      public Result(String objenesisDescription, String candidateDescription, boolean result,
         Exception exception) {
         this.objenesisDescription = objenesisDescription;
         this.candidateDescription = candidateDescription;
         this.result = result;
         this.exception = exception;
      }
   }

   private final PrintWriter summary;

   private final PrintWriter log;

   private long startTime;

   private long totalTime = 0;

   private int errorCount = 0;

   private SortedMap<String, Object> allCandidates = new TreeMap<String, Object>();

   private SortedMap<String, Object> allInstantiators = new TreeMap<String, Object>();

   private String currentObjenesis;

   private String currentCandidate;

   private Map<Object, Map<String, Result>> objenesisResults = new HashMap<Object, Map<String, Result>>();

   private String platformDescription;

   /**
    * @param summary Output of main report.
    * @param log Any additional information, useful for diagnostics.
    */
   public JspReporter(JspWriter summary, JspWriter log) {
      this.summary = new PrintWriter(summary);
      this.log = new PrintWriter(log);
   }

   public void startTests(String platformDescription, Map<String, Object> allCandidates,
      Map<String, Object> allInstantiators) {

      // HT: in case the same reporter is reused, I'm guessing that it will
      // always be the same platform
      this.platformDescription = platformDescription;
      this.allCandidates.putAll(allCandidates);
      this.allInstantiators.putAll(allInstantiators);

      for(String desc : allInstantiators.keySet()) {
         objenesisResults.put(desc, new HashMap<String, Result>());
      }

      startTime = System.currentTimeMillis();
   }

   public void startTest(String candidateDescription, String objenesisDescription) {
      currentCandidate = candidateDescription;
      currentObjenesis = objenesisDescription;
   }

   public void result(boolean instantiatedObject) {
      if(!instantiatedObject) {
         errorCount++;
      }
      objenesisResults.get(currentObjenesis).put(currentCandidate,
         new Result(
         currentObjenesis, currentCandidate, instantiatedObject, null));
   }

   public void exception(Exception exception) {

      errorCount++;

      objenesisResults.get(currentObjenesis).put(currentCandidate,
         new Result(
         currentObjenesis, currentCandidate, false, exception));
   }

   public void endTest() {
   }

   public void endTests() {
      totalTime += System.currentTimeMillis() - startTime;
   }

   /**
    * Print the final summary report
    *
    * @param parentConstructorTest If the test checking that the none serializable constructor was called was successful
    */
   public void printResult(boolean parentConstructorTest) {
      // Platform
      summary.println("<p>Running TCK on platform: " + platformDescription + "</p>");

      // Instantiator implementations
      summary.println("<p>Instantiators used:<br>");
      for(Map.Entry<String, Object> o : allInstantiators.entrySet()) {
         String inst = ((Objenesis) o.getValue()).getInstantiatorOf(String.class).getClass()
            .getSimpleName();
         summary.println("   " + o.getKey() + ": " + inst + "<br>");
      }
      summary.println("</p>");

      // Parent constructor special test
      summary.println("<p>Not serializable parent constructor called: "
         + (parentConstructorTest ? 'Y' : 'N'));
      summary.println("</p>");

      if(!parentConstructorTest) {
         errorCount++;
      }

      Set<String> instantiators = this.allInstantiators.keySet();
      Set<String> candidates = this.allCandidates.keySet();

      int maxObjenesisWidth = lengthOfLongestStringIn(instantiators);
      int maxCandidateWidth = lengthOfLongestStringIn(candidates);

      // Strategy used
      summary.println("<table><tr><th></th>");
      for(String desc : instantiators) {
         summary.print("<th>" + desc + "</th>");
      }
      summary.println("</tr>");

      List<Result> exceptions = new ArrayList<Result>();

      // Candidates (and keep the exceptions meanwhile)
      for(String candidateDesc : candidates) {
         summary.print("<tr><td>" + candidateDesc + "</td>");

         for(String instDesc : instantiators) {
            Result result = objenesisResults.get(instDesc).get(candidateDesc);
            if(result == null) {
               summary.print("<td style=\"text-align: center\">N/A</td>");
            }
            else {
               summary.print("<td style=\"text-align: center\">" + (result.result ? "Y" : "n") + "</td>");

               if(result.exception != null) {
                  exceptions.add(result);
               }
            }
         }
      }

      summary.println("</table>");

      // Final
      if(errorCount != 0) {

         for(Result element : exceptions) {
            log.println("<pre>--- Candidate '" + element.candidateDescription + "', Instantiator '"
               + element.objenesisDescription + "' ---");
            element.exception.printStackTrace(log);
            log.println("</pre>");
         }

         log.println();

         summary.println("<p>--- FAILED: " + errorCount + " error(s) occured ---</p>");
      }
      else {
         summary.println("<p>--- SUCCESSFUL: TCK tests passed without errors in " + totalTime + " ms</p>");
      }
   }

   /**
    * Return true if the reporter has registered some errors
    *
    * @return if there was errors during execution
    */
   public boolean hasErrors() {
      return errorCount != 0;
   }

   private int lengthOfLongestStringIn(Collection<String> descriptions) {
      int result = 0;
      for(Iterator<String> it = descriptions.iterator(); it.hasNext();) {
         result = Math.max(result, it.next().length());
      }
      return result;
   }
}
