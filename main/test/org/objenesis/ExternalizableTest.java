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
