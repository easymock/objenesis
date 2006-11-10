package org.objenesis;

import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Sun13Instantiator implements ObjectInstantiator {
	private static Method allocateNewObjectMethod = null;
	
	private static void initialize() {
		if(allocateNewObjectMethod == null) {
			try {
				allocateNewObjectMethod = ObjectInputStream.class.getDeclaredMethod(
						"allocateNewObject", new Class[] {Class.class, Class.class}
				);
				allocateNewObjectMethod.setAccessible(true);
			} catch (SecurityException e) {
				// Will keep the allocateNewMethod as null
			} catch (NoSuchMethodException e) {
				// Will keep the allocateNewMethod as null
			}
		}
	}
	private final Class type;
	public Sun13Instantiator(Class type) {
		this.type = type;
		initialize();
	}
	
	public Object newInstance() {
		if (allocateNewObjectMethod == null) {
			return null;
		}
		try {
			return allocateNewObjectMethod.invoke(null, new Object[] {type, Object.class});		
		} catch (IllegalAccessException e) {
			return null;
		} catch (InvocationTargetException e) {
			return null;
		}
	}
	
	
}
