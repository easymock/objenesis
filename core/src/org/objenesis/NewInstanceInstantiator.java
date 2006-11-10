package org.objenesis;

/**
 * The simplest instantiator - simply calls Class.newInstance().
 *
 * This can deal with default public constructors, but that's about it.
 *
 * @see ObjectInstantiator
 */
public class NewInstanceInstantiator implements ObjectInstantiator {

	private final Class type;
	public NewInstanceInstantiator(Class type) {
		this.type = type;
	}
    public Object newInstance() {
        try {
			return type.newInstance();
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		} catch(Exception e) {
			// java.lang.Class#newInstance() does not throw InvocationTargetException.
			// Instead, it will throw the exception that was thrown originally by the constructor.
			return null;
		}
    }
    
}
