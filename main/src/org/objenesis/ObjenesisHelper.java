package org.objenesis;

import java.io.Serializable;

/**
 * Easiest way to create a new class instance.
 */
public final class ObjenesisHelper {

	private static final Objenesis OBJENESIS = new Objenesis();
	
	private static final ObjenesisSerializer OBJENESIS_SERIALIZER = new ObjenesisSerializer();
	
	private ObjenesisHelper() {}
	
	/**
	 * Will create a new object without any constructor being called
	 * 
	 * @return
	 */
	public static final Object newInstance(Class clazz) {
		return OBJENESIS.newInstance(clazz);
	}
	
	/**
	 * Will create an object just like it's done by ObjectInputStream.readObject
	 * (the default constructor of the first non serializable class will be called)
	 * 
	 * @return
	 */
	public static final Serializable newSerializableInstance(Class clazz) {
		return (Serializable) OBJENESIS_SERIALIZER.newInstance(clazz);
	}
	
	/**
	 * Will pick the best instantiator for the provided class. If you need to create a lot of
	 * instances from the same class, it is way more efficient to create them from the same
	 * ObjectInstantiator than calling {@link #newInstance(Class)}. 
	 * 
	 * @param clazz Class to instantiate
	 * @return Instantiator dedicated to the class
	 */
	public static final ObjectInstantiator newInstantiatorOf(Class clazz) {
		return OBJENESIS.newInstantiatorOf(clazz);	
	}
	
	/**
	 * Same as {@link #newInstantiatorOf(Class)} but providing an instantiator emulating
	 * ObjectInputStream.readObject behavior.
	 * 
	 * @see #newSerializableInstance(Class)
	 *  
	 * @param clazz Class to instantiate
	 * @return Instantiator dedicated to the class
	 */
	public static final ObjectInstantiator newSerializableObjectInstantiatorOf(Class clazz) {
		return OBJENESIS_SERIALIZER.newInstantiatorOf(clazz);;
	}	
}
