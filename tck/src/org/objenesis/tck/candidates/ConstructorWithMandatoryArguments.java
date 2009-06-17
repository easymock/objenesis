package org.objenesis.tck.candidates;

/**
 * @author Joe Walnes
 */
public class ConstructorWithMandatoryArguments {

   public ConstructorWithMandatoryArguments(String something) {
      if(something == null) {
         throw new IllegalArgumentException("Need arguments");
      }
   }
}
