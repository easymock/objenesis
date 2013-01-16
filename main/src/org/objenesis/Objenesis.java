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
package org.objenesis;

import org.objenesis.instantiator.ObjectInstantiator;

/**
 * Common interface to all kind of Objenesis objects
 * 
 * @author Henri Tremblay
 */
public interface Objenesis {

   /**
    * Will create a new object without any constructor being called
    * 
    * @param clazz Class to instantiate
    * @return New instance of clazz
    */
   Object newInstance(Class clazz);

   /**
    * Will pick the best instantiator for the provided class. If you need to create a lot of
    * instances from the same class, it is way more efficient to create them from the same
    * ObjectInstantiator than calling {@link #newInstance(Class)}.
    * 
    * @param clazz Class to instantiate
    * @return Instantiator dedicated to the class
    */
   ObjectInstantiator getInstantiatorOf(Class clazz);
}
