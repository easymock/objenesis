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
package org.objenesis.instantiator.sun;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.objenesis.ObjenesisException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("deprecation")
public class SunReflectionFactoryInstantiatorTest {

   public static class WithConstructor {
      public WithConstructor() {
         fail("The constructor shouldn't be called");
      }
   }

   @Test
   @EnabledForJreRange(maxVersion = 27)
   public void newInstance() {
      SunReflectionFactoryInstantiator<WithConstructor> instantiator =
         new SunReflectionFactoryInstantiator<>(WithConstructor.class);
      assertEquals(WithConstructor.class, instantiator.newInstance().getClass());
   }

   @Test
   @EnabledForJreRange(minVersion = 28)
   @DisabledIfSystemProperty(named = "jdk.reflect.allowNonSerializableConstruction", matches = "true")
   public void newInstance_disabledSinceJava28() {
      ObjenesisException e = assertThrows(ObjenesisException.class,
         () -> new SunReflectionFactoryInstantiator<>(WithConstructor.class));
      assertEquals(UnsupportedOperationException.class, e.getCause().getClass());
      assertTrue(e.getMessage().contains("UnsafeFactoryInstantiator"));
   }
}
