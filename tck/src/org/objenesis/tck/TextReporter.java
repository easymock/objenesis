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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Reports results from TCK as tabulated text, suitable for dumping to the console or a file and
 * being read by a human. If can be reused to provide a summary reports of different candidates as
 * long as the same objenesisDescription is not used twice.
 * 
 * @author Joe Walnes
 * @author Henri Tremblay
 * @see TCK
 * @see Reporter
 */
public class TextReporter implements Reporter {

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

   private final PrintStream summary;

   private final PrintStream log;

   private long startTime;

   private long totalTime = 0;

   private int errorCount = 0;

   private SortedSet<String> allCandidates = new TreeSet<String>();

   private SortedSet<String> allInstantiators = new TreeSet<String>();

   private String currentObjenesis;

   private String currentCandidate;

   private Map<String, Map<String, Result>> objenesisResults = new HashMap<String, Map<String, Result>>();

   private String platformDescription;

   /**
    * @param summary Output of main report.
    * @param log Any additional information, useful for diagnostics.
    */
   public TextReporter(PrintStream summary, PrintStream log) {
      this.summary = summary;
      this.log = log;
   }

   public void startTests(String platformDescription, Collection<String> allCandidates,
      Collection<String> allInstantiators) {

      // HT: in case the same reporter is reused, I'm guessing that it will
      // always be the
      // same platform
      this.platformDescription = platformDescription;
      this.allCandidates.addAll(allCandidates);
      this.allInstantiators.addAll(allInstantiators);

      for(Iterator<String> it = allInstantiators.iterator(); it.hasNext();) {
         objenesisResults.put(it.next(), new HashMap<String, Result>());
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
    */
   public void printResult(boolean parentConstructorTest) {
      // Platform
      summary.println("Running TCK on platform: " + platformDescription);
      summary.println();

      summary.println("Not serializable parent constructor called: "
         + (parentConstructorTest ? 'Y' : 'N'));
      summary.println();
      
      if(!parentConstructorTest) {
         errorCount++;
      }
      
      int maxObjenesisWidth = lengthOfLongestStringIn(allInstantiators);
      int maxCandidateWidth = lengthOfLongestStringIn(allCandidates);

      // Header
      summary.print(pad("", maxCandidateWidth) + ' ');
      for(Iterator<String> it = allInstantiators.iterator(); it.hasNext();) {
         String desc = it.next();
         summary.print(pad(desc, maxObjenesisWidth) + ' ');
      }
      summary.println();

      List<Result> exceptions = new ArrayList<Result>();

      // Candidates (and keep the exceptions meanwhile)
      for(Iterator<String> it = allCandidates.iterator(); it.hasNext();) {
         String candidateDesc = it.next();
         summary.print(pad(candidateDesc, maxCandidateWidth) + ' ');

         for(Iterator<String> itInst = allInstantiators.iterator(); itInst.hasNext();) {
            String instDesc = itInst.next();
            Result result = objenesisResults.get(instDesc).get(candidateDesc);
            if(result == null) {
               summary.print(pad("N/A", maxObjenesisWidth) + " ");
            }
            else {
               summary.print(pad(result.result ? "Y" : "n", maxObjenesisWidth) + " ");

               if(result.exception != null) {
                  exceptions.add(result);
               }
            }
         }
         summary.println();
      }

      summary.println();

      // Final
      if(errorCount != 0) {

         for(Iterator<Result> it = exceptions.iterator(); it.hasNext();) {
            Result element = it.next();
            log.println("--- Candidate '" + element.candidateDescription + "', Instantiator '"
               + element.objenesisDescription + "' ---");
            element.exception.printStackTrace(log);
            log.println();
         }

         log.println();

         summary.println("--- FAILED: " + errorCount + " error(s) occured ---");
      }
      else {
         summary.println("--- SUCCESSFUL: TCK tests passed without errors in " + totalTime + " ms");
      }

      summary.println();
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
      for(Iterator<String> it = descriptions.iterator(); it.hasNext();) {
         result = Math.max(result, it.next().length());
      }
      return result;
   }
}
