package org.objenesis;

import java.util.Map;
import java.util.WeakHashMap;


public class Objenesis {

	private final InstantiatorStrategy strategy;
	
	private Map cache;
	
	/**
	 * Default constructor using the {@link AutomaticInstantiatorStrategy}
	 */
	public Objenesis() {
		this(new AutomaticInstantiatorStrategy());
	}

	/**
	 * Instance using the {@link AutomaticInstantiatorStrategy} with or without caching {@link ObjectInstantiator}s
	 * 
	 * @param useCache If {@link ObjectInstantiator}s should be cached
	 */
	public Objenesis(boolean useCache) {
		this(new AutomaticInstantiatorStrategy(), useCache);
	}
	
	/**
	 * Constructor allowing to pick your own strategy and using cache
	 * 
	 * @param strategy Strategy to use
	 */
	public Objenesis(InstantiatorStrategy strategy) {
		this(strategy, true);
	}
	
	/**
	 * Flexible constructor allowing to pick the strategy and if caching should be used
	 * 
	 * @param strategy Strategy to use
	 * @param useCache If {@link ObjectInstantiator}s should be cached
	 */
	public Objenesis(InstantiatorStrategy strategy, boolean useCache) {
		if(strategy == null) {
			throw new IllegalArgumentException("A strategy can't be null");
		}
		this.strategy = strategy;
		this.cache = useCache ? new WeakHashMap() : null; 
	}
	
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(!obj.getClass().equals(getClass())) {
			return false;
		}
		Objenesis o = (Objenesis) obj;
		// If it's the same class, then it's the same since they are theorically stateless 
		if(!strategy.getClass().equals(o.strategy.getClass())) {
			return false;
		}
		// The content of the cache doesn't matter, just the fact that cache is used or not
		return (cache == null && o.cache == null) || (cache != null && o.cache != null);
	}

	public int hashCode() {
		return strategy.getClass().hashCode();
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
		return newInstantiatorOf(clazz).newInstance();
	}	
	
	/**
	 * Will pick the best instantiator for the provided class. If you need to create a lot of
	 * instances from the same class, it is way more efficient to create them from the same
	 * ObjectInstantiator than calling {@link #newInstance(Class)}. 
	 * 
	 * @param clazz Class to instantiate
	 * @return Instantiator dedicated to the class
	 */
	public ObjectInstantiator newInstantiatorOf(Class clazz) {
		ObjectInstantiator instantiator = (ObjectInstantiator) cache.get(clazz);
		if(instantiator == null) {
			instantiator = strategy.newInstantiatorOf(clazz);
			cache.put(clazz, instantiator);
		}
		return instantiator;
	}	
}
