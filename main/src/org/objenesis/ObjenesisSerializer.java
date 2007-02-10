package org.objenesis;

/**
 * Class using the {@link SerializingInstantiatorStrategy} by default 
 * 
 * @author Henri Tremblay
 */
public class ObjenesisSerializer extends ObjenesisBase {

	/**
	 * Default constructor using the {@link SerializingInstantiatorStrategy}
	 */
	public ObjenesisSerializer() {
		super(new SerializingInstantiatorStrategy());
	}

	/**
	 * Instance using the {@link SerializingInstantiatorStrategy} with or without caching {@link ObjectInstantiator}s
	 * 
	 * @param useCache If {@link ObjectInstantiator}s should be cached
	 */
	public ObjenesisSerializer(boolean useCache) {
		super(new SerializingInstantiatorStrategy(), useCache);
	}	
}
