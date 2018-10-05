/*
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

import java.io.Serializable;

import org.objenesis.Objenesis;

/**
 * No-arg of the first none serializable parent class is called.
 *
 * @author Henri Tremblay
 */
public class ExtendsNotSerializableParentClass extends AbstractFeature {

   public static class ExtendsNotSerializable extends NotSerializableClass.NotSerializable implements Serializable {
      public ExtendsNotSerializable() {
         called.add(ExtendsNotSerializable.class.getSimpleName() + ".<init>");
      }
   }

   @Override
   public boolean isCompliant(Objenesis objenesis) {
      objenesis.newInstance(ExtendsNotSerializable.class);
      return called.size() == 1 && called.get(0).equals(NotSerializableClass.NotSerializable.class.getSimpleName() + ".<init>");
   }
}
