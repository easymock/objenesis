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

import java.io.ObjectInputStream;
import java.lang.reflect.Method;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 * Base class for Sun 1.3 based instantiators. It initializes reflection access to static method
 * ObjectInputStream.allocateNewObject.
 * 
 * @author Leonardo Mesquita
 */
public abstract class Sun13InstantiatorBase implements ObjectInstantiator {
   static Method allocateNewObjectMethod = null;

   private static void initialize() {
      if(allocateNewObjectMethod == null) {
         try {
            allocateNewObjectMethod = ObjectInputStream.class.getDeclaredMethod(
               "allocateNewObject", new Class[] {Class.class, Class.class});
            allocateNewObjectMethod.setAccessible(true);
         }
         catch(RuntimeException e) {
            throw new ObjenesisException(e);
         }
         catch(NoSuchMethodException e) {
            throw new ObjenesisException(e);
         }
      }
   }

   protected final Class type;

   public Sun13InstantiatorBase(Class type) {
      this.type = type;
      initialize();
   }

   public abstract Object newInstance();

}
