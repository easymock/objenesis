package org.objenesis.instantiator.basic;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Henri Tremblay
 */
public class ProxyObjectInstantiator<T> implements ObjectInstantiator<T> {

   static final byte OPS_aload_0 = 42;
   static final byte OPS_invokespecial = -73; // has two bytes parameters
   static final byte OPS_return = -79;

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

   static final int INDEX_METHODREF_SUPERCLASS_CONSTRUCTOR = 1;
   static final int INDEX_CLASS_THIS = 2;
   static final int INDEX_CLASS_SUPERCLASS = 3;
   static final int INDEX_UTF8_CONSTRUCTOR_NAME = 4;
   static final int INDEX_UTF8_CONSTRUCTOR_DESC = 5;
   static final int INDEX_UTF8_CODE_ATTRIBUTE = 6;
   static final int INDEX_NAMEANDTYPE_DEFAULT_CONSTRUCTOR = 13;
   static final int INDEX_UTF8_CLASS = 14;
   static final int INDEX_UTF8_SUPERCLASS = 15;

   static int CONSTANT_POOL_COUNT = 16;

   static final byte[] CODE = { OPS_aload_0, OPS_invokespecial, 0, INDEX_METHODREF_SUPERCLASS_CONSTRUCTOR, OPS_return};

   static final String CONSTRUCTOR_NAME = "<init>";
   static final String CONSTRUCTOR_DESC = "()V";

   static byte[] MAGIC = { (byte) 0xca, (byte) 0xfe, (byte) 0xba, (byte) 0xbe };
   static byte[] VERSION = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x31 }; // minor_version, major_version (Java 5)

   private final Class<?> newType;

   public ProxyObjectInstantiator(Class<T> type) {
      String parentClazz = type.getName().replace('.', '/');
      String clazz = parentClazz +"$$$Objenesis";

      DataOutputStream in = null;
      ByteArrayOutputStream bIn = new ByteArrayOutputStream(1000); // 1000 should be large enough
      try {
         in = new DataOutputStream(bIn);

         in.write(MAGIC);
         in.write(VERSION);
         in.writeShort(CONSTANT_POOL_COUNT);

         // set all the constant pool here

         // 1. Methodref to the superclass constructor
         in.writeByte(CONSTANT_Methodref);
         in.writeShort(INDEX_CLASS_SUPERCLASS);
         in.writeShort(INDEX_NAMEANDTYPE_DEFAULT_CONSTRUCTOR);

         // 2. class
         in.writeByte(CONSTANT_Class);
         in.writeShort(INDEX_UTF8_CLASS);

         // 3. super class
         in.writeByte(CONSTANT_Class);
         in.writeShort(INDEX_UTF8_SUPERCLASS);

         // 4. default constructor name
         in.writeByte(CONSTANT_Utf8);
         in.writeUTF(CONSTRUCTOR_NAME);

         // 5. default constructor description
         in.writeByte(CONSTANT_Utf8);
         in.writeUTF(CONSTRUCTOR_DESC);

         // 6. Code
         in.writeByte(CONSTANT_Utf8);
         in.writeUTF("Code");

         // 7. LineNumberTable
         in.writeByte(CONSTANT_Utf8);
         in.writeUTF("LineNumberTable");

         // 8. LocalVariableTable
         in.writeByte(CONSTANT_Utf8);
         in.writeUTF("LocalVariableTable");

         // 9. this
         in.writeByte(CONSTANT_Utf8);
         in.writeUTF("this");

         // 10. Class name
         in.writeByte(CONSTANT_Utf8);
         in.writeUTF("L" + clazz + ";");

         // 11. SourceFile
         in.writeByte(CONSTANT_Utf8);
         in.writeUTF("SourceFile");

         // 12. File
         in.writeByte(CONSTANT_Utf8);
         in.writeUTF(type.getSimpleName() + ".java");

         // 13. Constructor
         in.writeByte(CONSTANT_NameAndType);
         in.writeShort(INDEX_UTF8_CONSTRUCTOR_NAME);
         in.writeShort(INDEX_UTF8_CONSTRUCTOR_DESC);

         // 14. Class name (again)
         in.writeByte(CONSTANT_Utf8);
         in.writeUTF(clazz);

         // 15. Superclass name
         in.writeByte(CONSTANT_Utf8);
         in.writeUTF(parentClazz);

         // end of constant pool

         // access flags: We want public, ACC_SUPER is always there
         in.writeShort(ACC_PUBLIC | ACC_SUPER);

         // this class index in the constant pool
         in.writeShort(INDEX_CLASS_THIS);

         // super class index in the constant pool
         in.writeShort(INDEX_CLASS_SUPERCLASS);

         // interfaces implemented count (we have none)
         in.writeShort(0);

         // fields count (we have none)
         in.writeShort(0);

         // methods count (we have one: the default constructor)
         in.writeShort(1);

         // default constructor method_info
         in.writeShort(ACC_PUBLIC);
         in.writeShort(INDEX_UTF8_CONSTRUCTOR_NAME); // index of the method name (<init>)
         in.writeShort(INDEX_UTF8_CONSTRUCTOR_DESC); // index of the description
         in.writeShort(1); // number of attributes: only one, the code

         // code attribute of the default constructor
         in.writeShort(INDEX_UTF8_CODE_ATTRIBUTE);
         in.writeInt(17); // attribute length
         in.writeShort(1); // max_stack
         in.writeShort(1); // max_locals
         in.writeInt(CODE.length); // code length
         in.write(CODE);
         in.writeShort(0); // exception_table_length = 0
         in.writeShort(0); // attributes count = 0, no need to have LineNumberTable and LocalVariableTable

         // class attributes
         in.writeShort(0); // none. No need to have a source file attribute


      } catch (IOException e) {
         throw new ObjenesisException(e);
      } finally {
         if(in != null) {
            try {
               in.close();
            } catch (IOException e) {
               throw new ObjenesisException(e);
            }
         }
      }

      byte[] classBytes = bIn.toByteArray();

      try {
         FileOutputStream out = new FileOutputStream("test.dat");
         out.write(classBytes);
         out.close();
      }
      catch(IOException e) {
         throw new ObjenesisException(e);
      }
      Class<?> result;
      try {
         result = ClassDefinitionUtils.defineClass(clazz.replace('/', '.'), classBytes, type.getClassLoader());
      } catch (Exception e) {
         throw new ObjenesisException(e);
      }

      newType = result;
   }

   @SuppressWarnings("unchecked")
   public T newInstance() {
      try {
         return (T) newType.newInstance();
      } catch (InstantiationException e) {
         throw new ObjenesisException(e);
      } catch (IllegalAccessException e) {
         throw new ObjenesisException(e);
      }
   }

}
