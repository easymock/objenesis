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
package org.objenesis.instantiator.basic;

import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * @author Henri Tremblay
 */
public class ClassDefinitionUtilsTest {

   @Test
   public void testDefineClass() throws Exception {
      String className = "org.objenesis.EmptyClass";
      byte[] b = readClass();
      Class<?> c = ClassDefinitionUtils.defineClass(className, b, getClass().getClassLoader());
      assertEquals(c.getName(), className);
   }

   private byte[] readClass() throws Exception {
      byte[] b = new byte[1000];
      int length;
      InputStream in = getClass().getResourceAsStream("/org/objenesis/EmptyClass.class");
      try {
         length = in.read(b);
      }
      finally {
         in.close();
      }
      byte[] copy = new byte[length];
      System.arraycopy(b, 0, copy, 0, length);
      return copy;
   }
}
