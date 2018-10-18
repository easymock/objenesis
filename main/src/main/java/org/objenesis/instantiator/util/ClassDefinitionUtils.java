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
package org.objenesis.instantiator.util;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;

/**
 * Helper class for ProxyObjectInstantiator. We can see the details of a class specification
 * <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html">here</a>
 *
 * @author Henri Tremblay
 */
public final class ClassDefinitionUtils {

   public static final byte OPS_aload_0 = 42;
   public static final byte OPS_invokespecial = -73; // has two bytes parameters
   public static final byte OPS_return = -79;
   public static final byte OPS_new = -69;
   public static final byte OPS_dup = 89;
   public static final byte OPS_areturn = -80;

   public static final int CONSTANT_Utf8 = 1;
   public static final int CONSTANT_Integer = 3;
   public static final int CONSTANT_Float = 4;
   public static final int CONSTANT_Long = 5;
   public static final int CONSTANT_Double = 6;
   public static final int CONSTANT_Class = 7;
   public static final int CONSTANT_String = 8;
   public static final int CONSTANT_Fieldref = 9;
   public static final int CONSTANT_Methodref = 10;
   public static final int CONSTANT_InterfaceMethodref = 11;
   public static final int CONSTANT_NameAndType = 12;
   public static final int CONSTANT_MethodHandle = 15;
   public static final int CONSTANT_MethodType = 16;
   public static final int CONSTANT_InvokeDynamic = 18;

   public static final int ACC_PUBLIC = 0x0001; // Declared public; may be accessed from outside its package.
   public static final int ACC_FINAL = 0x0010; // Declared final; no subclasses allowed.
   public static final int ACC_SUPER = 0x0020; // Treat superclass methods specially when invoked by the invokespecial instruction.
   public static final int ACC_INTERFACE = 0x0200; // Is an interface, not a class.
   public static final int ACC_ABSTRACT = 0x0400; // Declared abstract; must not be instantiated.
   public static final int ACC_SYNTHETIC = 0x1000; // Declared synthetic; not present in the source code.
   public static final int ACC_ANNOTATION = 0x2000; // Declared as an annotation type.
   public static final int ACC_ENUM = 0x4000; // Declared as an enum type.

   public static final byte[] MAGIC = { (byte) 0xca, (byte) 0xfe, (byte) 0xba, (byte) 0xbe };
   public static final byte[] VERSION = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x31 }; // minor_version, major_version (Java 5)

   private ClassDefinitionUtils() { }

   private static final ProtectionDomain PROTECTION_DOMAIN;

   static {
      PROTECTION_DOMAIN = AccessController.doPrivileged((PrivilegedAction<ProtectionDomain>) ClassDefinitionUtils.class::getProtectionDomain);
   }

   /**
    * Define a class in the provided class loader from the array of bytes. Inspired by cglib
    * <code>ReflectUtils.defineClass</code>
    *
    * @param <T> type of the class returned
    * @param className class name in the format <code>org.objenesis.MyClass</code>
    * @param b bytes representing the class
    * @param neighbor a class in the same package as the loaded class
    * @param loader the class loader where the class will be loaded
    * @return the newly loaded class
    * @throws Exception whenever something goes wrong
    */
   @SuppressWarnings("unchecked")
   public static <T> Class<T> defineClass(String className, byte[] b, Class<?> neighbor, ClassLoader loader)
      throws Exception {
      Class<T> c = (Class<T>) DefineClassHelper.defineClass(className, b, 0, b.length, neighbor, loader, PROTECTION_DOMAIN);
      // Force static initializers to run.
      Class.forName(className, true, loader);
      return c;
   }

   /**
    * Read the bytes of a class from the classpath
    *
    * @param className full class name including the package
    * @return the bytes representing the class
    * @throws IllegalArgumentException if the class is longer than 2500 bytes
    * @throws IOException if we fail to read the class
    */
   public static byte[] readClass(String className) throws IOException {
      // convert to a resource
      className = ClassUtils.classNameToResource(className);

      byte[] b = new byte[2500]; // I'm assuming that I'm reading class that are not too big

      int length;

      try (InputStream in = ClassDefinitionUtils.class.getClassLoader().getResourceAsStream(className)) {
         length = in.read(b);
      }

      if(length >= 2500) {
         throw new IllegalArgumentException("The class is longer that 2500 bytes which is currently unsupported");
      }

      byte[] copy = new byte[length];
      System.arraycopy(b, 0, copy, 0, length);
      return copy;
   }

   /**
    * Write all class bytes to a file.
    *
    * @param fileName file where the bytes will be written
    * @param bytes bytes representing the class
    * @throws IOException if we fail to write the class
    */
   public static void writeClass(String fileName, byte[] bytes) throws IOException {
      try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileName))) {
         out.write(bytes);
      }
   }
}
