/*
 * Copyright 2006-2026 the original author or authors.
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
package org.mymodule;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisSerializer;
import org.objenesis.ObjenesisStd;

import java.util.ArrayList;
import java.util.List;

public class App {
   public List<?> newObject() {
      Objenesis o = new ObjenesisStd(false);
      return o.newInstance(ArrayList.class);
   }

   public List<?> newSerializable() {
      Objenesis o = new ObjenesisSerializer(false);
      return o.newInstance(ArrayList.class);
   }

   public static void main(String[] args) {
      App app = new App();
      System.out.println(app.newObject());
      System.out.println(app.newSerializable());
   }
}
