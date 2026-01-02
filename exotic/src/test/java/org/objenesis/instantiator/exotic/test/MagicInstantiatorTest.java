/*
 * Copyright 2006-2026 the original author or authors.
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
package org.objenesis.instantiator.exotic.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.instantiator.sun.MagicInstantiator;
import org.objenesis.test.EmptyClass;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.strategy.PlatformDescription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * This test is using the original sun.ProxyInstantiator do make sure the bridging to the new class is working.
 *
 * @author Henri Tremblay
 */
public class MagicInstantiatorTest {

   @BeforeEach
   public void before() {
      // I know it works on Hotspot and OpenJDK. Before JDK 9. Not sure on others
      assumeTrue((PlatformDescription.isThisJVM(PlatformDescription.HOTSPOT) || PlatformDescription.isThisJVM(PlatformDescription.OPENJDK))
         && !PlatformDescription.isAfterJigsaw()
      );
   }

   @Test
   public void testNewInstance() {
      ObjectInstantiator<EmptyClass> o1 = new MagicInstantiator<>(EmptyClass.class);
      assertEquals(EmptyClass.class, o1.newInstance().getClass());

      ObjectInstantiator<EmptyClass> o2 = new MagicInstantiator<>(EmptyClass.class);
      assertEquals(EmptyClass.class, o2.newInstance().getClass());
   }

}
