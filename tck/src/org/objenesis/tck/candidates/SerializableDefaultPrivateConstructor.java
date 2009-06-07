package org.objenesis.tck.candidates;

import java.io.Serializable;

public class SerializableDefaultPrivateConstructor implements Serializable {

   private static final long serialVersionUID = 1L;
   
   private SerializableDefaultPrivateConstructor() {

   }
}
