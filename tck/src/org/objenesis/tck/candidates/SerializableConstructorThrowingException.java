package org.objenesis.tck.candidates;

import java.io.Serializable;

public class SerializableConstructorThrowingException implements Serializable {

   public SerializableConstructorThrowingException() {
      throw new IllegalArgumentException("Constructor throwing an exception");
   }

}
