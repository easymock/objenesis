package org.objenesis.instantiator.basic;

import org.objenesis.instantiator.ObjectInstantiator;

import java.io.*;

/**
 * @author Henri Tremblay
 */
public class ProxyObjectInstantiator<T> implements ObjectInstantiator<T> {

   static final int CONSTANT_Utf8 = 1;
   static final int CONSTANT_Integer = 3;
   static final int CONSTANT_Float = 4;
   static final int CONSTANT_Long = 5;
   static final int CONSTANT_Double = 6;
   static final int CONSTANT_Class = 7;
   static final int CONSTANT_String = 8;
   static final int CONSTANT_Fieldref = 9;
   static final int CONSTANT_Methodref = 10;
   static final int CONSTANT_InterfaceMethodref = 11;
   static final int CONSTANT_NameAndType = 12;
   static final int CONSTANT_MethodHandle = 15;
   static final int CONSTANT_MethodType = 16;
   static final int CONSTANT_InvokeDynamic = 18;

   static final int ACC_PUBLIC = 0x0001; // Declared public; may be accessed from outside its package.
   static final int ACC_FINAL = 0x0010; // Declared final; no subclasses allowed.
   static final int ACC_SUPER = 0x0020; // Treat superclass methods specially when invoked by the invokespecial instruction.
   static final int ACC_INTERFACE = 0x0200; // Is an interface, not a class.
   static final int ACC_ABSTRACT = 0x0400; // Declared abstract; must not be instantiated.
   static final int ACC_SYNTHETIC = 0x1000; // Declared synthetic; not present in the source code.
   static final int ACC_ANNOTATION = 0x2000; // Declared as an annotation type.
   static final int ACC_ENUM = 0x4000; // Declared as an enum type.

   private static final String CONSTRUCTOR = "<init>";

   static byte[] MAGIC = { (byte) 0xca, (byte) 0xfe, (byte) 0xba, (byte) 0xbe };
   static byte[] VERSION = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x31 }; // minor_version, major_version (Java 5)
   static byte[] CONSTANT_POOL = {
      CONSTANT_Class, 0x03, // 1. class
      CONSTANT_Class, 0x04, // 2. superclass
      CONSTANT_Utf8, 0x05, // 3. name of the class
      CONSTANT_Utf8, 0x05, // 4. name of the superclass
      CONSTANT_Utf8, 0x05, // 5. default constructor name
   };
   static byte[] CONSTANT_POOL_COUNT = { 0x00, 0x06 };
   static byte[] ACCESS_FLAGS = { 0x00, ACC_PUBLIC };
   static byte[] THIS_CLASS = { (byte) 0x00, (byte) 0x01 };
   static byte[] SUPER_CLASS = { (byte) 0x00, (byte) 0x02 };
   static byte[] INTERFACES_COUNT = { (byte) 0x00, (byte) 0x00 };
   static byte[] FIELDS_COUNT = { (byte) 0x00, (byte) 0x00 };
   static byte[] METHODS_COUNT = { (byte) 0x00, (byte) 0x01 };
   static byte[] ATTRIBUTES_COUNT = { (byte) 0x00, (byte) 0x00 };


   public ProxyObjectInstantiator(Class<T> type) {
      String clazz = type.getName().replace('.', '/');
      String parentClazz = type.getSuperclass().getName().replace('.', '/');


      try {
         DataOutputStream in = new DataOutputStream(new FileOutputStream("test.dat"));
         in.write(MAGIC);
         in.write(VERSION);
         in.write(CONSTANT_POOL_COUNT);

         // class name
         in.write(CONSTANT_Utf8);
         in.writeInt(clazz.length());
         in.writeUTF(clazz);

         // superclass name
         in.write(CONSTANT_Utf8);
         in.writeInt(parentClazz.length());
         in.writeUTF(parentClazz);

         // default constructor
         in.write(CONSTANT_Utf8);
         in.writeInt(CONSTRUCTOR.length());
         in.writeUTF(CONSTRUCTOR);

         in.write(ACCESS_FLAGS);
         in.write(THIS_CLASS);
         in.write(SUPER_CLASS);
         in.write(INTERFACES_COUNT);
         in.write(FIELDS_COUNT);
         in.write(METHODS_COUNT);

         in.write(new byte[] { 0x00, ACC_PUBLIC }); // public

         in.write(ATTRIBUTES_COUNT);
         in.close();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   public T newInstance() {
      return null;
   }

}
