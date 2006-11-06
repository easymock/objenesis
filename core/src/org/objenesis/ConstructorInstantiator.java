package org.objenesis;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Instantiates a class by grabbing the no args constructor and calling Constructor.newInstance().
 *
 * This can deal with default public constructors, but that's about it.
 *
 * @see ObjectInstantiator
 */
public class ConstructorInstantiator implements ObjectInstantiator {

    public Object instantiate(Class type) {
        try {
            Constructor constructor = type.getDeclaredConstructor(null);
            return constructor.newInstance(null);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }
    }

}
