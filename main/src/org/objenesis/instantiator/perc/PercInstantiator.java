package org.objenesis.instantiator.perc;

import java.io.ObjectInputStream;
import java.lang.reflect.Method;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 * Instantiates a class by making a call to internal Aonix PERC private methods. 
 * This instantiator will not call any constructors. Tested with perc-linux-5.0.0667.i386
 * 
 * @author Henri Tremblay
 * @see org.objenesis.instantiator.ObjectInstantiator
 */
public class PercInstantiator implements ObjectInstantiator {

	private final Method newInstanceMethod;

	private final Object[] typeArgs = new Object[] { null, Boolean.FALSE };

	public PercInstantiator(Class type) {

		typeArgs[0] = type;

		try {
			newInstanceMethod = ObjectInputStream.class.getDeclaredMethod("newInstance",
					new Class[] { Class.class, Boolean.TYPE });
			newInstanceMethod.setAccessible(true);
		} catch (Exception e) {
			throw new ObjenesisException(e);
		}
	}

	public Object newInstance() {
		try {
			return newInstanceMethod.invoke(null, typeArgs);
		} catch (Exception e) {
			throw new ObjenesisException(e);
		}
	}

}
