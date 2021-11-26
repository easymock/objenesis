/*
 * Copyright 2006-2021 the original author or authors.
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

import org.junit.jupiter.api.Test;

import java.io.NotSerializableException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Henri Tremblay
 * @author Leonardo Mesquita
 */
public class SerializingInstantiatorTest {

   @Test
   public void testNotSerializable() {
      ObjenesisSerializer o = new ObjenesisSerializer();
      ObjenesisException exception = assertThrows(ObjenesisException.class, () -> o.newInstance(Object.class));
      assertEquals(exception.getCause().getClass(), NotSerializableException.class);
   }

}
