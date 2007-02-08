package org.objenesis;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;

public abstract class GCJInstantiatorBase implements ObjectInstantiator {
	protected static Method newObjectMethod = null;
	protected static ObjectInputStream dummyStream;
	private static class DummyStream extends ObjectInputStream {
		public DummyStream() throws IOException {
			super();			
		}		
	}
	private static void initialize() {
		if(newObjectMethod == null) {
			try {
				newObjectMethod = ObjectInputStream.class.getDeclaredMethod(
						"newObject", new Class[] {Class.class, Class.class}
				);
				newObjectMethod.setAccessible(true);
				dummyStream = new DummyStream();
			} catch (SecurityException e) {
				newObjectMethod = null;
			} catch (NoSuchMethodException e) {
				newObjectMethod = null;
			} catch (IOException e) {
				newObjectMethod = null;
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
