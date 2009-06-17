package org.objenesis.tck.candidates;

import java.io.Serializable;

/**
 * @author Joe Walnes
 */
public class SerializableConstructorWithMandatoryArguments implements Serializable {

   private static final long serialVersionUID = 1L;
   
   public SerializableConstructorWithMandatoryArguments(String something) {
      if(something == null) {
         throw new IllegalArgumentException("Need arguments");
      }
   }
}
