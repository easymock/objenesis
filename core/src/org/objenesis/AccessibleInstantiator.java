package org.objenesis;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Instantiates a class by grabbing the no-args constructor, making it accessible and then
 * calling Constructor.newInstance().
 *
 * Although this still requires no-arg constructors, it can call non-public constructors
 * (if the security manager allows it).
 *
 * @see ObjectInstantiator
 */
public class AccessibleInstantiator implements ObjectInstantiator {

    public Object instantiate(Class type) {
        try {
            Constructor constructor = type.getDeclaredConstructor(null);
            constructor.setAccessible(true);
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
