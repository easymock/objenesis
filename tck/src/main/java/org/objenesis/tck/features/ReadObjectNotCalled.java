package org.objenesis.tck.features;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import org.objenesis.Objenesis;

/**
 * @author Henri Tremblay
 */
public class ReadObjectNotCalled extends AbstractFeature {

   public static class ReadObjectAndAll implements Serializable {
      private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         called.add("readObject");
      }

      private Object readResolve() throws ObjectStreamException {
         called.add("readResolve");
         return this;
      }

      private void readObjectNoData() throws ObjectStreamException {
         called.add("readObjectNoData");
      }
   }

   @Override
   public boolean isCompliant(Objenesis objenesis) {
      objenesis.newInstance(ReadObjectAndAll.class);
      return called.isEmpty();
   }
}
