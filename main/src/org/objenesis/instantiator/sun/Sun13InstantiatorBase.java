package org.objenesis.instantiator.sun;

import java.io.ObjectInputStream;
import java.lang.reflect.Method;

import org.objenesis.instantiator.ObjectInstantiator;

public abstract class Sun13InstantiatorBase implements ObjectInstantiator {
	protected static Method allocateNewObjectMethod = null;
	
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
	protected final Class type;
	public Sun13InstantiatorBase(Class type) {
		this.type = type;
		initialize();
	}
	
	public abstract Object newInstance();	
	
}
