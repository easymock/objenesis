package org.objenesis.tck.features;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisException;

/**
 * Not serializable classes can't be instantiated
 *
 * @author Henri Tremblay
 */
public class NotSerializableClass extends AbstractFeature {

   public static class NotSerializable {
      public NotSerializable() {
         called.add(NotSerializable.class.getSimpleName() + ".<init>");
      }
   }

   @Override
   public boolean isCompliant(Objenesis objenesis) {
      try {
         objenesis.newInstance(NotSerializable.class);
      }
      catch(ObjenesisException e) {
         return true;
      }
      return false;
   }
}
