package org.objenesis.instantiator.basic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.objenesis.instantiator.ObjectInstantiator;

/**
 * Instantiates a class by grabbing the no args constructor and calling Constructor.newInstance().
 *
 * This can deal with default public constructors, but that's about it.
 *
 * @see ObjectInstantiator
 */
public class ConstructorInstantiator implements ObjectInstantiator {

	protected Constructor constructor;
	public ConstructorInstantiator(Class type) {
		try {
			constructor = type.getDeclaredConstructor((Class[])null);
		} catch (SecurityException e) {
			constructor = null;
		} catch (NoSuchMethodException e) {
			constructor = null;
		}
	}
    public Object newInstance() {
    	if (constructor == null) {
    		return null;
    	}
        try {
			return constructor.newInstance((Object[])null);
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		} catch (InvocationTargetException e) {
			return null;
		}
    }

}
