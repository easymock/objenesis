package org.objenesis.instantiator.sun;

import sun.reflect.ReflectionFactory;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.objenesis.instantiator.ObjectInstantiator;

/**
 * Instantiates an object using internal sun.reflect.ReflectionFactory - a class only available on
 * JDK's that use Sun's 1.4 (or later) Java implementation. This instantiator will create classes in
 * a way compatible with serialization, calling the first non-serializable superclass' no-arg
 * constructor. This is the best way to instantiate an object without any side effects caused by the
 * constructor - however it is not available on every platform.
 * 
 * @see ObjectInstantiator
 */
public class SunReflectionFactorySerializationInstantiator implements ObjectInstantiator {

   private final Constructor mungedConstructor;

   public SunReflectionFactorySerializationInstantiator(Class type) {
      ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();
      Class nonSerializableAncestor = type.getSuperclass();
      while(nonSerializableAncestor != null
         && Serializable.class.isAssignableFrom(nonSerializableAncestor)) {
         nonSerializableAncestor = nonSerializableAncestor.getSuperclass();
      }
      if(nonSerializableAncestor == null) {
         /** @todo: (Henri) Can't happen, Object is not Serializable */
         mungedConstructor = null;
         return;
      }
      Constructor nonSerializableAncestorConstructor;
      try {
         nonSerializableAncestorConstructor = nonSerializableAncestor
            .getConstructor((Class[]) null);
      }
      catch(NoSuchMethodException e) {
         /**
          * @todo (Henri) I think we should throw a NotSerializableException just to put the same
          *       message a ObjectInputStream. Otherwise, the user won't know if the null returned
          *       if a "Not serializable", a "No default constructor on ancestor" or a "Exception in
          *       constructor"
          */
         mungedConstructor = null;
         return;
      }

      mungedConstructor = reflectionFactory.newConstructorForSerialization(type,
         nonSerializableAncestorConstructor);
      mungedConstructor.setAccessible(true);
   }

   public Object newInstance() {
      try {
         return mungedConstructor.newInstance((Object[]) null);
      }
      catch(InstantiationException e) {
         /** @todo (Henri) See todo above */
         return null;
      }
      catch(IllegalAccessException e) {
         return null;
      }
      catch(InvocationTargetException e) {
         return null;
      }
   }
}
