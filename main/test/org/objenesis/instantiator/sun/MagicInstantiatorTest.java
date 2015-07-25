package org.objenesis.instantiator.sun;

import org.junit.Test;
import org.objenesis.EmptyClass;
import org.objenesis.instantiator.ObjectInstantiator;

import static org.junit.Assert.assertEquals;

/**
 * @author Henri Tremblay
 */
public class MagicInstantiatorTest {

   @Test
   public void testNewInstance() throws Exception {
      ObjectInstantiator<EmptyClass> o = new MagicInstantiator<EmptyClass>(EmptyClass.class);
      assertEquals(EmptyClass.class, o.newInstance().getClass());
   }
}
