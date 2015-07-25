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
         String className = type.getName() + "$$Objenesis$$ObjectInstantiator$$" + uniquifier.getAndIncrement();

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
