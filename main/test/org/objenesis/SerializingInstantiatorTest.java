package org.objenesis;

import java.io.NotSerializableException;

import junit.framework.TestCase;

/**
 * @author Henri Tremblay
 * @author Leonardo Mesquita
 */
public class SerializingInstantiatorTest extends TestCase {

   protected void setUp() throws Exception {
      super.setUp();
   }

   protected void tearDown() throws Exception {
      super.tearDown();
   }

   public void testNotSerializable() {
      ObjenesisSerializer o = new ObjenesisSerializer();
      try {
    	  o.newInstance(Object.class);
    	  fail("Should have thrown an exception");
      } catch (ObjenesisException e) {
    	  assertTrue(e.getCause() instanceof NotSerializableException);
      }
   }
}
