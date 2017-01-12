/**
 * Copyright 2006-2017 the original author or authors.
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
 * {@link ObjectInstantiator} for Android which creates objects using the constructor from the first
 * non-serializable parent class constructor, using internal methods on the Dalvik implementation of
 * {@link ObjectStreamClass}.
 *
 * @author Ian Parkinson (Google Inc.)
 */
@Instantiator(Typology.SERIALIZATION)
public class AndroidSerializationInstantiator<T> implements ObjectInstantiator<T> {
   private final Class<T> type;
   private final ObjectStreamClass objectStreamClass;
   private final Method newInstanceMethod;

   public AndroidSerializationInstantiator(Class<T> type) {
      this.type = type;
      newInstanceMethod = getNewInstanceMethod();
      Method m = null;
      try {
         m = ObjectStreamClass.class.getMethod("lookupAny", Class.class);
      } catch (NoSuchMethodException e) {
         throw new ObjenesisException(e);
      }
      try {
         objectStreamClass = (ObjectStreamClass) m.invoke(null, type);
      } catch (IllegalAccessException e) {
         throw new ObjenesisException(e);
      } catch (InvocationTargetException e) {
         throw new ObjenesisException(e);
      }
   }

   public T newInstance() {
      try {
         return type.cast(newInstanceMethod.invoke(objectStreamClass, type));
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

   private static Method getNewInstanceMethod() {
      try {
         Method newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod(
            "newInstance", Class.class);
         newInstanceMethod.setAccessible(true);
         return newInstanceMethod;
      }
      catch(RuntimeException e) {
         throw new ObjenesisException(e);
      }
      catch(NoSuchMethodException e) {
         throw new ObjenesisException(e);
      }
   }
}
