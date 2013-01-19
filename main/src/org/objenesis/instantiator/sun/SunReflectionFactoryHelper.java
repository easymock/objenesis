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

package org.objenesis.instantiator.sun;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;


/**
 * Helper methods providing access to {@link sun.reflect.ReflectionFactory} via reflection, for use
 * by the {@link ObjectInstantiator}s that use it.
 * 
 * @author Henri Tremblay
 */
class SunReflectionFactoryHelper {

   public static Constructor newConstructorForSerialization(Class type, Constructor constructor) {
      Class reflectionFactoryClass = getReflectionFactoryClass();
      Object reflectionFactory = createReflectionFactory(reflectionFactoryClass);

      Method newConstructorForSerializationMethod = getNewConstructorForSerializationMethod(
         reflectionFactoryClass);

      try {
         return (Constructor) newConstructorForSerializationMethod.invoke(
            reflectionFactory, new Object[] {type, constructor});
      }
      catch(IllegalArgumentException e) {
         throw new ObjenesisException(e);
      }
      catch(IllegalAccessException e) {
         throw new ObjenesisException(e);
      }
      catch(InvocationTargetException e) {
         throw new ObjenesisException(e);
      }
   }

   private static Class getReflectionFactoryClass() {
      try {
         return Class.forName("sun.reflect.ReflectionFactory");
      }
      catch(ClassNotFoundException e) {
         throw new ObjenesisException(e);
      }
   }

   private static Object createReflectionFactory(Class reflectionFactoryClass) {
      try {
         Method method = reflectionFactoryClass.getDeclaredMethod(
            "getReflectionFactory", new Class[] {});
         return method.invoke(null, new Object[] {});
      }
      catch(NoSuchMethodException e) {
         throw new ObjenesisException(e);
      }
      catch(IllegalAccessException e) {
         throw new ObjenesisException(e);
      }
      catch(IllegalArgumentException e) {
         throw new ObjenesisException(e);
      }
      catch(InvocationTargetException e) {
         throw new ObjenesisException(e);
      }
   }

   private static Method getNewConstructorForSerializationMethod(Class reflectionFactoryClass) {
      try {
         return reflectionFactoryClass.getDeclaredMethod(
            "newConstructorForSerialization", new Class[] {Class.class, Constructor.class});
      }
      catch(NoSuchMethodException e) {
         throw new ObjenesisException(e);
      }
   }
}
