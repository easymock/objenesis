package org.objenesis.tck.candidates;

import java.io.Serializable;

public class SerializableReplacer implements Serializable {
   protected Object writeReplace() {
      return new SerializableResolver();
   }
}
