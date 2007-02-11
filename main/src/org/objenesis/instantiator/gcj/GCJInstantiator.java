package org.objenesis.instantiator.gcj;
import java.lang.reflect.InvocationTargetException;

public class GCJInstantiator extends GCJInstantiatorBase {
	public GCJInstantiator(Class type) {
		super(type);
	}
	
	public Object newInstance() {
		if (newObjectMethod == null) {
			return null;
		}
		try {
			return newObjectMethod.invoke(dummyStream, new Object[] {type, Object.class});		
		} catch (IllegalAccessException e) {
			return null;
		} catch (InvocationTargetException e) {
			return null;
		}
	}

}
