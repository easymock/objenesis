package org.objenesis.instantiator.basic;

import org.junit.Test;

import java.io.InputStream;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * @author Henri Tremblay
 */
public class ClassDefinitionUtilsTest {

   @Test
   public void testDefineClass() throws Exception {
      String className = "org.objenesis.EmptyClass";
      byte[] b = readClass();
      Class<?> c = ClassDefinitionUtils.defineClass(className, b, getClass().getClassLoader());
      assertEquals(c.getName(), className);
   }

   private byte[] readClass() throws Exception {
      byte[] b = new byte[1000];
      int length;
      InputStream in = getClass().getResourceAsStream("/org/objenesis/EmptyClass.class");
      try {
         length = in.read(b);
      }
      finally {
         in.close();
      }
      return Arrays.copyOf(b, length);
   }
}
