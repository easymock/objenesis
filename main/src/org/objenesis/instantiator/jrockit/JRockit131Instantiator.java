/**
 * COPYRIGHT & LICENSE
 *
 * This code is Copyright (c) 2006 BEA Systems, inc. It is provided free, as-is and without any warranties for the purpose of
 * inclusion in Objenesis or any other open source project with a FSF approved license, as long as this notice is not
 * removed. There are no limitations on modifying or repackaging the code apart from this. 
 *
 * BEA does not guarantee that the code works, and provides no support for it. Use at your own risk.
 *
 * Originally developed by Leonardo Mesquita. Copyright notice added by Henrik Ståhl, BEA JRockit Product Manager.
 *  
 */
package org.objenesis.instantiator.jrockit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.objenesis.instantiator.ObjectInstantiator;

public class JRockit131Instantiator implements ObjectInstantiator {
    
    private Constructor mungedConstructor;

    private static Method newConstructorForSerializationMethod;
    
    private static void initialize() {
    	if(newConstructorForSerializationMethod == null) {
    		Class cl;
			try {
				cl = Class.forName("COM.jrockit.reflect.MemberAccess");
				newConstructorForSerializationMethod = cl.getDeclaredMethod("newConstructorForSerialization", new Class[] {Constructor.class, Class.class});
				newConstructorForSerializationMethod.setAccessible(true);
			} catch (ClassNotFoundException e) {
				newConstructorForSerializationMethod = null;
			} catch (SecurityException e) {
				newConstructorForSerializationMethod = null;
			} catch (NoSuchMethodException e) {
				newConstructorForSerializationMethod = null;
			}
    	}
    }
    
    public JRockit131Instantiator(Class type) {
    	initialize();

    	if(newConstructorForSerializationMethod != null) {
    		
	    	Constructor javaLangObjectConstructor;
	    	
	        try {
	            javaLangObjectConstructor = Object.class.getConstructor((Class[])null);
	        } catch (NoSuchMethodException e) {
	            throw new Error("Cannot find constructor for java.lang.Object!");
	        }
	    	
	        try {
	        	mungedConstructor = (Constructor) newConstructorForSerializationMethod.invoke(null, new Object[] {javaLangObjectConstructor, type});
			} catch (IllegalAccessException e) {
				mungedConstructor = null;
			} catch (InvocationTargetException e) {
				mungedConstructor = null;
			}
    	}

    }

    public Object newInstance() {
    	if(mungedConstructor == null) {
    		return null;
    	}
        try {
            return mungedConstructor.newInstance((Object[])null);
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }
    }
}
