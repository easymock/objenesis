/**
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
package org.objenesis.strategy;

import java.lang.reflect.Method;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Currently the test just check nothing is crashing. A more complex test should play with class
 * loading an properties
 *
 * @author Henri Tremblay
 */
public class PlatformDescriptionTest {

   @Test
   public void isJvmName() {
      PlatformDescription.isThisJVM(PlatformDescription.HOTSPOT);
   }

   @Test
   public void test() {
      if(!PlatformDescription.isThisJVM(PlatformDescription.DALVIK)) {
         assertEquals(0, PlatformDescription.ANDROID_VERSION);
      }
   }

   @Test
   public void testAndroidVersion() throws Exception {
      Method m = PlatformDescription.class.getDeclaredMethod("getAndroidVersion0");
      m.setAccessible(true);
      int actual = (Integer) m.invoke(null);
      assertEquals(42, actual);
   }

   @Test
   public void isAfterJigsaw() {
      PlatformDescription.isAfterJigsaw(); // just make sure it doesn't crash
   }
}
