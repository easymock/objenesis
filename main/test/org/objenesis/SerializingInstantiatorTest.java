package org.objenesis;

import junit.framework.TestCase;

public class SerializingInstantiatorTest extends TestCase {

   protected void setUp() throws Exception {
      super.setUp();
   }

   protected void tearDown() throws Exception {
      super.tearDown();
   }

   public void testNotSerializable() {
      ObjenesisSerializer o = new ObjenesisSerializer();
      assertNull(o.newInstance(Object.class));
   }
}
