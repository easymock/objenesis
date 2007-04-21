package org.objenesis;

import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.strategy.InstantiatorStrategy;

import junit.framework.TestCase;

public class ObjenesisTest extends TestCase {

   public final void testObjenesis() {
      Objenesis o = new ObjenesisStd();
      assertEquals(
         "org.objenesis.ObjenesisStd using org.objenesis.strategy.StdInstantiatorStrategy with caching",
         o.toString());
   }

   public final void testObjenesis_WithoutCache() {
      Objenesis o = new ObjenesisStd(false);
      assertEquals(
         "org.objenesis.ObjenesisStd using org.objenesis.strategy.StdInstantiatorStrategy without caching",
         o.toString());

      assertEquals(o.getInstantiatorOf(getClass()).newInstance().getClass(), getClass());
   }

   public final void testNewInstance() {
      Objenesis o = new ObjenesisStd();
      assertEquals(getClass(), o.newInstance(getClass()).getClass());
   }

   public final void testGetInstantiatorOf() {
      Objenesis o = new ObjenesisStd();
      ObjectInstantiator i1 = o.getInstantiatorOf(getClass());
      // Test instance creation
      assertEquals(getClass(), i1.newInstance().getClass());

      // Test caching
      ObjectInstantiator i2 = o.getInstantiatorOf(getClass());
      assertSame(i1, i2);
   }

   public final void testToString() {
      Objenesis o = new ObjenesisStd() {};
      assertEquals(
         "org.objenesis.ObjenesisTest$1 using org.objenesis.strategy.StdInstantiatorStrategy with caching",
         o.toString());
   }
}

class MyStrategy implements InstantiatorStrategy {
   public ObjectInstantiator newInstantiatorOf(Class type) {
      return null;
   }
}
