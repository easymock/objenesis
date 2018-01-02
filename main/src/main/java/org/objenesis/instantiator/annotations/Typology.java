/**
 * Copyright 2006-2018 the original author or authors.
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
package org.objenesis.instantiator.annotations;

/**
 * Possible types of instantiator
 * @author Henri Tremblay
 */
public enum Typology {
   /**
    * Mark an instantiator used for standard instantiation (not calling a constructor).
    */
   STANDARD,

   /**
    * Mark an instantiator used for serialization.
    */
   SERIALIZATION,

   /**
    * Mark an instantiator that doesn't behave like a {@link #STANDARD} nor a {@link #SERIALIZATION} (e.g. calls a constructor, fails
    * all the time, etc.)
    */
   NOT_COMPLIANT,

   /**
    * No type specified on the instantiator class
    */
   UNKNOWN
}
