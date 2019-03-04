package org.objenesis.strategy;

import java.util.AbstractList;
import org.junit.Assume;
import org.junit.Test;
import org.objenesis.instantiator.ObjectInstantiator;

public class StdInstantiatorTest {
   @Test
   public void testAbstract() {
      // Can't run on Android until https://github.com/easymock/objenesis/issues/68 is fixed
      Assume.assumeFalse(PlatformDescription.isThisJVM(PlatformDescription.DALVIK));
      ObjectInstantiator<AbstractList> instantiator =
         new StdInstantiatorStrategy().newInstantiatorOf(AbstractList.class);
      instantiator.newInstance();
   }
}
