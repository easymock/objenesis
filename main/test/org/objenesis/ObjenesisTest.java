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
package org.objenesis;

import junit.framework.TestCase;

import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.strategy.InstantiatorStrategy;

/**
 * @author Henri Tremblay
 */
public class ObjenesisTest extends TestCase {

   public final void testObjenesis() {
      Objenesis o = new ObjenesisStd();
      assertEquals(
         "org.objenesis.ObjenesisStd using org.objenesis.strategy.StdInstantiatorStrategy with caching",
         o.toString());
   }

   public final void testObjenesis_WithoutCache() {
      Objenesis o = new ObjenesisStd(false);
      assertEquals(
         "org.objenesis.ObjenesisStd using org.objenesis.strategy.StdInstantiatorStrategy without caching",
         o.toString());

      assertEquals(o.getInstantiatorOf(getClass()).newInstance().getClass(), getClass());
   }

   public final void testNewInstance() {
      Objenesis o = new ObjenesisStd();
      assertEquals(getClass(), o.newInstance(getClass()).getClass());
   }

   public final void testGetInstantiatorOf() {
      Objenesis o = new ObjenesisStd();
      ObjectInstantiator i1 = o.getInstantiatorOf(getClass());
      // Test instance creation
      assertEquals(getClass(), i1.newInstance().getClass());

      // Test caching
      ObjectInstantiator i2 = o.getInstantiatorOf(getClass());
      assertSame(i1, i2);
   }

   public final void testToString() {
      Objenesis o = new ObjenesisStd() {};
      assertEquals(
         "org.objenesis.ObjenesisTest$1 using org.objenesis.strategy.StdInstantiatorStrategy with caching",
         o.toString());
   }
}

class MyStrategy implements InstantiatorStrategy {
   public ObjectInstantiator newInstantiatorOf(Class type) {
      return null;
   }
}
