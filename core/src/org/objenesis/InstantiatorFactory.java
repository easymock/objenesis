package org.objenesis;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class InstantiatorFactory {
	public static ObjectInstantiator getInstantiator(Class instantiatorClass, Class type) {
		Constructor constructor;
		try {
			constructor = instantiatorClass.getDeclaredConstructor(new Class[] {Class.class});
			return (ObjectInstantiator)constructor.newInstance(new Object[]{type});
		} catch (SecurityException e) {
			throw new UnsupportedOperationException("Security Manager does not allow creation through reflection");
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Bad instantiator class. No constructor found with 'java.lang.Class' argument");		
		} catch (InstantiationException e) {
			throw new RuntimeException("Problems creating instantiator", e);		
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Problems creating instantiator", e);		
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Problems creating instantiator", e);		
		}
	}
}
