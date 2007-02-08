package org.objenesis;
import java.lang.reflect.InvocationTargetException;

public class GCJInstantiator extends GCJInstantiatorBase {
	public GCJInstantiator(Class type) {
		super(type);
	}
	
	public Object newInstance() {
		if (allocateObjectMethod == null) {
			return null;
		}
		try {
			return allocateObjectMethod.invoke(dummyStream, new Object[] {type});		
		} catch (IllegalAccessException e) {
			return null;
		} catch (InvocationTargetException e) {
			return null;
		}
	}

}
