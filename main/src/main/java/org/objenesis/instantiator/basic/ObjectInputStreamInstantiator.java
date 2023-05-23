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
package org.objenesis.instantiator.basic;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.ObjectStreamConstants;
import java.io.Serializable;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.instantiator.annotations.Instantiator;
import org.objenesis.instantiator.annotations.Typology;

/**
 * Instantiates a class by using a dummy input stream that always feeds data for an empty object of
 * the same kind. NOTE: This instantiator may not work properly if the class being instantiated
 * defines a "readResolve" method, since it may return objects that have been returned previously
 * (i.e., there's no guarantee that the returned object is a new one), or even objects from a
 * completely different class.
 *
 * @author Leonardo Mesquita
 * @see org.objenesis.instantiator.ObjectInstantiator
 */
@Instantiator(Typology.SERIALIZATION)
public class ObjectInputStreamInstantiator<T> implements ObjectInstantiator<T> {
   private static class MockStream extends InputStream {

      private int pointer;
      private byte[] data;
      private int sequence;
      private static final int[] NEXT = new int[] {1, 2, 2};
      private final byte[][] buffers;

      private static byte[] HEADER;
      private static byte[] REPEATING_DATA;

      static {
         initialize();
      }

      private static void initialize() {
         try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(byteOut);
            dout.writeShort(ObjectStreamConstants.STREAM_MAGIC);
            dout.writeShort(ObjectStreamConstants.STREAM_VERSION);
            HEADER = byteOut.toByteArray();

            byteOut = new ByteArrayOutputStream();
            dout = new DataOutputStream(byteOut);

            dout.writeByte(ObjectStreamConstants.TC_OBJECT);
            dout.writeByte(ObjectStreamConstants.TC_REFERENCE);
            dout.writeInt(ObjectStreamConstants.baseWireHandle);
            REPEATING_DATA = byteOut.toByteArray();
         }
         catch(IOException e) {
            throw new Error("IOException: " + e.getMessage());
         }

      }

      public MockStream(Class<?> clazz) {
         this.pointer = 0;
         this.sequence = 0;
         this.data = HEADER;

         // (byte) TC_OBJECT
         // (byte) TC_CLASSDESC
         // (short length)
         // (byte * className.length)
         // (long)serialVersionUID
         // (byte) SC_SERIALIZABLE
         // (short)0 <fields>
         // TC_ENDBLOCKDATA
         // TC_NULL
         ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
         DataOutputStream dout = new DataOutputStream(byteOut);
         try {
            dout.writeByte(ObjectStreamConstants.TC_OBJECT);
            dout.writeByte(ObjectStreamConstants.TC_CLASSDESC);
            dout.writeUTF(clazz.getName());
            dout.writeLong(ObjectStreamClass.lookup(clazz).getSerialVersionUID());
            dout.writeByte(ObjectStreamConstants.SC_SERIALIZABLE);
            dout.writeShort((short) 0); // Zero fields
            dout.writeByte(ObjectStreamConstants.TC_ENDBLOCKDATA);
            dout.writeByte(ObjectStreamConstants.TC_NULL);
         }
         catch(IOException e) {
            throw new Error("IOException: " + e.getMessage());
         }
         byte[] firstData = byteOut.toByteArray();
         buffers = new byte[][] {HEADER, firstData, REPEATING_DATA};
      }

      private void advanceBuffer() {
         pointer = 0;
         sequence = NEXT[sequence];
         data = buffers[sequence];
      }

      @Override
      public int read() {
         int result = data[pointer++];
         if(pointer >= data.length) {
            advanceBuffer();
         }

         return result;
      }

      @Override
      public int available() {
         return Integer.MAX_VALUE;
      }

      @Override
      public int read(byte[] b, int off, int len) {
         int left = len;
         int remaining = data.length - pointer;

         while(remaining <= left) {
            System.arraycopy(data, pointer, b, off, remaining);
            off += remaining;
            left -= remaining;
            advanceBuffer();
            remaining = data.length - pointer;
         }
         if(left > 0) {
            System.arraycopy(data, pointer, b, off, left);
            pointer += left;
         }

         return len;
      }
   }

   private final ObjectInputStream inputStream;

   public ObjectInputStreamInstantiator(Class<T> clazz) {
      if(Serializable.class.isAssignableFrom(clazz)) {
         try {
            this.inputStream = new ObjectInputStream(new MockStream(clazz));
         }
         catch(IOException e) {
            throw new Error("IOException: " + e.getMessage());
         }
      }
      else {
         throw new ObjenesisException(new NotSerializableException(clazz + " not serializable"));
      }
   }

   @SuppressWarnings("unchecked")
   public T newInstance() {
      try {
         return (T) inputStream.readObject();
      }
      catch(ClassNotFoundException e) {
         throw new Error("ClassNotFoundException: " + e.getMessage());
      }
      catch(Exception e) {
         throw new ObjenesisException(e);
      }
   }
}
