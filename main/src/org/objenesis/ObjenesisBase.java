package org.objenesis;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Base class to extend if you want to have a class providing your own default strategy.
 * Can also be instantiated directly.
 * 
 * @author Henri Tremblay
 */
public class ObjenesisBase implements Objenesis {

	protected final InstantiatorStrategy strategy;
	
	protected Map cache;
	
	/**
	 * Constructor allowing to pick a strategy and using cache
	 * 
	 * @param strategy Strategy to use
	 */
	public ObjenesisBase(InstantiatorStrategy strategy) {
		this(strategy, true);
	}
	
	/**
	 * Flexible constructor allowing to pick the strategy and if caching should be used
	 * 
	 * @param strategy Strategy to use
	 * @param useCache If {@link ObjectInstantiator}s should be cached
	 */
	public ObjenesisBase(InstantiatorStrategy strategy, boolean useCache) {
		if(strategy == null) {
			throw new IllegalArgumentException("A strategy can't be null");
		}
		this.strategy = strategy;
		this.cache = useCache ? new WeakHashMap() : null; 
	}	

	public String toString() {
		return getClass().getName() + " using " + strategy.getClass().getName() + 
		(cache == null ? " without" : " with") + " caching"; 
	}

	/**
	 * Will create a new object without any constructor being called
	 * 
	 * @return
	 */
	public Object newInstance(Class clazz) {
		return getInstantiatorOf(clazz).newInstance();
	}

	/**
	 * Will pick the best instantiator for the provided class. If you need to create a lot of
	 * instances from the same class, it is way more efficient to create them from the same
	 * ObjectInstantiator than calling {@link #newInstance(Class)}. 
	 * 
	 * @param clazz Class to instantiate
	 * @return Instantiator dedicated to the class
	 */
	public ObjectInstantiator getInstantiatorOf(Class clazz) {
		ObjectInstantiator instantiator = (ObjectInstantiator) cache.get(clazz);
		if(instantiator == null) {
			instantiator = strategy.newInstantiatorOf(clazz);
			cache.put(clazz, instantiator);
		}
		return instantiator;
	}

}