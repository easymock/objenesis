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
package org.objenesis.instantiator.perc;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.instantiator.annotations.Instantiator;
import org.objenesis.instantiator.annotations.Typology;

/**
 * Instantiates a class by making a call to internal Perc private methods. It is only supposed to
 * work on Perc JVMs. This instantiator will create classes in a way compatible with serialization,
 * calling the first non-serializable superclass' no-arg constructor.
 * <p>
 * Based on code provided by Aonix but <b>doesn't work right now</b>
 *
 * @author Henri Tremblay
 * @see org.objenesis.instantiator.ObjectInstantiator
 */
@Instantiator(Typology.SERIALIZATION)
public class PercSerializationInstantiator<T> implements ObjectInstantiator<T> {

   private Object[] typeArgs;

   private final Method newInstanceMethod;

   public PercSerializationInstantiator(Class<T> type) {

      // Find the first unserializable parent class
      Class<? super T> unserializableType = type;

      while(Serializable.class.isAssignableFrom(unserializableType)) {
         unserializableType = unserializableType.getSuperclass();
      }

      try {
         // Get the special Perc method to call
         Class<?> percMethodClass = Class.forName("COM.newmonics.PercClassLoader.Method");

         newInstanceMethod = ObjectInputStream.class.getDeclaredMethod("noArgConstruct",
            new Class[] {Class.class, Object.class, percMethodClass});
         newInstanceMethod.setAccessible(true);

         // Create invoke params
         Class<?> percClassClass = Class.forName("COM.newmonics.PercClassLoader.PercClass");
         Method getPercClassMethod = percClassClass.getDeclaredMethod("getPercClass", Class.class);
         Object someObject = getPercClassMethod.invoke(null, unserializableType);
         Method findMethodMethod = someObject.getClass().getDeclaredMethod("findMethod",
            new Class[] {String.class});
         Object percMethod = findMethodMethod.invoke(someObject, "<init>()V");

         typeArgs = new Object[] {unserializableType, type, percMethod};

      }
      catch(ClassNotFoundException e) {
         throw new ObjenesisException(e);
      }
      catch(NoSuchMethodException e) {
         throw new ObjenesisException(e);
      }
      catch(InvocationTargetException e) {
         throw new ObjenesisException(e);
      }
      catch(IllegalAccessException e) {
         throw new ObjenesisException(e);
      }
   }

   @SuppressWarnings("unchecked")
   public T newInstance() {
      try {
         return (T) newInstanceMethod.invoke(null, typeArgs);
      }
      catch(IllegalAccessException e) {
         throw new ObjenesisException(e);
      }
      catch(InvocationTargetException e) {
         throw new ObjenesisException(e);
      }
   }

}
