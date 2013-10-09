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
package org.objenesis.strategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 * Strategy returning only one instantiator type. Useful if you know on which JVM Objenesis
 * will be used and want to specify it explicitly.
 *
 * @author Henri Tremblay
 */
public class SingleInstantiatorStrategy implements InstantiatorStrategy {

   private Constructor<?> constructor;

   /**
    * Create a strategy that will return always the same instantiator type. We assume this instantiator
    * has one constructor taking the class to instantiate in parameter.
    *
    * @param instantiator the instantiator type
    */
   public <T extends ObjectInstantiator<?>> SingleInstantiatorStrategy(Class<T> instantiator) {
      try {
         constructor = instantiator.getConstructor(Class.class);
      }
      catch(NoSuchMethodException e) {
         throw new ObjenesisException(e);
      }
   }

   @SuppressWarnings("unchecked")
   public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {
      try {
         return (ObjectInstantiator<T>) constructor.newInstance(type);
      } catch (InstantiationException e) {
         throw new ObjenesisException(e);
      } catch (IllegalAccessException e) {
         throw new ObjenesisException(e);
      } catch (InvocationTargetException e) {
         throw new ObjenesisException(e);
      }
   }
}
