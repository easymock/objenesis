package org.objenesis;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Instantiates a class by grabbing the no-args constructor, making it accessible and then
 * calling Constructor.newInstance().
 *
 * Although this still requires no-arg constructors, it can call non-public constructors
 * (if the security manager allows it).
 *
 * @see ClassInstantiator
 */
public class AccessibleClassInstantiator implements ClassInstantiator {
	private Constructor constructor;
	public AccessibleClassInstantiator(Class type) {
		try {
			this.constructor = type.getDeclaredConstructor((Class[])null);
			// This will allow access to non-public constructors
			// Also, setting reflection objects as "accessible" usually
			// makes the access a lot faster.
			this.constructor.setAccessible(true);
		} catch (SecurityException e) {
			this.constructor = null;
		} catch (NoSuchMethodException e) {
			this.constructor = null;
		}		
	}
	
	public Object newInstance() {
		if(this.constructor == null) {
			return null;
		}
		
		try {
			return this.constructor.newInstance((Object[])null);
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		} catch (InvocationTargetException e) {
			return null;
		}
	}

}
