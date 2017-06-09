/**
 * Copyright 2006-2017 the original author or authors.
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
package org.objenesis.instantiator.sun;

import org.junit.Before;
import org.junit.Test;
import org.objenesis.EmptyClass;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.strategy.PlatformDescription;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 * @author Henri Tremblay
 */
public class MagicInstantiatorTest {

   @Before
   public void before() {
      // I know it works on Hotspot and OpenJDK. Before JDK 9. Not sure on others
      assumeTrue((PlatformDescription.isThisJVM(PlatformDescription.HOTSPOT) || PlatformDescription.isThisJVM(PlatformDescription.OPENJDK))
         && !PlatformDescription.SPECIFICATION_VERSION.equals("9"));
   }

   @Test
   public void testNewInstance() throws Exception {
      ObjectInstantiator<EmptyClass> o1 = new MagicInstantiator<EmptyClass>(EmptyClass.class);
      assertEquals(EmptyClass.class, o1.newInstance().getClass());

      ObjectInstantiator<EmptyClass> o2 = new MagicInstantiator<EmptyClass>(EmptyClass.class);
      assertEquals(EmptyClass.class, o2.newInstance().getClass());
   }

   @Test
   public void testInternalInstantiator() {
      ObjectInstantiator<EmptyClass> o1 = new MagicInstantiator<EmptyClass>(EmptyClass.class).getInstantiator();
      assertEquals(EmptyClass.class, o1.newInstance().getClass());
   }
}
