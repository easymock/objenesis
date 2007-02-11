package org.objenesis.instantiator.sun;

import java.lang.reflect.InvocationTargetException;

public class Sun13Instantiator extends Sun13InstantiatorBase {
	public Sun13Instantiator(Class type) {
		super(type);
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
