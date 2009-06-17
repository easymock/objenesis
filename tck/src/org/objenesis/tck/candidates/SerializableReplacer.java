package org.objenesis.tck.candidates;

import java.io.Serializable;

/**
 * @author Joe Walnes
 */
public class SerializableReplacer implements Serializable {
   
   private static final long serialVersionUID = 1L;
   
   protected Object writeReplace() {
      return new SerializableResolver();
   }
}
