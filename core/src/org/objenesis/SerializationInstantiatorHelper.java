package org.objenesis;

import java.io.Serializable;

public class SerializationInstantiatorHelper {
	public static Class getNonSerializableSuperClass(Class type) {
		Class result = type;
		while(Serializable.class.isAssignableFrom(result)) {
			result = result.getSuperclass();
			if(result == null) {
				throw new Error("Bad class hierarchy: No non-serializable parents");
			}
		}
		return result;
		
	}
}
