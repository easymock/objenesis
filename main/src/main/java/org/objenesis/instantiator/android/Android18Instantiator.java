/*
 * Copyright 2006-2025 the original author or authors.
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
package org.objenesis.instantiator.android;

import java.io.ObjectStreamClass;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.instantiator.annotations.Instantiator;
import org.objenesis.instantiator.annotations.Typology;

/**
 * Instantiator for Android API level 18 and higher. Same as the version 17 but the
 * <code>newInstance</code> now takes a long in parameter
 *
 * @author Henri Tremblay
 */
@Instantiator(Typology.STANDARD)
public class Android18Instantiator<T> implements ObjectInstantiator<T> {
   private final Class<T> type;
   private final Method newInstanceMethod;
   private final Long objectConstructorId;

   public Android18Instantiator(Class<T> type) {
      this.type = type;
      newInstanceMethod = getNewInstanceMethod();
      objectConstructorId = findConstructorIdForJavaLangObjectConstructor();
   }

   public T newInstance() {
      try {
         return type.cast(newInstanceMethod.invoke(null, type, objectConstructorId));
      }
      catch(Exception e) {
         throw new ObjenesisException(e);
      }
   }

   private static Method getNewInstanceMethod() {
      try {
         Method newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("newInstance",
            Class.class, Long.TYPE);
         newInstanceMethod.setAccessible(true);
         return newInstanceMethod;
      }
      catch(RuntimeException | NoSuchMethodException e) {
         throw new ObjenesisException(e);
      }
   }

   private static Long findConstructorIdForJavaLangObjectConstructor() {
      try {
         Method newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("getConstructorId",
            Class.class);
         newInstanceMethod.setAccessible(true);

         return (Long) newInstanceMethod.invoke(null, Object.class);
      }
      catch(RuntimeException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
         throw new ObjenesisException(e);
      }
   }
}
