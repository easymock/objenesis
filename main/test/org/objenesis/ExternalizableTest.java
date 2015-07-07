/**
 * Copyright 2006-2015 the original author or authors.
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

import org.junit.Test;

import java.io.*;
import java.util.ArrayList;

/**
 * This test makes sure an issue mentioned is not occurring.
 * @author Henri Tremblay
 */
public class ExternalizableTest {

    public static class A extends ArrayList<String> implements Externalizable {

        public A() {
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {

        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

        }
    }

    @Test
    public void test() {
        A a = ObjenesisHelper.newSerializableInstance(A.class);
        // This call should work because the ArrayList constructor as been called. This is required by A implementing Externalizable
        a.add("Test");
    }
}
