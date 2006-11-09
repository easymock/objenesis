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
				throw new UnsupportedOperationException("The SecurityManager does not allow the use of "
						+Sun13Instantiator.class.getName());
			} catch (NoSuchMethodException e) {
				throw new UnsupportedOperationException("This VM does not support "
						+Sun13Instantiator.class.getName());
			}
		}
	}
	
	static {
		initialize();
	}
	
	public Object instantiate(Class type) {
		
		try {
			return allocateNewObjectMethod.invoke(null, new Object[] {type, Object.class});		
		} catch (IllegalAccessException e) {
			return null;
		} catch (InvocationTargetException e) {
			return null;
		}
	}
	
	
}
