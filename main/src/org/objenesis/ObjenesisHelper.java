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
package org.objenesis;

import java.io.Serializable;

import org.objenesis.instantiator.ObjectInstantiator;

/**
 * Use Objenesis in a static way. <strong>It is strongly not recommended to use this class.</strong>
 * 
 * @author Henri Tremblay
 */
public final class ObjenesisHelper {

   private static final Objenesis OBJENESIS_STD = new ObjenesisStd();

   private static final Objenesis OBJENESIS_SERIALIZER = new ObjenesisSerializer();

   private ObjenesisHelper() {
   }

   /**
    * Will create a new object without any constructor being called
    * 
    * @param clazz Class to instantiate
    * @return New instance of clazz
    */
   public static Object newInstance(Class clazz) {
      return OBJENESIS_STD.newInstance(clazz);
   }

   /**
    * Will create an object just like it's done by ObjectInputStream.readObject (the default
    * constructor of the first non serializable class will be called)
    * 
    * @param clazz Class to instantiate
    * @return New instance of clazz
    */
   public static Serializable newSerializableInstance(Class clazz) {
      return (Serializable) OBJENESIS_SERIALIZER.newInstance(clazz);
   }

   /**
    * Will pick the best instantiator for the provided class. If you need to create a lot of
    * instances from the same class, it is way more efficient to create them from the same
    * ObjectInstantiator than calling {@link #newInstance(Class)}.
    * 
    * @param clazz Class to instantiate
    * @return Instantiator dedicated to the class
    */
   public static ObjectInstantiator getInstantiatorOf(Class clazz) {
      return OBJENESIS_STD.getInstantiatorOf(clazz);
   }

   /**
    * Same as {@link #getInstantiatorOf(Class)} but providing an instantiator emulating
    * ObjectInputStream.readObject behavior.
    * 
    * @see #newSerializableInstance(Class)
    * @param clazz Class to instantiate
    * @return Instantiator dedicated to the class
    */
   public static ObjectInstantiator getSerializableObjectInstantiatorOf(Class clazz) {
      return OBJENESIS_SERIALIZER.getInstantiatorOf(clazz);
   }
}
