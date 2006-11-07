package org.objenesis;

import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Instantiates an object, WITHOUT calling it's constructor, using internal
 * sun.reflect.ReflectionFactory - a class only available on JDK's that use
 * Sun's 1.4 (or later) Java implementation.
 *
 * This is the best way to instantiate an object without any side effects
 * caused by the constructor - however it is not available on every platform.
 * 
 * @see ObjectInstantiator
 */
public class SunReflectionFactoryInstantiator implements ObjectInstantiator {

    private final ReflectionFactory reflectionFactory;
    private final Constructor javaLangObjectConstructor;

    public SunReflectionFactoryInstantiator() {
        reflectionFactory = ReflectionFactory.getReflectionFactory();
        try {
            javaLangObjectConstructor = Object.class.getConstructor((Class[])null);
        } catch (NoSuchMethodException e) {
            throw new Error("Cannot find constructor for java.lang.Object!");
        }
    }

    public Object instantiate(Class type) {
        Constructor magicConstructor =
                reflectionFactory.newConstructorForSerialization(type, javaLangObjectConstructor);
        try {
            return magicConstructor.newInstance((Object[])null);
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }
    }
}
