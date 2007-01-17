package org.objenesis;


/**
 * Guess the best instantiator for a given class. Currently, the selection doesn't depend on the class. It relies on the
 * <ul>
 * <li>JVM version</li>
 * <li>JVM vendor</li>
 * <li>JVM vendor version</li>
 * </ul>
 * However, instantiators are stateful and so dedicated to their class.
 *
 * @see ObjectInstantiator
 */
public class StdInstantiatorStrategy extends BaseInstantiatorStrategy {
	
	public ObjectInstantiator newInstantiatorOf(Class type) {

		if(VM_VERSION.startsWith("1.3")) {
			if(JVM_NAME.startsWith(JROCKIT)) {
				throw new RuntimeException("Unsupported JVM: " + JVM_NAME + "/" + VM_VERSION);
			}
			return new Sun13Instantiator(type);
		}
		
		if(JVM_NAME.startsWith(GNU)) {
			return new GCJInstantiator(type);
		}
		// It's JVM 1.4 and above since we are not supporting below 1.3
		// This instantiator should also work for JRockit except for old 1.4 JVM
		return new SunReflectionFactoryInstantiator(type);
	}
}
