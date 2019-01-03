/*
 * Copyright 2006-2019 the original author or authors.
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

import org.objenesis.instantiator.util.ClassUtils;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.objenesis.instantiator.util.ClassDefinitionUtils.*;

/**
 * @author Henri Tremblay
 */
public class ClassReader {

   private final byte[] buffer = new byte[256];
   private Object[] constant_pool;

   public static void main(String[] args) throws IOException {
      if(args.length != 1) {
         System.out.println("Usage: ClassReader (path_to_the_class_file|class:complete_class_name)");
      }

      ClassReader reader = new ClassReader();
      reader.readClass(args[0]);
   }

   static class CONSTANT_Utf8_info {
      // int length; u2 is read by readUTF
      String bytes;

      CONSTANT_Utf8_info(DataInputStream in) throws IOException {
         bytes = in.readUTF();
      }

      @Override
      public String toString() {
         return "CONSTANT_Utf8_info{" +
            "bytes='" + bytes + '\'' +
            '}';
      }
   }

   static class CONSTANT_Methodref_info {
      int class_index; // u2
      int name_and_type_index; // u2

      CONSTANT_Methodref_info(DataInputStream in) throws IOException {
         class_index = in.readUnsignedShort();
         name_and_type_index = in.readUnsignedShort();
      }

      @Override
      public String toString() {
         return "CONSTANT_Methodref_info{" +
            "class_index=" + class_index +
            ", name_and_type_index=" + name_and_type_index +
            '}';
      }
   }

   static class CONSTANT_Class_info {
      int name_index; // u2

      public CONSTANT_Class_info(DataInputStream in) throws IOException{
         name_index = in.readUnsignedShort();
      }

      @Override
      public String toString() {
         return "CONSTANT_Class_info{" +
            "name_index=" + name_index +
            '}';
      }
   }

   static class CONSTANT_NameAndType_info {
      int name_index; // u2
      int descriptor_index; // u2

      public CONSTANT_NameAndType_info(DataInputStream in) throws IOException{
         name_index = in.readUnsignedShort();
         descriptor_index = in.readUnsignedShort();
      }

      @Override
      public String toString() {
         return "CONSTANT_NameAndType_info{" +
            "name_index=" + name_index +
            ", descriptor_index=" + descriptor_index +
            '}';
      }
   }

   class method_info {
      int access_flags; // u2
      int name_index;
      int descriptor_index;
      int attributes_count;
      attribute_info[] attributes;

      public method_info(DataInputStream in) throws IOException{
         access_flags = in.readUnsignedShort();
         name_index = in.readUnsignedShort();
         descriptor_index = in.readUnsignedShort();
         attributes_count = in.readUnsignedShort();
         attributes = new attribute_info[attributes_count];

         for (int i = 0; i < attributes_count; i++) {
            attributes[i] = new attribute_info(in);
         }
      }

      @Override
      public String toString() {
         return "method_info{" +
            "access_flags=" + access_flags +
            ", name_index=" + name_index +
            ", descriptor_index=" + descriptor_index +
            ", attributes_count=" + attributes_count +
            '}';
      }
   }

   class attribute_info {
      int attribute_name_index; // u2
      int attribute_length; // u4
      Object info;

      public attribute_info(DataInputStream in) throws IOException{
         attribute_name_index = in.readUnsignedShort();
         attribute_length = in.readInt();

         String attribute_name = ((CONSTANT_Utf8_info) constant_pool[attribute_name_index]).bytes;

         System.out.println(this +  " " + attribute_name);

         if("Code".equals(attribute_name)) {
            info = new Code_attribute(in);
         }
         else if("SourceFile".equals(attribute_name)) {
            assertEquals(2, attribute_length); // always 2
            info = new SourceFile_attribute(in);
         }
         else if("LineNumberTable".equals(attribute_name)) {
            // I don't care about that (only used for debugging) so I will skip
            System.out.println("Attribute LineNumberTable skipped");
            in.read(buffer, 0, attribute_length);
         }
         else if("LocalVariableTable".equals(attribute_name)) {
            // I don't care about that (only used for debugging) so I will skip
            System.out.println("Attribute LocalVariableTable skipped");
            in.read(buffer, 0, attribute_length);
         }
         else {
            fail("Unknown attribute: " + attribute_name);
         }

         System.out.println("\t" + info);
      }

      @Override
      public String toString() {
         return "attribute_info{" +
            "attribute_name_index=" + attribute_name_index +
            ", attribute_length=" + attribute_length +
            '}';
      }
   }

   class Code_attribute {
      int max_stack; // u2
      int max_locals; // u2
      int code_length; // u4
      byte[] code; // length of code_length
      int exception_table_length; // u2 if will be 0, so we will skip the exception_table
      int attributes_count; // u2
      attribute_info[] attributes;

