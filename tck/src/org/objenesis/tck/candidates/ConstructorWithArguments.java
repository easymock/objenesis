package org.objenesis.tck.candidates;

/**
 * @author Joe Walnes
 */
public class ConstructorWithArguments {

   private final String something;
   private final int another;

   public ConstructorWithArguments(String something, int another) {
      this.something = something;
      this.another = another;
   }

   public String toString() {
      return something + another;
   }
}
