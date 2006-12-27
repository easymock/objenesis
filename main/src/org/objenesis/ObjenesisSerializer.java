package org.objenesis;

/**
 * Class using the {@link SerializingInstantiatorStrategy} by default 
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
	
	/**
	 * Constructor allowing to pick your own strategy and using cache
	 * 
	 * @param strategy Strategy to use
	 */
	public ObjenesisSerializer(InstantiatorStrategy strategy) {
		super(strategy);
	}
	
	/**
	 * Flexible constructor allowing to pick the strategy and if caching should be used
	 * 
	 * @param strategy Strategy to use
	 * @param useCache If {@link ObjectInstantiator}s should be cached
	 */
	public ObjenesisSerializer(InstantiatorStrategy strategy, boolean useCache) {
		super(strategy, useCache); 
	}	
}
