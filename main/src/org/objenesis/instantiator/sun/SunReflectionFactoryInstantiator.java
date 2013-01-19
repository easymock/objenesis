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
package org.objenesis.instantiator.sun;

import java.lang.reflect.Constructor;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 * Instantiates an object, WITHOUT calling it's constructor, using internal
 * sun.reflect.ReflectionFactory - a class only available on JDK's that use Sun's 1.4 (or later)
 * Java implementation. This is the best way to instantiate an object without any side effects
 * caused by the constructor - however it is not available on every platform.
 * 
 * @author Joe Walnes
 * @see ObjectInstantiator
 */
public class SunReflectionFactoryInstantiator implements ObjectInstantiator {

   private final Constructor mungedConstructor;

   public SunReflectionFactoryInstantiator(Class type) {
      Constructor javaLangObjectConstructor = getJavaLangObjectConstructor();
      mungedConstructor = SunReflectionFactoryHelper.newConstructorForSerialization(
          type, javaLangObjectConstructor);
      mungedConstructor.setAccessible(true);
   }

   public Object newInstance() {
      try {
         return mungedConstructor.newInstance((Object[]) null);
      }
      catch(Exception e) {
         throw new ObjenesisException(e);
      }
   }

   private static Constructor getJavaLangObjectConstructor() {
      try {
         return Object.class.getConstructor((Class[]) null);
      }
      catch(NoSuchMethodException e) {
         throw new ObjenesisException(e);
      }
   }
}
