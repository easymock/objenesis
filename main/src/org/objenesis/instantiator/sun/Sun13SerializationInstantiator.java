package org.objenesis.instantiator.sun;

import java.lang.reflect.InvocationTargetException;

import org.objenesis.instantiator.SerializationInstantiatorHelper;

/**
 * Instantiates a class by making a call to internal Sun private methods. It is only supposed to
 * work on Sun HotSpot 1.3 JVM. This instantiator will create classes in a way compatible with
 * serialization, calling the first non-serializable superclass' no-arg constructor.
 * 
 * @author Leonardo Mesquita
 * @see org.objenesis.instantiator.ObjectInstantiator
 */
public class Sun13SerializationInstantiator extends Sun13InstantiatorBase {
   private final Class superType;

   public Sun13SerializationInstantiator(Class type) {
      super(type);
      this.superType = SerializationInstantiatorHelper.getNonSerializableSuperClass(type);
   }

   public Object newInstance() {
      if(allocateNewObjectMethod == null) {
         return null;
      }
      try {
         return allocateNewObjectMethod.invoke(null, new Object[] {type, superType});
      }
      catch(IllegalAccessException e) {
         return null;
      }
      catch(InvocationTargetException e) {
         return null;
      }
   }

}
