/*
 * Copyright 2006-2024 the original author or authors.
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

import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * This test makes sure issue #33 is not occurring.
 *
 * @author Henri Tremblay
 */
public class ExternalizableTest {

   public static class C {

      public int val = 33;

      protected C() {}
   }

   public static class B extends C implements Serializable {
      public B() {
         fail("B constructor shouldn't be called");
      }
   }

   public static class A extends B implements Externalizable {

      public A() {
         fail("A constructor shouldn't be called");
      }

      public void writeExternal(ObjectOutput out) {

      }

      public void readExternal(ObjectInput in) {

      }
   }

   @Test
   public void test() {
      A a = ObjenesisHelper.newSerializableInstance(A.class);
      // The constructor from C should have been called
      assertEquals(33, a.val);
   }
}
