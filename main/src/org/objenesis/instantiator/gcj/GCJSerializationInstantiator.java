package org.objenesis.instantiator.gcj;
import java.lang.reflect.InvocationTargetException;

import org.objenesis.instantiator.SerializationInstantiatorHelper;

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
