package org.objenesis;

import java.io.Serializable;

/**
 * Guess the best serializing instantiator for a given class. Currently, the
 * selection doesn't depend on the class. It relies on the
 * <ul>
 * <li>JVM version</li>
 * <li>JVM vendor</li>
 * <li>JVM vendor version</li>
 * </ul>
 * However, instantiators are stateful and so dedicated to their class.
 * 
 * @see ObjectInstantiator
 */
public class SerializingInstantiatorStrategy extends BaseInstantiatorStrategy {

	/**
	 * Create a Serializable object calling the last none-serializable constructor.
	 * 
	 * @param type Class to instantiate
	 * @return The newly created class. Null is the type isn't serializable
	 */
	public ObjectInstantiator newInstantiatorOf(Class type) {
		if(!Serializable.class.isAssignableFrom(type)) {
			return new NullInstantiator();
		}
		return new ObjectStreamClassInstantiator(type);
	}

}
