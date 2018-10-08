/*
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
package org.objenesis.instantiator.util;

import org.objenesis.ObjenesisException;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;

/**
 * Helper class for to play with classes. It contains everything needed to play with a class
 * except the dodgy (Java 8) code you will find in {@link ClassDefinitionUtils}.
 *
 * @author Henri Tremblay
 */
public final class ClassUtils {

   private ClassUtils() { }

   /**
    * Will convert a class name to its name in the class definition format (e.g {@code org.objenesis.EmptyClass}
    * becomes {@code org/objenesis/EmptyClass})
    *
    * @param className full class name including the package
    * @return the internal name
    */
   public static String classNameToInternalClassName(String className) {
      return className.replace('.', '/');
   }

   /**
    * Will convert a class name to its class loader resource name (e.g {@code org.objenesis.EmptyClass}
    * becomes {@code org/objenesis/EmptyClass.class})
    *
    * @param className full class name including the package
    * @return the resource name
    */
   public static String classNameToResource(String className) {
      return classNameToInternalClassName(className) + ".class";
   }

   /**
    * Check if this class already exists in the class loader and return it if it does
    *
    * @param <T> type of the class returned
    * @param classLoader Class loader where to search the class
    * @param className Class name with full path
    * @return the class if it already exists or null
    */
   @SuppressWarnings("unchecked")
   public static <T> Class<T> getExistingClass(ClassLoader classLoader, String className) {
      try {
         return (Class<T>) Class.forName(className, true, classLoader);
      }
      catch (ClassNotFoundException e) {
         return null;
      }
   }

   @SuppressWarnings("deprecation")
   public static <T> T newInstance(Class<T> clazz) {
      try {
         return clazz.newInstance();
      } catch (InstantiationException | IllegalAccessException e) {
         throw new ObjenesisException(e);
      }
   }
}
