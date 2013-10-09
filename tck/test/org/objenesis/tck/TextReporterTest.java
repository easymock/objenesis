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

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisBase;
import org.objenesis.instantiator.basic.ConstructorInstantiator;
import org.objenesis.instantiator.basic.FailingInstantiator;
import org.objenesis.instantiator.basic.NullInstantiator;
import org.objenesis.strategy.SingleInstantiatorStrategy;

/**
 * @author Joe Walnes
 * @author Henri Tremblay
 */
public class TextReporterTest {

   private TextReporter textReporter;
   private ByteArrayOutputStream summaryBuffer;

   @Before
   public void setUp() throws Exception {
      summaryBuffer = new ByteArrayOutputStream();
      ByteArrayOutputStream logBuffer = new ByteArrayOutputStream();
      textReporter = new TextReporter(new PrintStream(summaryBuffer), new PrintStream(logBuffer));
   }

   @Test
   public void testReportsSuccessesInTabularFormat() {
      Map<String, Object> candidates = new HashMap<String, Object>();
      candidates.put("candidate A", "A");
      candidates.put("candidate B", "B");
      candidates.put("candidate C", "C");
      Map<String, Object> instantiators = new HashMap<String, Object>();
      
      Objenesis instantiator1 = new ObjenesisBase(new SingleInstantiatorStrategy(
         ConstructorInstantiator.class));
      Objenesis instantiator2 = new ObjenesisBase(new SingleInstantiatorStrategy(
         FailingInstantiator.class));
      Objenesis instantiator3 = new ObjenesisBase(new SingleInstantiatorStrategy(
         NullInstantiator.class));
      
      instantiators.put("instantiator1", instantiator1);
      instantiators.put("instantiator2", instantiator2);
      instantiators.put("instantiator3", instantiator3);

      textReporter.startTests("Some platform", candidates, instantiators);

      textReporter.startTest("candidate A", "instantiator1");
      textReporter.result(false);
      textReporter.startTest("candidate A", "instantiator2");
      textReporter.result(false);
      textReporter.startTest("candidate A", "instantiator3");
      textReporter.result(true);

      textReporter.startTest("candidate B", "instantiator1");
      textReporter.result(true);
      textReporter.startTest("candidate B", "instantiator2");
      textReporter.result(false);
      textReporter.startTest("candidate B", "instantiator3");
      textReporter.result(true);

      textReporter.startTest("candidate C", "instantiator1");
      textReporter.exception(new RuntimeException("Problem"));
      textReporter.startTest("candidate C", "instantiator2");
      textReporter.result(false);
      textReporter.startTest("candidate C", "instantiator3");
      textReporter.result(true);

      textReporter.endTests();

      textReporter.printResult(true);

      ByteArrayOutputStream expectedSummaryBuffer = new ByteArrayOutputStream();
      PrintStream out = new PrintStream(expectedSummaryBuffer);
      out.println("Running TCK on platform: Some platform");
      out.println();
      out.println("Instantiators used: ");
      out.println("   instantiator1: ConstructorInstantiator");
      out.println("   instantiator2: FailingInstantiator");
      out.println("   instantiator3: NullInstantiator");
      out.println();
      out.println("Not serializable parent constructor called: Y");
      out.println();
      out.println("            instantiator1 instantiator2 instantiator3 ");
      out.println("candidate A n             n             Y             ");
      out.println("candidate B Y             n             Y             ");
      out.println("candidate C n             n             Y             ");
      out.println();
      out.println("--- FAILED: 5 error(s) occured ---");
      out.println();

      assertEquals(expectedSummaryBuffer.toString(), summaryBuffer.toString());
   }

}
