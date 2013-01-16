/**
 * Copyright 2006-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.objenesis.strategy;

/**
 * Base {@link InstantiatorStrategy} class basically containing helpful constant to sort out JVMs.
 * 
 * @author Henri Tremblay
 */
public abstract class BaseInstantiatorStrategy implements InstantiatorStrategy {

   /** JVM_NAME prefix for JRockit */
   protected static final String JROCKIT = "BEA";

   /** JVM_NAME prefix for GCJ */
   protected static final String GNU = "GNU libgcj";

   /** JVM_NAME prefix for Sun Java HotSpot */
   protected static final String SUN = "Java HotSpot";

   /** JVM_NAME prefix for Aonix PERC */
   protected static final String PERC = "PERC";
   
   /** JVM_NAME prefix for Dalvik/Android */
   protected static final String DALVIK = "Dalvik";
   
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
