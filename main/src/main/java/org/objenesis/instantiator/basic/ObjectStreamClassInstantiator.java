/**
 * Copyright 2006-2016 the original author or authors.
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
package org.objenesis.instantiator.basic;

import java.io.ObjectStreamClass;
import java.lang.reflect.Method;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 * Instantiates a class by using reflection to make a call to private method
 * ObjectStreamClass.newInstance, present in many JVM implementations. This instantiator will create
 * classes in a way compatible with serialization, calling the first non-serializable superclass'
 * no-arg constructor.
 * 
 * @author Leonardo Mesquita
 * @see ObjectInstantiator
 * @see java.io.Serializable
 */
public class ObjectStreamClassInstantiator<T> implements ObjectInstantiator<T> {

   private static Method newInstanceMethod;

   private static void initialize() {
      if(newInstanceMethod == null) {
         try {
            newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("newInstance");
            newInstanceMethod.setAccessible(true);
         }
         catch(RuntimeException e) {
            throw new ObjenesisException(e);
         }
         catch(NoSuchMethodException e) {
            throw new ObjenesisException(e);
         }         
      }
   }

   private final ObjectStreamClass objStreamClass;

   public ObjectStreamClassInstantiator(Class<T> type) {
      initialize();
      objStreamClass = ObjectStreamClass.lookup(type);
   }

   @SuppressWarnings("unchecked")
   public T newInstance() {
	   
      try {
         return (T) newInstanceMethod.invoke(objStreamClass);
      }
      catch(Exception e) {
         throw new ObjenesisException(e);
      }
      
   }

}
