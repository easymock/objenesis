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

package org.objenesis;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

class CachingAcrossClassLoadersTest {

   @Test
   void testCachingAcrossClassLoaders() throws Exception {
      Objenesis shared = new ObjenesisStd();   // single shared instance across tenants

      // two class loaders inheriting the class loader that loaded ObjenesisStd
      // Each loads org.objenesis.Context from a different location (tenantA and tenantB directories)
      URL[] urls = new URL[] {new File("target/test-classes").toURI().toURL()};
      URLClassLoader clA = new URLClassLoader(urls, getClass().getClassLoader().getParent());
      URLClassLoader clB = new URLClassLoader(urls, getClass().getClassLoader().getParent());

      Class<?> classA = Class.forName("org.objenesis.Context", true, clA);
      Class<?> classB = Class.forName("org.objenesis.Context", true, clB);
      assertNotSame(classA, classB); // sanity check: different since they were loaded from different class loaders

      // Create an object for classA
      Object contextA = shared.newInstance(classA);
      assertSame(classA, contextA.getClass(), "Loaded from " + classA.getClassLoader() + ":" + contextA.getClass().getClassLoader()); // should be a classA instance

      // Create an object for classB
      Object contextB = shared.newInstance(classB);
      assertSame(classB, contextB.getClass(), "Loaded from " + classB.getClassLoader() + ":" + contextB.getClass().getClassLoader()); // should be a classB instance
   }
}

class Context {}
