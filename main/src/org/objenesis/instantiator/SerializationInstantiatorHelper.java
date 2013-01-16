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
package org.objenesis.instantiator;

import java.io.Serializable;

/**
 * Helper for common serialization-compatible instantiation functions
 * 
 * @author Leonardo Mesquita
 */
public class SerializationInstantiatorHelper {

   /**
    * Returns the first non-serializable superclass of a given class. According to Java Object
    * Serialization Specification, objects read from a stream are initialized by calling an
    * accessible no-arg constructor from the first non-serializable superclass in the object's
    * hierarchy, allowing the state of non-serializable fields to be correctly initialized.
    * 
    * @param type Serializable class for which the first non-serializable superclass is to be found
    * @return The first non-serializable superclass of 'type'.
    * @see java.io.Serializable
    */
   public static Class getNonSerializableSuperClass(Class type) {
      Class result = type;
      while(Serializable.class.isAssignableFrom(result)) {
         result = result.getSuperclass();
         if(result == null) {
            throw new Error("Bad class hierarchy: No non-serializable parents");
         }
      }
      return result;

   }
}
