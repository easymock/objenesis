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
package org.objenesis.tck.features;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import org.objenesis.Objenesis;

/**
 * {@code Serializable} special methods should not be called during instantiation.
 *
 * @author Henri Tremblay
 */
public class ReadObjectNotCalled extends AbstractFeature {

   public static class ReadObjectAndAll implements Serializable {
      private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         called.add("readObject");
      }

      private Object readResolve() throws ObjectStreamException {
         called.add("readResolve");
         return this;
      }

      private void readObjectNoData() throws ObjectStreamException {
         called.add("readObjectNoData");
      }
   }

   @Override
   public boolean isCompliant(Objenesis objenesis) {
      objenesis.newInstance(ReadObjectAndAll.class);
      return called.isEmpty();
   }
}
