package org.objenesis.tck.candidates;

import java.io.Serializable;

/**
 * @author Joe Walnes
 */
public class SerializableWithAncestorThrowingException extends ConstructorThrowingException
   implements Serializable {

   private static final long serialVersionUID = 1L;
   
   public SerializableWithAncestorThrowingException() {

   }
}
