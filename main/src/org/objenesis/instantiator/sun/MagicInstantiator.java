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
package org.objenesis.instantiator.sun;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.instantiator.basic.ClassDefinitionUtils;

import java.util.concurrent.atomic.AtomicInteger;

import static org.objectweb.asm.Opcodes.*;

/**
 * This instantiator will correctly bypass the constructors by instantiating the class using the default
 * constructor from Object. It will be allowed to do so by extending {@code MagicAccessorImpl} which prevents
 * its children to be verified by the class loader
 * 
 * @author Henri Tremblay
 */
public class MagicInstantiator<T> implements ObjectInstantiator<T> {

   private static final AtomicInteger uniquifier = new AtomicInteger(0);
   private ObjectInstantiator<T> instantiator;

   public MagicInstantiator(Class<T> type) {
      instantiator = newInstantiatorOf(type);
   }

   private <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {
      try {
         String className = getClass().getName() + "$$" + uniquifier.getAndIncrement();

         ClassWriter cw = new ClassWriter(0);
         cw.visit(V1_5, ACC_PUBLIC | ACC_FINAL, className.replace('.', '/'), null, "sun/reflect/MagicAccessorImpl",
            new String[] { ObjectInstantiator.class.getName().replace('.', '/') });
         cw.visitSource("ObjectInstantiator", null);

         {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 1);
         }

         {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "()Ljava/lang/Object;", null, null);
            mv.visitCode();
            mv.visitTypeInsn(NEW, Type.getInternalName(type));
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(0, 1);
         }

         byte[] bytes = cw.toByteArray();

         Class<ObjectInstantiator<T>> clazz = ClassDefinitionUtils.defineClass(className, bytes, type.getClassLoader());

         return clazz.newInstance();
      } catch (Exception e) {
         throw new ObjenesisException(e);
      }
   }

   public T newInstance() {
      return instantiator.newInstance();
   }
}
