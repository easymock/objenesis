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
			throw new UnsupportedOperationException("Security Manager exception: "+e.getMessage());
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Bad instantiator class. No constructor found with 'java.lang.Class' argument");		
		} catch (InstantiationException e) {
			throw new RuntimeException("InstantiationException creating instantiator of class "+type.getName()+": "+e.getMessage());		
		} catch (IllegalAccessException e) {
			throw new RuntimeException("IlleglAccessException creating instantiator of class "+type.getName()+": "+e.getMessage());		
		} catch (InvocationTargetException e) {
			throw new RuntimeException("InvocationTargetException creating instantiator of class "+type.getName()+": "+e.getMessage());		
		}
	}	
}
