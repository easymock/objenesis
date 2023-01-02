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
/*
 * Copyright 2006-2021 the original author or authors.
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

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Helper class extended by instantiators for which the implementation was moved to the exotic project.
 *
 * @param <T> type of the class instantiated
 * @author Henri Tremblay
 */
public abstract class DelegatingToExoticInstantiator<T> implements ObjectInstantiator<T> {

   private final ObjectInstantiator<T> wrapped;

   protected DelegatingToExoticInstantiator(String className, Class<T> type) {
      Class<ObjectInstantiator<T>> clazz = instantiatorClass(className);
      Constructor<ObjectInstantiator<T>> constructor = instantiatorConstructor(className, clazz);
      wrapped = instantiator(className, type, constructor);
   }

   private ObjectInstantiator<T> instantiator(String className, Class<T> type, Constructor<ObjectInstantiator<T>> constructor) {
      try {
         return constructor.newInstance(type);
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
         throw new RuntimeException("Failed to call constructor of " + className, e);
      }
   }

   private Class<ObjectInstantiator<T>> instantiatorClass(String className) {
      try {
         @SuppressWarnings("unchecked")
         Class<ObjectInstantiator<T>> clazz = (Class<ObjectInstantiator<T>>) Class.forName(className);
         return clazz;
      } catch (ClassNotFoundException e) {
         throw new ObjenesisException(getClass().getSimpleName() + " now requires objenesis-exotic to be in the classpath", e);
      }
   }

   private Constructor<ObjectInstantiator<T>> instantiatorConstructor(String className, Class<ObjectInstantiator<T>> clazz) {
      try {
         return clazz.getConstructor(Class.class);
      } catch (NoSuchMethodException e) {
         throw new ObjenesisException("Try to find constructor taking a Class<T> in parameter on " + className + " but can't find it", e);
      }
   }

   @Override
   public T newInstance() {
      return wrapped.newInstance();
   }
}
