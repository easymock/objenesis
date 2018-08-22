/**
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
package org.objenesis.instantiator.sun;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.instantiator.annotations.Instantiator;
import org.objenesis.instantiator.annotations.Typology;
import org.objenesis.instantiator.util.ClassDefinitionUtils;

import static org.objenesis.instantiator.util.ClassDefinitionUtils.*;

/**
 * This instantiator will correctly bypass the constructors by instantiating the class using the default
 * constructor from Object. It will be allowed to do so by extending {@code MagicAccessorImpl} which prevents
 * its children to be verified by the class loader
 *
 * @author Henri Tremblay
 */
@Instantiator(Typology.STANDARD)
public class MagicInstantiator<T> implements ObjectInstantiator<T> {

   private static final String MAGIC_ACCESSOR = getMagicClass();

   private static final int INDEX_CLASS_THIS = 1;
   private static final int INDEX_CLASS_SUPERCLASS = 2;
   private static final int INDEX_UTF8_CONSTRUCTOR_NAME = 3;
   private static final int INDEX_UTF8_CONSTRUCTOR_DESC = 4;
   private static final int INDEX_UTF8_CODE_ATTRIBUTE = 5;
   private static final int INDEX_UTF8_INSTANTIATOR_CLASS = 7;
   private static final int INDEX_UTF8_SUPERCLASS = 8;
   private static final int INDEX_CLASS_INTERFACE = 9;
   private static final int INDEX_UTF8_INTERFACE = 10;
   private static final int INDEX_UTF8_NEWINSTANCE_NAME = 11;
   private static final int INDEX_UTF8_NEWINSTANCE_DESC = 12;
   private static final int INDEX_METHODREF_OBJECT_CONSTRUCTOR = 13;
   private static final int INDEX_CLASS_OBJECT = 14;
   private static final int INDEX_UTF8_OBJECT = 15;
   private static final int INDEX_NAMEANDTYPE_DEFAULT_CONSTRUCTOR = 16;
   private static final int INDEX_CLASS_TYPE = 17;
   private static final int INDEX_UTF8_TYPE = 18;

   private static int CONSTANT_POOL_COUNT = 19;

   private static final byte[] CONSTRUCTOR_CODE = { OPS_aload_0, OPS_invokespecial, 0, INDEX_METHODREF_OBJECT_CONSTRUCTOR, OPS_return};
   private static final int CONSTRUCTOR_CODE_ATTRIBUTE_LENGTH = 12 + CONSTRUCTOR_CODE.length;

   private static final byte[] NEWINSTANCE_CODE = { OPS_new, 0, INDEX_CLASS_TYPE, OPS_dup, OPS_invokespecial, 0, INDEX_METHODREF_OBJECT_CONSTRUCTOR, OPS_areturn};
   private static final int NEWINSTANCE_CODE_ATTRIBUTE_LENGTH = 12 + NEWINSTANCE_CODE.length;

   private static final String CONSTRUCTOR_NAME = "<init>";
   private static final String CONSTRUCTOR_DESC = "()V";

   private ObjectInstantiator<T> instantiator;

   public MagicInstantiator(Class<T> type) {
      instantiator = newInstantiatorOf(type);
   }

   /**
    * Get the underlying instantiator.
    *
    * {@link MagicInstantiator} is a wrapper around another object
    * which implements {@link ObjectInstantiator} interface.
    * This method exposes that instantiator.
    *
    * @return the underlying instantiator
    */
   public ObjectInstantiator<T> getInstantiator() {
      return instantiator;
   }

   private <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {
      String suffix = type.getSimpleName();
      String className = getClass().getName() + "$$$" + suffix;

      Class<ObjectInstantiator<T>> clazz = getExistingClass(getClass().getClassLoader(), className);

      if(clazz == null) {
         byte[] classBytes = writeExtendingClass(type, className);

         try {
            clazz = ClassDefinitionUtils.defineClass(className, classBytes, getClass().getClassLoader());
         } catch (Exception e) {
            throw new ObjenesisException(e);
         }
      }

      return ClassDefinitionUtils.newInstance(clazz);
   }