      Code_attribute(DataInputStream in) throws IOException {
         max_stack = in.readUnsignedShort();
         max_locals = in.readUnsignedShort();
         code_length = in.readInt();
         code = new byte[code_length];
         in.read(code);
         exception_table_length = in.readUnsignedShort();
         attributes_count = in.readUnsignedShort();
         attributes = new attribute_info[attributes_count];
         for (int i = 0; i < attributes_count; i++) {
            attributes[i] = new attribute_info(in);
         }
      }

      @Override
      public String toString() {
         return "Code_attribute{" +
            "max_stack=" + max_stack +
            ", max_locals=" + max_locals +
            ", code_length=" + code_length +
            ", code=" + Arrays.toString(code) +
            ", exception_table_length=" + exception_table_length +
            ", attributes_count=" + attributes_count +
            '}';
      }
   }

   static class SourceFile_attribute {
      int sourcefile_index;

      SourceFile_attribute(DataInputStream in) throws IOException {
         sourcefile_index = in.readUnsignedShort();
      }

      @Override
      public String toString() {
         return "SourceFile_attribute{" +
            "sourcefile_index=" + sourcefile_index +
            '}';
      }
   }

   public void readClass(String classPath) throws IOException {
      InputStream iin;
      if(classPath.startsWith("classpath:")) {
         String className = classPath.substring("classpath:".length());
         String resourceName = ClassUtils.classNameToResource(className);
         iin = getClass().getClassLoader().getResourceAsStream(resourceName);
      }
      else {
         iin = new FileInputStream(classPath);
      }

      DataInputStream in = new DataInputStream(iin);

      // magic number
      in.read(buffer, 0, MAGIC.length);
      assertArrayEquals(MAGIC);

      // version
      in.read(buffer, 0, VERSION.length);
      assertArrayEquals(VERSION);

      // constant_pool_count
      int constant_pool_count = in.readUnsignedShort();
      System.out.println("Constant pool count: " + constant_pool_count);

      // indexed from 1 (0 will be unused) to constant_pool_count-1
      constant_pool = new Object[constant_pool_count];

      // constant pool
      for (int i = 1; i < constant_pool_count; i++) {
         System.out.print(i + ": ");
         int type = in.readUnsignedByte();
         switch(type) {
         case CONSTANT_Utf8:
            constant_pool[i] = new CONSTANT_Utf8_info(in);
            break;
         case CONSTANT_Class:
            constant_pool[i] = new CONSTANT_Class_info(in);
            break;
         case CONSTANT_Methodref:
            constant_pool[i] = new CONSTANT_Methodref_info(in);
            break;
         case CONSTANT_NameAndType:
            constant_pool[i] = new CONSTANT_NameAndType_info(in);
            break;
         default:
            fail("Unknown type: " + type);
         }
         System.out.println(constant_pool[i]);
      }

      // access flags
      int access_flags = in.readUnsignedShort();
      System.out.println("Access flags: " + access_flags); // see http://stackoverflow.com/questions/8949933/what-is-the-purpose-of-the-acc-super-access-flag-on-java-class-files

      // this class name
      int this_class = in.readUnsignedShort();
      System.out.println("This class index: " + this_class);

      // super class name
      int super_class = in.readUnsignedShort();
      System.out.println("This superclass index: " + super_class);

      // interfaces implemented count (we have none)
      int interfaces_count = in.readUnsignedShort();
      System.out.println("Interfaces count: " + interfaces_count);
      for (int i = 0; i < interfaces_count; i++) {
         int index = in.readUnsignedShort();
         System.out.println("Interface " + i + " index: " + index);
      }

      // fields count (we have none)
      int fields_count = in.readUnsignedShort();
      System.out.println("Fields count: " + fields_count);
      assertEquals("Reading fields isn't yet supported", 0, fields_count);

      //methods count (we have one)
      int methods_count = in.readUnsignedShort();
      System.out.println("Methods count: " + methods_count);

      for (int i = 0; i < methods_count; i++) {
         method_info methodInfo = new method_info(in);
         System.out.println("for " + methodInfo);
      }

      // reading final class attributes
      int attributes_count = in.readUnsignedShort();
      System.out.println("Class attributes count: " + attributes_count);
      for (int i = 0; i < attributes_count ; i++) {
         attribute_info attributeInfo = new attribute_info(in);
      }

      in.close();
   }

   private void assertArrayEquals(byte[] expected) {
      for (int i = 0; i < expected.length; i++) {
         if(expected[i] != buffer[i]) {
            fail("Expected was " + Arrays.toString(expected) + " but actual is " + Arrays.toString(buffer));
         }
      }
   }
}
