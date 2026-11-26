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

import java.io.NotSerializableException;
import java.io.Serializable;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.sun.other.ProtectedConstructorParent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class SunReflectionFactorySerializationInstantiatorTest {

   public static class PrivateConstructorParent {
      public int value = 42;

      private PrivateConstructorParent() {
      }

      PrivateConstructorParent(int value) {
         this.value = value;
      }
   }

   public static class ExtendsPrivateConstructorParent extends PrivateConstructorParent implements Serializable {
      public ExtendsPrivateConstructorParent() {
         super(1);
         fail("ExtendsPrivateConstructorParent constructor shouldn't be called");
      }
   }

   public static class ExtendsProtectedConstructorParent extends ProtectedConstructorParent implements Serializable {
      public ExtendsProtectedConstructorParent() {
         fail("ExtendsProtectedConstructorParent constructor shouldn't be called");
      }
   }

   @Test
   public void newInstance_protectedParentConstructorInAnotherPackage() {
      SunReflectionFactorySerializationInstantiator<ExtendsProtectedConstructorParent> instantiator =
         new SunReflectionFactorySerializationInstantiator<>(ExtendsProtectedConstructorParent.class);
      // The constructor from ProtectedConstructorParent should have been called
      assertEquals(42, instantiator.newInstance().value);
   }

   @Test
   @EnabledForJreRange(maxVersion = 27)
   public void newInstance_privateParentConstructor() {
      SunReflectionFactorySerializationInstantiator<ExtendsPrivateConstructorParent> instantiator =
         new SunReflectionFactorySerializationInstantiator<>(ExtendsPrivateConstructorParent.class);
      // The constructor from PrivateConstructorParent should have been called
      assertEquals(42, instantiator.newInstance().value);
   }

   @Test
   @EnabledForJreRange(minVersion = 28)
   @DisabledIfSystemProperty(named = "jdk.reflect.allowNonSerializableConstruction", matches = "true")
   public void newInstance_privateParentConstructorSinceJava28() {
      ObjenesisException e = assertThrows(ObjenesisException.class,
         () -> new SunReflectionFactorySerializationInstantiator<>(ExtendsPrivateConstructorParent.class));
      assertEquals(NotSerializableException.class, e.getCause().getClass());
   }
}
