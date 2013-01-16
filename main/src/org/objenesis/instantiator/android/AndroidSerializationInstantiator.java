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
package org.objenesis.instantiator.android;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

import java.io.ObjectStreamClass;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * {@link ObjectInstantiator} for Android which creates objects using the constructor from the first
 * non-serializable parent class constructor, using internal methods on the Dalvik implementation of
 * {@link ObjectStreamClass}.
 * 
 * @author Ian Parkinson (Google Inc.)
 */
public class AndroidSerializationInstantiator implements ObjectInstantiator {
   private final Class type;
   private final ObjectStreamClass objectStreamClass;
   private final Method newInstanceMethod;

   public AndroidSerializationInstantiator(Class type) {
      this.type = type;
      newInstanceMethod = getNewInstanceMethod();
      objectStreamClass = ObjectStreamClass.lookupAny(type);
   }

   public Object newInstance() {
      try {
         return newInstanceMethod.invoke(objectStreamClass, new Object[] {type});
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
            "newInstance", new Class[] {Class.class});
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
