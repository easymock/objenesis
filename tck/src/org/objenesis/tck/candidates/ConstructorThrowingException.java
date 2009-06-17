package org.objenesis.tck.candidates;

/**
 * @author Joe Walnes
 */
public class ConstructorThrowingException {

   public ConstructorThrowingException() {
      throw new IllegalArgumentException("Constructor throwing an exception");
   }

}