   /**
    * Will generate the bytes for a class extending the type passed in parameter. This class will
    * only have an empty default constructor
    *
    * @param type type to extend
    * @param className name of the wrapped instantiator class
    * @return the byte for the class
    * @throws ObjenesisException is something goes wrong
    */
   private byte[] writeExtendingClass(Class<?> type, String className) {
      String clazz = classNameToInternalClassName(className);

      DataOutputStream in = null;
      ByteArrayOutputStream bIn = new ByteArrayOutputStream(1000); // 1000 should be large enough to fit the entire class
      try {
         in = new DataOutputStream(bIn);

         in.write(MAGIC);
         in.write(VERSION);
         in.writeShort(CONSTANT_POOL_COUNT);

         // set all the constant pool here

         // 1. class
         in.writeByte(CONSTANT_Class);
         in.writeShort(INDEX_UTF8_INSTANTIATOR_CLASS);

         // 2. super class
         in.writeByte(CONSTANT_Class);
         in.writeShort(INDEX_UTF8_SUPERCLASS);

         // 3. default constructor name
         in.writeByte(CONSTANT_Utf8);
         in.writeUTF(CONSTRUCTOR_NAME);

         // 4. default constructor description
         in.writeByte(CONSTANT_Utf8);
         in.writeUTF(CONSTRUCTOR_DESC);

         // 5. Code
         in.writeByte(CONSTANT_Utf8);
         in.writeUTF("Code");

         // 6. Class name
         in.writeByte(CONSTANT_Utf8);
         in.writeUTF("L" + clazz + ";");

         // 7. Class name (again)
         in.writeByte(CONSTANT_Utf8);
         in.writeUTF(clazz);

         // 8. Superclass name
         in.writeByte(CONSTANT_Utf8);
//         in.writeUTF("java/lang/Object");
         in.writeUTF(MAGIC_ACCESSOR);

         // 9. ObjectInstantiator interface
         in.writeByte(CONSTANT_Class);
         in.writeShort(INDEX_UTF8_INTERFACE);

         // 10. ObjectInstantiator name
         in.writeByte(CONSTANT_Utf8);
         in.writeUTF(ObjectInstantiator.class.getName().replace('.', '/'));

         // 11. newInstance name
         in.writeByte(CONSTANT_Utf8);
         in.writeUTF("newInstance");

         // 12. newInstance desc
         in.writeByte(CONSTANT_Utf8);
         in.writeUTF("()Ljava/lang/Object;");

         // 13. Methodref to the Object constructor
         in.writeByte(CONSTANT_Methodref);
         in.writeShort(INDEX_CLASS_OBJECT);
         in.writeShort(INDEX_NAMEANDTYPE_DEFAULT_CONSTRUCTOR);

         // 14. Object class
         in.writeByte(CONSTANT_Class);
         in.writeShort(INDEX_UTF8_OBJECT);

         // 15. Object class name
         in.writeByte(CONSTANT_Utf8);
         in.writeUTF("java/lang/Object");

         // 16. Default constructor name and type
         in.writeByte(CONSTANT_NameAndType);
         in.writeShort(INDEX_UTF8_CONSTRUCTOR_NAME);
         in.writeShort(INDEX_UTF8_CONSTRUCTOR_DESC);

         // 17. Type to instantiate class
         in.writeByte(CONSTANT_Class);
         in.writeShort(INDEX_UTF8_TYPE);

         // 18. Type to instantiate name
         in.writeByte(CONSTANT_Utf8);
         in.writeUTF(classNameToInternalClassName(type.getName()));

         // end of constant pool

         // access flags: We want public, ACC_SUPER is always there
         in.writeShort(ACC_PUBLIC | ACC_SUPER | ACC_FINAL);

         // this class index in the constant pool
         in.writeShort(INDEX_CLASS_THIS);

         // super class index in the constant pool
         in.writeShort(INDEX_CLASS_SUPERCLASS);

         // interfaces implemented count (we have none)
         in.writeShort(1);
         in.writeShort(INDEX_CLASS_INTERFACE);

         // fields count (we have none)
         in.writeShort(0);

         // method count (we have two: the default constructor and newInstance)
         in.writeShort(2);

         // default constructor method_info
         in.writeShort(ACC_PUBLIC);
         in.writeShort(INDEX_UTF8_CONSTRUCTOR_NAME); // index of the method name (<init>)
         in.writeShort(INDEX_UTF8_CONSTRUCTOR_DESC); // index of the description
         in.writeShort(1); // number of attributes: only one, the code

         // code attribute of the default constructor
         in.writeShort(INDEX_UTF8_CODE_ATTRIBUTE);
         in.writeInt(CONSTRUCTOR_CODE_ATTRIBUTE_LENGTH); // attribute length
         in.writeShort(0); // max_stack
         in.writeShort(1); // max_locals
         in.writeInt(CONSTRUCTOR_CODE.length); // code length
         in.write(CONSTRUCTOR_CODE);
         in.writeShort(0); // exception_table_length = 0
         in.writeShort(0); // attributes count = 0, no need to have LineNumberTable and LocalVariableTable

         // newInstance method_info
         in.writeShort(ACC_PUBLIC);
         in.writeShort(INDEX_UTF8_NEWINSTANCE_NAME); // index of the method name (newInstance)
         in.writeShort(INDEX_UTF8_NEWINSTANCE_DESC); // index of the description
         in.writeShort(1); // number of attributes: only one, the code

         // code attribute of newInstance
         in.writeShort(INDEX_UTF8_CODE_ATTRIBUTE);
         in.writeInt(NEWINSTANCE_CODE_ATTRIBUTE_LENGTH); // attribute length
         in.writeShort(2); // max_stack
         in.writeShort(1); // max_locals
         in.writeInt(NEWINSTANCE_CODE.length); // code length
         in.write(NEWINSTANCE_CODE);
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

      return bIn.toByteArray();
   }

   public T newInstance() {
      return instantiator.newInstance();
   }

   private static String getMagicClass() {
      try {
         Class.forName("sun.reflect.MagicAccessorImpl", false, MagicInstantiator.class.getClassLoader());
         return "sun/reflect/MagicAccessorImpl";
      } catch (ClassNotFoundException e) {
         return "jdk/internal/reflect/MagicAccessorImpl";
      }
   }
}
