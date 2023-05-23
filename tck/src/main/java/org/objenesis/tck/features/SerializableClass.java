/*
 * Copyright 2006-2023 the original author or authors.
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

import java.io.Serializable;

import org.objenesis.Objenesis;

/**
 * A serializable class can be instantiated without calling any constructor
 *
 * @author Henri Tremblay
 */
public class SerializableClass extends AbstractFeature {

   public static class IsSerializable implements Serializable {
      public IsSerializable() {
         called.add(Serializable.class.getSimpleName() + ".<init>");
      }
   }

   @Override
   public boolean isCompliant(Objenesis objenesis) {
      objenesis.newInstance(IsSerializable.class);
      return called.isEmpty();
   }
}
