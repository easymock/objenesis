package org.objenesis;

/**
 * Instantiates a new object.
 */
public interface ObjectInstantiator {

    Object instantiate(Class type);
    
}
