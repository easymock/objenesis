package org.objenesis;

/**
 * The simplest instantiator - simply calls Class.newInstance().
 *
 * This can deal with default public constructors, but that's about it.
 *
 * @see ObjectInstantiator
 */
public class NewInstanceInstantiator implements ObjectInstantiator {

    public Object instantiate(Class type) {
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }
    
}
