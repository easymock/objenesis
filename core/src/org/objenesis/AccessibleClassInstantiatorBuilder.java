package org.objenesis;

public class AccessibleClassInstantiatorBuilder implements ClassInstantiatorBuilder {
	public ClassInstantiator newInstantiatorOf(Class type) {		
		return new AccessibleClassInstantiator(type);
	}

}
