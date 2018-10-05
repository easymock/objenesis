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
package org.objenesis.tck;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test showcasing how the normal serialization should behave (constructor and special methods called). You
 * can use it to compare with an instantiator to see how it mimics it.
 *
 * @author Henri Tremblay
 */
public class SerializableTest {

   public static class IsSerializable implements Serializable {
      public IsSerializable() {
         called.add("IsSerializable.constructor");
      }
   }

   public static class ExtendsSerializable extends IsSerializable {
      public ExtendsSerializable() {
         called.add("ExtendsSerializable.constructor");
      }
   }

   public static class NotSerializable {
      public NotSerializable() {
         called.add("NotSerializable.constructor");
      }
   }

   public static class ExtendsNotSerializableButIs extends NotSerializable implements Serializable {
      public ExtendsNotSerializableButIs() {
         called.add("ExtendsNotSerializableButIs.constructor");
      }
   }

   public static class IsExternalizable implements Externalizable {

      public IsExternalizable() {
         called.add("IsExternalizable.constructor");
      }

      @Override
      public void writeExternal(ObjectOutput out) {
         called.add("IsExternalizable.write");
      }

      @Override
      public void readExternal(ObjectInput in) {
         called.add("IsExternalizable.read");
      }

      private Object writeReplace() {
         called.add("IsExternalizable.writeReplace");
         return this;
      }

      private Object readResolve() {
         called.add("IsExternalizable.readResolve");
         return this;
      }
   }

   public static class IsExternalizableAndSerializable implements Externalizable, Serializable {

      public IsExternalizableAndSerializable() {
         called.add("IsExternalizableAndSerializable.constructor");
      }

      @Override
      public void writeExternal(ObjectOutput out) {
         called.add("IsExternalizableAndSerializable.write");
      }

      @Override
      public void readExternal(ObjectInput in) {
         called.add("IsExternalizableAndSerializable.read");
      }
   }

   public static class IsExternalizableButExtends extends ExtendsNotSerializableButIs implements Externalizable {
      public IsExternalizableButExtends() {
         called.add("IsExternalizableButExtends.constructor");
      }

      @Override
      public void writeExternal(ObjectOutput out) {
         called.add("IsExternalizableButExtends.write");
      }

      @Override
      public void readExternal(ObjectInput in) {
         called.add("IsExternalizableButExtends.read");
      }
   }

   public static class ReadWriteObject implements Serializable {
      public ReadWriteObject() {
         called.add("ReadWriteObject.constructor");
      }

      // Write the object to the stream
      private void writeObject(java.io.ObjectOutputStream out) {
         called.add("ReadWriteObject.write");
      }

      // Read the object from the stream
      private void readObject(java.io.ObjectInputStream in) {
         called.add("ReadWriteObject.read");
      }
   }

   public static class ReadWriteReplace implements Serializable {
      public ReadWriteReplace() {
         called.add("ReadWriteReplace.constructor");
      }

      private Object writeReplace() {
         called.add("ReadWriteReplace.write");
         return this;
      }

      private Object readResolve() {
         called.add("ReadWriteReplace.read");
         return this;
      }
   }

   private static final Set<String> called = new HashSet<>();

   @After
   public void verify() {
      assertTrue(called.toString(), called.isEmpty());
   }

   @Test
   public void isSerializable() throws Exception {
      writeRead(new IsSerializable());
      assertNotCalled("IsSerializable.constructor");
   }

   @Test
   public void extendsSerializable() throws Exception {
      writeRead(new ExtendsSerializable());
      assertNotCalled("IsSerializable.constructor");
      assertNotCalled("ExtendsSerializable.constructor");
   }

   @Test
   public void notSerializable() throws Exception {
      try {
         writeRead(new NotSerializable());
         fail("Class is not serializable so can't be read");
      } catch(NotSerializableException e) {

      }

      assertNotCalled("NotSerializable.constructor");

   }

   @Test
   public void extendsNotSerializable() throws Exception {
      writeRead(new ExtendsNotSerializableButIs());
      assertCalled("NotSerializable.constructor");
      assertNotCalled("ExtendsNotSerializableButIs.constructor");
   }

   @Test
   public void isExternalizable() throws Exception {
      writeRead(new IsExternalizable());
      assertCalled("IsExternalizable.constructor");
      assertCalled("IsExternalizable.read");
      assertCalled("IsExternalizable.write");
      assertCalled("IsExternalizable.writeReplace");
      assertCalled("IsExternalizable.readResolve");
   }

   @Test
   public void isExternalizableAndSerializable() throws Exception {
      writeRead(new IsExternalizableAndSerializable());
      assertCalled("IsExternalizableAndSerializable.constructor");
      assertCalled("IsExternalizableAndSerializable.read");
      assertCalled("IsExternalizableAndSerializable.write");
   }

   @Test
   public void isExternalizableButExtends() throws Exception {
      writeRead(new IsExternalizableButExtends());
      assertCalled("NotSerializable.constructor");
      assertCalled("ExtendsNotSerializableButIs.constructor");
      assertCalled("IsExternalizableButExtends.constructor");
      assertCalled("IsExternalizableButExtends.read");
      assertCalled("IsExternalizableButExtends.write");
   }

   @Test
   public void readWriteObject() throws Exception {
      writeRead(new ReadWriteObject());
      assertNotCalled("ReadWriteObject.constructor");
      assertCalled("ReadWriteObject.read");
      assertCalled("ReadWriteObject.write");
   }

   @Test
   public void readWriteReplace() throws Exception {
      writeRead(new ReadWriteReplace());
      assertNotCalled("ReadWriteReplace.constructor");
      assertCalled("ReadWriteReplace.read");
      assertCalled("ReadWriteReplace.write");
   }

   private void assertCalled(String s) {
      assertTrue(called.remove(s));
   }

   private void assertNotCalled(String s) {
      assertFalse(called.remove(s));
   }

   private void writeRead(Object s) throws IOException, ClassNotFoundException {
      called.clear();
      ByteArrayOutputStream bOut = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(bOut);
      out.writeObject(s);
      out.close();
      byte[] buffer = bOut.toByteArray();
      ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buffer));
      in.readObject();
      in.close();
   }
}
