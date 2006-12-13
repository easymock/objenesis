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

package org.objenesis;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JRockitLegacyInstantiator implements ObjectInstantiator {
	private static Method safeAllocObjectMethod = null;
	private static void initialize() {
		if(safeAllocObjectMethod == null) {
			Class memSystem;
			try {
				memSystem = Class.forName("jrockit.vm.MemSystem");
				safeAllocObjectMethod = memSystem.getDeclaredMethod("safeAllocObject", new Class[] {Class.class});
				safeAllocObjectMethod.setAccessible(true);
			} catch (ClassNotFoundException e) {
				safeAllocObjectMethod = null;
			} catch (SecurityException e) {
				safeAllocObjectMethod = null;
			} catch (NoSuchMethodException e) {
				safeAllocObjectMethod = null;
			}
		}
	}
	private Class type;
	public JRockitLegacyInstantiator(Class type) {
		initialize();
		this.type = type;
	}
	
	public Object newInstance() {
		if(safeAllocObjectMethod == null) {
			return null;
		}
		try {
			return safeAllocObjectMethod.invoke(null, new Object[]{type});
		} catch (IllegalAccessException e) {
			return null;
		} catch (InvocationTargetException e) {
			return null;
		}
	}
}
