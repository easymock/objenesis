/*
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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisBase;
import org.objenesis.instantiator.basic.ConstructorInstantiator;
import org.objenesis.instantiator.basic.FailingInstantiator;
import org.objenesis.strategy.SingleInstantiatorStrategy;

import static org.junit.Assert.*;

/**
 * @author Joe Walnes
 * @author Henri Tremblay
 */
public class TextReporterTest {

   private TextReporter textReporter;
   private ByteArrayOutputStream summaryBuffer;

   @Before
   public void setUp() {
      summaryBuffer = new ByteArrayOutputStream();
      ByteArrayOutputStream logBuffer = new ByteArrayOutputStream();
      textReporter = new TextReporter(new PrintStream(summaryBuffer), new PrintStream(logBuffer));
   }

   @Test
   public void testReportsSuccessesInTabularFormat() {
      Objenesis instantiator1 = new ObjenesisBase(new SingleInstantiatorStrategy(
         ConstructorInstantiator.class));
      Objenesis instantiator2 = new ObjenesisBase(new SingleInstantiatorStrategy(
         FailingInstantiator.class));

      textReporter.startTests("Some platform", instantiator1, instantiator2);

      textReporter.startTest(new Candidate(TCKTest.CandidateA.class, "candidate A",
         Candidate.CandidateType.STANDARD, Candidate.CandidateType.SERIALIZATION));
      textReporter.result(Candidate.CandidateType.STANDARD,false);
      textReporter.result(Candidate.CandidateType.SERIALIZATION, true);

      textReporter.startTest(new Candidate(TCKTest.CandidateB.class, "candidate B", Candidate.CandidateType.STANDARD));
      textReporter.result(Candidate.CandidateType.STANDARD,true);

      textReporter.startTest(new Candidate(TCKTest.CandidateC.class,"candidate C",
         Candidate.CandidateType.STANDARD, Candidate.CandidateType.SERIALIZATION));
      textReporter.exception(Candidate.CandidateType.STANDARD, new RuntimeException("Problem"));
      textReporter.result(Candidate.CandidateType.SERIALIZATION, false);

      textReporter.startTest(new Candidate(TCKTest.CandidateD.class,"candidate D",
         Candidate.CandidateType.SERIALIZATION));
      textReporter.result(Candidate.CandidateType.SERIALIZATION, true);

      textReporter.endTests();

      ByteArrayOutputStream expectedSummaryBuffer = new ByteArrayOutputStream();
      PrintStream out = new PrintStream(expectedSummaryBuffer);
      out.println("Running TCK on platform: Some platform");
      out.println();
      out.println("Instantiators used: ");
      out.println("   Objenesis standard  : org.objenesis.instantiator.basic.ConstructorInstantiator");
      out.println("   Objenesis serializer: org.objenesis.instantiator.basic.FailingInstantiator");
      out.println();
      out.println("            Objenesis standard   Objenesis serializer");
      out.println("candidate A n                    Y                   ");
      out.println("candidate B Y                    N/A                 ");
      out.println("candidate C n                    n                   ");
      out.println("candidate D N/A                  Y                   ");
      out.println();
      out.println("--- FAILED: 3 error(s) occurred ---");
      out.println();

      assertEquals(expectedSummaryBuffer.toString(), summaryBuffer.toString());
   }

}
