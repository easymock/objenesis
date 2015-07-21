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
package org.objenesis.instantiator.basic;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.objenesis.instantiator.basic.ClassDefinitionUtils.*;
/**
 * This instantiator creates a class by dynamically extending it. The attempt is to slip the constructor of the
 * super class. So that the constructor is indeed not called but you however instantiate a child class, not
 * the actual class. Currently, there is a verify error if the constructor is skipped (so it is currently not)
 *
 * @author Henri Tremblay
 */
public class ProxyObjectInstantiator<T> implements ObjectInstantiator<T> {

   private static int CONSTANT_POOL_COUNT = 16;
   private static final byte[] CODE = { OPS_aload_0, OPS_invokespecial, 0, INDEX_METHODREF_SUPERCLASS_CONSTRUCTOR, OPS_return};

   static final String CONSTRUCTOR_NAME = "<init>";
   static final String CONSTRUCTOR_DESC = "()V";

   private final Class<?> newType;

   public ProxyObjectInstantiator(Class<T> type) {
      String parentClazz = type.getName().replace('.', '/');
      String clazz = parentClazz +"$$$Objenesis";

      DataOutputStream in = null;
      ByteArrayOutputStream bIn = new ByteArrayOutputStream(1000); // 1000 should be large enough to fit the entire class
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
