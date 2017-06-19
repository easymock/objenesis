package org.objenesis.tck.features;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import org.objenesis.Objenesis;

/**
 * @author Henri Tremblay
 */
public class ReadExternalNotCalled extends AbstractFeature {

   public static class ReadExternalAndAll implements Serializable, Externalizable {
      @Override
      public void writeExternal(ObjectOutput out) throws IOException {
         called.add("writeExternal");
      }

      @Override
      public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
         called.add("readExternal");
      }
   }

   @Override
   public boolean isCompliant(Objenesis objenesis) {
      objenesis.newInstance(ReadExternalAndAll.class);
      return called.isEmpty();
   }
}
