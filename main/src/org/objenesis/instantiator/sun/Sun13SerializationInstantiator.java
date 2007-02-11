package org.objenesis.instantiator.sun;

import java.lang.reflect.InvocationTargetException;

import org.objenesis.instantiator.SerializationInstantiatorHelper;

public class Sun13SerializationInstantiator extends Sun13InstantiatorBase {
	private final Class superType;
	public Sun13SerializationInstantiator(Class type) {
		super(type);
		this.superType = SerializationInstantiatorHelper.getNonSerializableSuperClass(type);		
	}
	
	public Object newInstance() {
		if (allocateNewObjectMethod == null) {
			return null;
		}
		try {
			return allocateNewObjectMethod.invoke(null, new Object[] {type, superType});		
		} catch (IllegalAccessException e) {
			return null;
		} catch (InvocationTargetException e) {
			return null;
		}
	}
	
	
}
