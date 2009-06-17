package org.objenesis.tck.candidates;

import java.io.Serializable;

/**
 * @author Joe Walnes
 */
public class SerializableResolver implements Serializable {
   
   private static final long serialVersionUID = 1L;
   
   protected Object readResolve() {
      return new SerializableReplacer();
   }
}
