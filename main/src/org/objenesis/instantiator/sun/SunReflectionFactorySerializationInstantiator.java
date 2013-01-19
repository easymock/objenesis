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

import java.io.NotSerializableException;
import java.lang.reflect.Constructor;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.instantiator.SerializationInstantiatorHelper;

/**
 * Instantiates an object using internal sun.reflect.ReflectionFactory - a class only available on
 * JDK's that use Sun's 1.4 (or later) Java implementation. This instantiator will create classes in
 * a way compatible with serialization, calling the first non-serializable superclass' no-arg
 * constructor. This is the best way to instantiate an object without any side effects caused by the
 * constructor - however it is not available on every platform.
 * 
 * @author Leonardo Mesquita
 * @see ObjectInstantiator
 */
public class SunReflectionFactorySerializationInstantiator implements ObjectInstantiator {

   private final Constructor mungedConstructor;

   public SunReflectionFactorySerializationInstantiator(Class type) {
	  Class nonSerializableAncestor = SerializationInstantiatorHelper.getNonSerializableSuperClass(type);
      
      Constructor nonSerializableAncestorConstructor;
      try {
         nonSerializableAncestorConstructor = nonSerializableAncestor
            .getConstructor((Class[]) null);
      }
      catch(NoSuchMethodException e) {
         throw new ObjenesisException(new NotSerializableException(type+" has no suitable superclass constructor"));         
      }

      mungedConstructor = SunReflectionFactoryHelper.newConstructorForSerialization(
         type, nonSerializableAncestorConstructor);
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
}
