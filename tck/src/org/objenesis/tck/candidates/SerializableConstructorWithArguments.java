package org.objenesis.tck.candidates;

import java.io.Serializable;

public class SerializableConstructorWithArguments implements Serializable {

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
