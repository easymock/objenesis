package org.objenesis.tck.candidates;

import java.io.Serializable;

public class SerializableConstructorWithMandatoryArguments implements Serializable {

   public SerializableConstructorWithMandatoryArguments(String something) {
      if(something == null) {
         throw new IllegalArgumentException("Need arguments");
      }
   }
}
