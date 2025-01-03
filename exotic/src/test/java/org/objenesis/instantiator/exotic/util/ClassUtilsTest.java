/*
 * Copyright 2006-2025 the original author or authors.
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
package org.objenesis.instantiator.exotic.util;

import org.junit.jupiter.api.Test;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.util.ClassUtils;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Henri Tremblay
 */
public class ClassUtilsTest {

   private final String className = "org.objenesis.test.EmptyClassBis";

   @Test
   public void testClassNameToInternalClassName() {
      String actual = ClassUtils.classNameToInternalClassName(className);
      assertEquals("org/objenesis/test/EmptyClassBis", actual);
   }

   @Test
   public void testClassNameToResource() {
      String actual = ClassUtils.classNameToResource(className);
      assertEquals("org/objenesis/test/EmptyClassBis.class", actual);
   }

   @Test
   public void testGetExistingClass_existing() {
      Class<?> actual = ClassUtils.getExistingClass(getClass().getClassLoader(),
         getClass().getName());
      assertSame(actual, getClass());
   }

   @Test
   public void testGetExistingClass_notExisting() {
      Class<?> actual = ClassUtils.getExistingClass(getClass().getClassLoader(), getClass().getName() + "$$$1");
      assertNull(actual);
   }

   @Test
   public void testNewInstance_noArgsConstructorPresent() {
      ArrayList<?> i = ClassUtils.newInstance(ArrayList.class);
      assertTrue(i.isEmpty());
   }

   @Test
   public void testNewInstance_noArgsConstructorAbsent() {
      ObjenesisException exception = assertThrows(ObjenesisException.class, () -> ClassUtils.newInstance(Integer.class));
      assertEquals(exception.getCause().getClass(), InstantiationException.class);
   }
}
