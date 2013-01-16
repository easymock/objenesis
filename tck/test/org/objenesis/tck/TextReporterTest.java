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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import junit.framework.TestCase;

/**
 * @author Joe Walnes
 * @author Henri Tremblay
 */
public class TextReporterTest extends TestCase {

   private TextReporter textReporter;
   private ByteArrayOutputStream summaryBuffer;

   protected void setUp() throws Exception {
      super.setUp();
      summaryBuffer = new ByteArrayOutputStream();
      ByteArrayOutputStream logBuffer = new ByteArrayOutputStream();
      textReporter = new TextReporter(new PrintStream(summaryBuffer), new PrintStream(logBuffer));
   }

   public void testReportsSuccessesInTabularFormat() {
      textReporter.startTests("Some platform", Arrays.asList(new String[] {"candidate A",
         "candidate B", "candidate C"}), Arrays.asList(new String[] {"instantiator1",
         "instantiator2", "instantiator3"}));

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
