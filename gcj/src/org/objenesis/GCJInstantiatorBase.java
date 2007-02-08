package org.objenesis;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;

public abstract class GCJInstantiatorBase implements ObjectInstantiator {
	protected static Method allocateObjectMethod = null;
	protected static ObjectInputStream dummyStream;
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
	protected final Class type;
	public GCJInstantiatorBase(Class type) {
		this.type = type;
		initialize();
	}
	
	public abstract Object newInstance();
}
