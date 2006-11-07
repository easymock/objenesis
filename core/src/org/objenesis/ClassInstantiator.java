package org.objenesis;

/**
 * Instantiates objects of a class
 */
public interface ClassInstantiator {
	
	
	/**
	 * Creates a new object of a given class 
	 * @return a new object
	 */
	public Object newInstance();

}
