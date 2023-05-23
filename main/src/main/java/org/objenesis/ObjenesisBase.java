/*
 * Copyright 2006-2023 the original author or authors.
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

import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.strategy.InstantiatorStrategy;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Base class to extend if you want to have a class providing your own default strategy. Can also be
 * instantiated directly.
 *
 * @author Henri Tremblay
 */
public class ObjenesisBase implements Objenesis {

   /** Strategy used by this Objenesis implementation to create classes */
   protected final InstantiatorStrategy strategy;

   /** Strategy cache. Key = Class, Value = InstantiatorStrategy */
   protected ConcurrentHashMap<String, ObjectInstantiator<?>> cache;

   /**
    * Constructor allowing to pick a strategy and using cache
    *
    * @param strategy Strategy to use
    */
   public ObjenesisBase(InstantiatorStrategy strategy) {
      this(strategy, true);
   }

   /**
    * Flexible constructor allowing to pick the strategy and if caching should be used
    *
    * @param strategy Strategy to use
    * @param useCache If {@link ObjectInstantiator}s should be cached
    */
   public ObjenesisBase(InstantiatorStrategy strategy, boolean useCache) {
      if(strategy == null) {
         throw new IllegalArgumentException("A strategy can't be null");
      }
      this.strategy = strategy;
      this.cache = useCache ? new ConcurrentHashMap<>() : null;
   }

   @Override
   public String toString() {
      return getClass().getName() + " using " + strategy.getClass().getName()
         + (cache == null ? " without" : " with") + " caching";
   }

   /**
    * Will create a new object without any constructor being called
    *
    * @param clazz Class to instantiate
    * @return New instance of clazz
    */
   public <T> T newInstance(Class<T> clazz) {
      return getInstantiatorOf(clazz).newInstance();
   }

   /**
    * Will pick the best instantiator for the provided class. If you need to create a lot of
    * instances from the same class, it is way more efficient to create them from the same
    * ObjectInstantiator than calling {@link #newInstance(Class)}.
    *
    * @param clazz Class to instantiate
    * @return Instantiator dedicated to the class
    */
   @SuppressWarnings("unchecked")
   public <T> ObjectInstantiator<T> getInstantiatorOf(Class<T> clazz) {
      if(clazz.isPrimitive()) {
         throw new IllegalArgumentException("Primitive types can't be instantiated in Java");
      }
      if(cache == null) {
         return strategy.newInstantiatorOf(clazz);
      }
      ObjectInstantiator<?> instantiator = cache.get(clazz.getName());
      if(instantiator == null) {
         ObjectInstantiator<?> newInstantiator = strategy.newInstantiatorOf(clazz);
         instantiator = cache.putIfAbsent(clazz.getName(), newInstantiator);
         if(instantiator == null) {
            instantiator = newInstantiator;
         }
      }
      return (ObjectInstantiator<T>) instantiator;
   }
}
