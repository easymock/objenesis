package org.objenesis.instantiator.basic;

import java.io.ObjectStreamClass;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.objenesis.instantiator.ObjectInstantiator;

/**
 * Instantiates a class by using reflection to make a call to private method 
 * ObjectStreamClass.newInstance, present in many JVM implementations.
 * This instantiator will create classes in a way compatible with serialization,
 * calling the first non-serializable superclass' no-arg constructor.
 * 
 * @author Leonardo Mesquita
 * @see ObjectInstantiator
 * @see java.io.Serializable
 */
public class ObjectStreamClassInstantiator implements ObjectInstantiator {
	
	private static Method newInstanceMethod;
	
	private static void initialize() {
		if (newInstanceMethod == null) {
			try {
	 			newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("newInstance", new Class[] {});
				newInstanceMethod.setAccessible(true);
			} catch (SecurityException e) {
				newInstanceMethod = null;
			} catch (NoSuchMethodException e) {
				newInstanceMethod = null;
			}
		}
	}
	
	private ObjectStreamClass objStreamClass;
	
	public ObjectStreamClassInstantiator(Class type) {
		initialize();
		objStreamClass = ObjectStreamClass.lookup(type);
	}
	
	public Object newInstance() {
		if (newInstanceMethod == null) {
			return null;
		}
		try {
			return newInstanceMethod.invoke(objStreamClass, new Object[]{});
		} catch (IllegalArgumentException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		} catch (InvocationTargetException e) {
			return null;
		}
	}

}
