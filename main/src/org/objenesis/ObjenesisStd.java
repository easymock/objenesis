package org.objenesis;

/**
 * Class using the {@link StdInstantiatorStrategy} by default 
 */
public class ObjenesisStd extends ObjenesisBase {

	/**
	 * Default constructor using the {@link StdInstantiatorStrategy}
	 */
	public ObjenesisStd() {
		super(new StdInstantiatorStrategy());
	}

	/**
	 * Instance using the {@link StdInstantiatorStrategy} with or without caching {@link ObjectInstantiator}s
	 * 
	 * @param useCache If {@link ObjectInstantiator}s should be cached
	 */
	public ObjenesisStd(boolean useCache) {
		super(new StdInstantiatorStrategy(), useCache);
	}
	
	/**
	 * Constructor allowing to pick your own strategy and using cache
	 * 
	 * @param strategy Strategy to use
	 */
	public ObjenesisStd(InstantiatorStrategy strategy) {
		super(strategy);
	}
	
	/**
	 * Flexible constructor allowing to pick the strategy and if caching should be used
	 * 
	 * @param strategy Strategy to use
	 * @param useCache If {@link ObjectInstantiator}s should be cached
	 */
	public ObjenesisStd(InstantiatorStrategy strategy, boolean useCache) {
		super(strategy, useCache); 
	}	
}
