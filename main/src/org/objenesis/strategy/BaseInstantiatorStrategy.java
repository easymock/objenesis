package org.objenesis.strategy;

public abstract class BaseInstantiatorStrategy implements InstantiatorStrategy {

	protected static final String JROCKIT = "BEA";

	protected static final String GNU = "GNU libgcj";
   
   protected static final String SUN = "Java HotSpot";

	/** JVM version */
	protected static final String VM_VERSION = System.getProperty("java.runtime.version");

	/** JVM version */
	protected static final String VM_INFO = System.getProperty("java.vm.info");

	/** Vendor version */
	protected static final String VENDOR_VERSION = System.getProperty("java.vm.version");

	/** Vendor name */
	protected static final String VENDOR = System.getProperty("java.vm.vendor");

	/** JVM name */
	protected static final String JVM_NAME = System.getProperty("java.vm.name");
}