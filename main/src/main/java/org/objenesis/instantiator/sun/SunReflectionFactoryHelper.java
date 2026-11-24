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
@SuppressWarnings("restriction")
class SunReflectionFactoryHelper {

   /** {@code null} until {@link #isLegacyNewConstructorForSerializationAvailable()} is called */
   private static volatile Boolean legacyNewConstructorForSerializationAvailable;

   @SuppressWarnings("unchecked")
   public static <T> Constructor<T> newConstructorForSerialization(Class<T> type,
      Constructor<?> constructor) {
      Class<?> reflectionFactoryClass = getReflectionFactoryClass();
      Object reflectionFactory = createReflectionFactory(reflectionFactoryClass);

      Method newConstructorForSerializationMethod = getNewConstructorForSerializationMethod(
         reflectionFactoryClass, Class.class, Constructor.class);

      try {
         return (Constructor<T>) newConstructorForSerializationMethod.invoke(
            reflectionFactory, type, constructor);
      }
      catch(InvocationTargetException e) {
         if(e.getCause() instanceof UnsupportedOperationException) {
            throw new ObjenesisException("ReflectionFactory.newConstructorForSerialization(Class, Constructor) is "
               + "disabled since Java 28. Use UnsafeFactoryInstantiator instead or temporarily run with "
               + "-Djdk.reflect.allowNonSerializableConstruction=true", e.getCause());
         }
         throw new ObjenesisException(e);
      }
      catch(IllegalArgumentException | IllegalAccessException e) {
         throw new ObjenesisException(e);
      }
   }

   /**
    * Returns a constructor allocating an instance of {@code type} and then calling the no-arg
    * constructor of its first non-serializable superclass. Unlike
    * {@link #newConstructorForSerialization(Class, Constructor)}, it fully follows the java
    * serialization specification. So this no-arg constructor must be accessible from
    * {@code type}.
    *
    * @param type the serializable class to instantiate
    * @param <T> type to instantiate
    * @return the constructor or null if {@code type} or its superclasses are not suitable
    */
   @SuppressWarnings("unchecked")
   public static <T> Constructor<T> newConstructorForSerialization(Class<T> type) {
      Class<?> reflectionFactoryClass = getReflectionFactoryClass();
      Object reflectionFactory = createReflectionFactory(reflectionFactoryClass);

      Method newConstructorForSerializationMethod = getNewConstructorForSerializationMethod(
         reflectionFactoryClass, Class.class);

      try {
         return (Constructor<T>) newConstructorForSerializationMethod.invoke(reflectionFactory, type);
      }
      catch(IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
         throw new ObjenesisException(e);
      }
   }

   /**
    * Tells if {@link #newConstructorForSerialization(Class, Constructor)} can be used on this JVM.
    * It is disabled by default since Java 28 and can be temporarily re-enabled with
    * {@code -Djdk.reflect.allowNonSerializableConstruction=true}. It will be removed in a future
    * Java version.
    *
    * @return if the 2-args newConstructorForSerialization can be used
    */
   public static boolean isLegacyNewConstructorForSerializationAvailable() {
      Boolean available = legacyNewConstructorForSerializationAvailable;
      if(available == null) {
         available = probeLegacyNewConstructorForSerialization();
         legacyNewConstructorForSerializationAvailable = available;
      }
      return available;
   }

   private static boolean probeLegacyNewConstructorForSerialization() {
      Class<?> reflectionFactoryClass = getReflectionFactoryClass();
      Object reflectionFactory = createReflectionFactory(reflectionFactoryClass);

      Method newConstructorForSerializationMethod;
      try {
         newConstructorForSerializationMethod = reflectionFactoryClass.getDeclaredMethod(
            "newConstructorForSerialization", Class.class, Constructor.class);
      }
      catch(NoSuchMethodException e) {
         return false; // removed from the JVM
      }

      try {
         // Cheap: the constructor is returned as is since it belongs to the instantiated class
         newConstructorForSerializationMethod.invoke(reflectionFactory, Object.class, Object.class.getConstructor());
         return true;
      }
      catch(InvocationTargetException e) {
         if(e.getCause() instanceof UnsupportedOperationException) {
            return false; // disabled, which is the default since Java 28
         }
         throw new ObjenesisException(e);
      }
      catch(NoSuchMethodException | IllegalAccessException | IllegalArgumentException e) {
         throw new ObjenesisException(e);
      }
   }

   private static Class<?> getReflectionFactoryClass() {
      try {
         return Class.forName("sun.reflect.ReflectionFactory");
      }
      catch(ClassNotFoundException e) {
         try {
            // try the more official version, which doesn't exist in Java 8
            return Class.forName("jdk.internal.reflect.ReflectionFactory");
         } catch (ClassNotFoundException ex) {
            throw new ObjenesisException("You might need to add the unsupported module with --add-modules jdk.unsupported", e);
         }
      }
   }

   private static Object createReflectionFactory(Class<?> reflectionFactoryClass) {
      try {
         Method method = reflectionFactoryClass.getDeclaredMethod(
            "getReflectionFactory");
         return method.invoke(null);
      }
      catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
         throw new ObjenesisException(e);
      }
   }

   private static Method getNewConstructorForSerializationMethod(Class<?> reflectionFactoryClass,
      Class<?>... parameterTypes) {
      try {
         return reflectionFactoryClass.getDeclaredMethod(
            "newConstructorForSerialization", parameterTypes);
      }
      catch(NoSuchMethodException e) {
         throw new ObjenesisException(e);
      }
   }
}
