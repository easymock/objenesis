/*
 * Copyright 2006-2022 the original author or authors.
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

import org.objenesis.ObjenesisSerializer;
import org.objenesis.ObjenesisStd;

/**
 * Command line launcher for Technology Compatibility Kit (TCK).
 *
 * @author Joe Walnes
 * @author Henri Tremblay
 * @see TCK
 */
public class Main {

   /**
    * Main class of the TCK. Can also be called as a normal method from an application server.
    *
    * @param args No parameters are required
    */
   public static void main(String[] args) {

      TextReporter reporter = new TextReporter(System.out, System.err);

      run(reporter);

      if(reporter.hasErrors()) {
         System.exit(1);
      }
   }

   /**
    * Run the full test suite using standard Objenesis instances
    *
    * @param reporter result are recorded in the reporter
    */
   public static void run(Reporter reporter) {
      TCK tck = new TCK(new ObjenesisStd(), new ObjenesisSerializer(), reporter);
      tck.runTests();
   }

}
