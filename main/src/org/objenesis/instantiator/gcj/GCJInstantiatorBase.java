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
package org.objenesis.instantiator.gcj;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 * Base class for GCJ-based instantiators. It initializes reflection access to method
 * ObjectInputStream.newObject, as well as creating a dummy ObjectInputStream to be used as the
 * "this" argument for the method.
 * 
 * @author Leonardo Mesquita
 */
public abstract class GCJInstantiatorBase implements ObjectInstantiator {
   static Method newObjectMethod = null;
   static ObjectInputStream dummyStream;

   private static class DummyStream extends ObjectInputStream {
      public DummyStream() throws IOException {
      }
   }

   private static void initialize() {
      if(newObjectMethod == null) {
         try {
            newObjectMethod = ObjectInputStream.class.getDeclaredMethod("newObject", new Class[] {
               Class.class, Class.class});
            newObjectMethod.setAccessible(true);
            dummyStream = new DummyStream();
         }
         catch(RuntimeException e) {
            throw new ObjenesisException(e);
         }
         catch(NoSuchMethodException e) {
            throw new ObjenesisException(e);
         }
         catch(IOException e) {
            throw new ObjenesisException(e);
         }
      }
   }

   protected final Class type;

   public GCJInstantiatorBase(Class type) {
      this.type = type;
      initialize();
   }

   public abstract Object newInstance();
}
