package org.objenesis.strategy;

import java.util.AbstractList;
import org.junit.Test;

public class StdInstantiatorTest {
   @Test
   public void testAbstract() {
      // Can't run on Android until https://github.com/easymock/objenesis/issues/68 is fixed
      if(!PlatformDescription.isThisJVM(PlatformDescription.DALVIK)) {
         new StdInstantiatorStrategy().newInstantiatorOf(AbstractList.class).newInstance();
      }
   }
}
