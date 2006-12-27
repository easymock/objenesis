package org.objenesis;

/**
 * Class using the {@link AutomaticInstantiatorStrategy} by default 
 */
public class Objenesis extends ObjenesisBase {

	/**
	 * Default constructor using the {@link AutomaticInstantiatorStrategy}
	 */
	public Objenesis() {
		super(new AutomaticInstantiatorStrategy());
	}

	/**
	 * Instance using the {@link AutomaticInstantiatorStrategy} with or without caching {@link ObjectInstantiator}s
	 * 
	 * @param useCache If {@link ObjectInstantiator}s should be cached
	 */
	public Objenesis(boolean useCache) {
		super(new AutomaticInstantiatorStrategy(), useCache);
	}
	
	/**
	 * Constructor allowing to pick your own strategy and using cache
	 * 
	 * @param strategy Strategy to use
	 */
	public Objenesis(InstantiatorStrategy strategy) {
		super(strategy);
	}
	
	/**
	 * Flexible constructor allowing to pick the strategy and if caching should be used
	 * 
	 * @param strategy Strategy to use
	 * @param useCache If {@link ObjectInstantiator}s should be cached
	 */
	public Objenesis(InstantiatorStrategy strategy, boolean useCache) {
		super(strategy, useCache); 
	}	
}
