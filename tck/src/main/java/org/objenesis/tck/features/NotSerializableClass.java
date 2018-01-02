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
package org.objenesis.tck.features;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisException;

/**
 * Not serializable classes can't be instantiated.
 *
 * @author Henri Tremblay
 */
public class NotSerializableClass extends AbstractFeature {

   public static class NotSerializable {
      public NotSerializable() {
         called.add(NotSerializable.class.getSimpleName() + ".<init>");
      }
   }

   @Override
   public boolean isCompliant(Objenesis objenesis) {
      try {
         objenesis.newInstance(NotSerializable.class);
      }
      catch(ObjenesisException e) {
         return true;
      }
      return false;
   }
}
