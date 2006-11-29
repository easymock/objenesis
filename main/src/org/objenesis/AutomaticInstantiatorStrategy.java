package org.objenesis;


/**
 * Guess the best instantiator for a given class. Currently, the selection doesn't depend on the class. It relies on the
 * <ul>
 * <li>JVM version</li>
 * <li>JVM vendor</li>
 * <li>JVM vendor version</li>
 * </ul>
 * However, instantiator a stateful and so dedicated to their class.
 *
 * @see ObjectInstantiator
 */
public class AutomaticInstantiatorStrategy implements InstantiatorStrategy {
	
	private static final String JROCKIT = "BEA JRockit";	
	
	/** JVM version */
	private static final String VM_VERSION = System.getProperty("java.runtime.version");
	
	/** Vendor version */
	private static final String VENDOR_VERSION = System.getProperty("java.vm.version");
	
	/** Vendor name */
	private static final String VENDOR = System.getProperty("java.vm.vendor");
	
	/** JVM name */
	private static final String JVM_NAME = System.getProperty("java.vm.name");
	
	public ObjectInstantiator newInstantiatorOf(Class type) {

		if(VM_VERSION.startsWith("1.3")) {
			if(JVM_NAME.startsWith(JROCKIT)) {
				throw new RuntimeException("Unsupported JVM: " + JVM_NAME + "/" + VM_VERSION);
			}
			return new Sun13Instantiator(type);
		}
		// It's JVM 1.4 and above since we are not supporting below 1.3
		// This instantiator should also work for JRockit except for old 1.4 JVM
		return new SunReflectionFactoryInstantiator(type);
	}
}
