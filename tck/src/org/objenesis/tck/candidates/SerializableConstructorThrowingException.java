package org.objenesis.tck.candidates;

import java.io.Serializable;

public class SerializableConstructorThrowingException implements Serializable {

   private static final long serialVersionUID = 1L;
   
   public SerializableConstructorThrowingException() {
      throw new IllegalArgumentException("Constructor throwing an exception");
   }

}
