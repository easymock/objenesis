package org.objenesis.tck.candidates;

import java.io.Serializable;

/**
 * @author Joe Walnes
 */
public class SerializableConstructorWithArguments implements Serializable {

   private static final long serialVersionUID = 1L;
   
   private final String something;
   private final int another;

   public SerializableConstructorWithArguments(String something, int another) {
      this.something = something;
      this.another = another;
   }

   public String toString() {
      return something + another;
   }
}
