package org.objenesis.instantiator.gcj;
import java.lang.reflect.InvocationTargetException;

import org.objenesis.instantiator.SerializationInstantiatorHelper;

/**
 * Instantiates a class by making a call to internal GCJ private methods.
 * It is only supposed to work on GCJ JVMs.
 * This instantiator will create classes in a way compatible with serialization,
 * calling the first non-serializable superclass' no-arg constructor.
 * 
 * @author Leonardo Mesquita
 * @see org.objenesis.instantiator.ObjectInstantiator
 */
public class GCJSerializationInstantiator extends GCJInstantiatorBase {
	private Class superType;
	public GCJSerializationInstantiator(Class type) {
		super(type);
		this.superType = SerializationInstantiatorHelper.getNonSerializableSuperClass(type);
	}
	
	public Object newInstance() {
		if (newObjectMethod == null) {
			return null;
		}
		try {
			return newObjectMethod.invoke(dummyStream, new Object[] {type, superType});		
		} catch (IllegalAccessException e) {
			return null;
		} catch (InvocationTargetException e) {
			return null;
		}
	}

}
