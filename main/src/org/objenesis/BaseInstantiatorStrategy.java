package org.objenesis;

public abstract class BaseInstantiatorStrategy implements InstantiatorStrategy {

	protected static final String JROCKIT = "BEA JRockit";

	protected static final String GNU = "GNU libgcj";
   
   protected static final String SUN = "Java HotSpot";

	/** JVM version */
	protected static final String VM_VERSION = System.getProperty("java.runtime.version");

	/** Vendor version */
	protected static final String VENDOR_VERSION = System.getProperty("java.vm.version");

	/** Vendor name */
	protected static final String VENDOR = System.getProperty("java.vm.vendor");

	/** JVM name */
	protected static final String JVM_NAME = System.getProperty("java.vm.name");
}