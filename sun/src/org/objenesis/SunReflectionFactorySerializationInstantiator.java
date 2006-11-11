package org.objenesis;

import sun.reflect.ReflectionFactory;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Instantiates an object, WITHOUT calling it's constructor, using internal
 * sun.reflect.ReflectionFactory - a class only available on JDK's that use
 * Sun's 1.4 (or later) Java implementation.
 *
 * This is the best way to instantiate an object without any side effects
 * caused by the constructor - however it is not available on every platform.
 * 
 * @see ObjectInstantiator
 */
public class SunReflectionFactorySerializationInstantiator implements ObjectInstantiator {
    
    private final Constructor mungedConstructor;

    public SunReflectionFactorySerializationInstantiator(Class type) {
    	if(!Serializable.class.isAssignableFrom(type)) {
    		mungedConstructor = null;
    		return;
    	}
    	
    	ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();
    	Class nonSerializableAncestor = type.getSuperclass();
    	while(nonSerializableAncestor != null && Serializable.class.isAssignableFrom(nonSerializableAncestor)) {
    		nonSerializableAncestor = nonSerializableAncestor.getSuperclass();
    	}
    	if(nonSerializableAncestor == null) {
    		mungedConstructor = null;
    		return;
    	}
    	Constructor nonSerializableAncestorConstructor;
		try {
			nonSerializableAncestorConstructor = nonSerializableAncestor.getConstructor((Class[])null);
		} catch (NoSuchMethodException e) {
			mungedConstructor = null;
    		return;
		}    	
        
        mungedConstructor = reflectionFactory.newConstructorForSerialization(type, nonSerializableAncestorConstructor);        
    }

    public Object newInstance() {
    	if(mungedConstructor == null) {
    		return null;
    	}
        try {
            return mungedConstructor.newInstance((Object[])null);
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }
    }
}
