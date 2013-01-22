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

package org.objenesis.tck.android;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.Bundle;

import org.objenesis.tck.Main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Wraps the Objenesis TCK so that it can be invoked on Android as an {@link Instrumentation}.
 *
 * @author Ian Parkinson (Google Inc.)
 */
public class TckInstrumentation extends Instrumentation {

   public void onCreate(Bundle arguments) {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      PrintStream printStream = new PrintStream(outputStream);
      System.setOut(printStream);

      try {
         Main.main(new String[0]);
      } catch (IOException e) {
         e.printStackTrace();
      }
    
      Bundle bundle = new Bundle();
      String fromStdout = outputStream.toString();
      bundle.putString(Instrumentation.REPORT_KEY_STREAMRESULT, fromStdout);
      finish(Activity.RESULT_OK, bundle);
   }
}

