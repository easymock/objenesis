package org.objenesis;

import java.io.ObjectStreamClass;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
	
	ObjectStreamClass objStreamClass;
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
