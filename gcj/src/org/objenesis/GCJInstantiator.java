package org.objenesis;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GCJInstantiator implements ObjectInstantiator {
	private static Method allocateObjectMethod = null;
	private static ObjectInputStream dummyStream;
	private static class DummyStream extends ObjectInputStream {
		public DummyStream() throws IOException {
			super();			
		}		
	}
	private static void initialize() {
		if(allocateObjectMethod == null) {
			try {
				allocateObjectMethod = ObjectInputStream.class.getDeclaredMethod(
						"allocateObject", new Class[] {Class.class}
				);
				allocateObjectMethod.setAccessible(true);
				dummyStream = new DummyStream();
			} catch (SecurityException e) {
				allocateObjectMethod = null;
			} catch (NoSuchMethodException e) {
				allocateObjectMethod = null;
			} catch (IOException e) {
				allocateObjectMethod = null;
			}
		}
	}
	private final Class type;
	public GCJInstantiator(Class type) {
		this.type = type;
		initialize();
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
