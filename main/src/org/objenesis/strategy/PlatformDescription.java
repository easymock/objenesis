/*
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

import java.lang.reflect.Field;

import org.objenesis.ObjenesisException;

/**
 * List of constants describing the currently used platform.
 *
 * @author Henri Tremblay
 */
public final class PlatformDescription {

    /** JVM_NAME prefix for JRockit */
    public static final String JROCKIT = "BEA";

    /** JVM_NAME prefix for GCJ */
    public static final String GNU = "GNU libgcj";

    /** JVM_NAME prefix for Sun Java HotSpot */
    public static final String SUN = "Java HotSpot";

    /** JVM_NAME prefix for Aonix PERC */
    public static final String PERC = "PERC";

    /** JVM_NAME prefix for Dalvik/Android */
    public static final String DALVIK = "Dalvik";

    /** Java specification version */
    public static final String SPECIFICATION_VERSION = System.getProperty("java.specification.version");

    /** JVM version */
    public static final String VM_VERSION = System.getProperty("java.runtime.version");

    /** JVM version */
    public static final String VM_INFO = System.getProperty("java.vm.info");

    /** Vendor version */
    public static final String VENDOR_VERSION = System.getProperty("java.vm.version");

    /** Vendor name */
    public static final String VENDOR = System.getProperty("java.vm.vendor");

    /** JVM name */
    public static final String JVM_NAME = System.getProperty("java.vm.name");

    /** Version Specific to Android */
    public static final String ANDROID_API_LEVEL = androidApiLevel();

    /**
     * Check if the current JVM is of the type passed in parameter. Normally, this will be a constant from this
     * class. We basically do <code>System.getProperty("java.vm.name").startWith(name)</code>.
     *
     * @param name jvm name we are looking for
     * @return if it's the requested JVM
     */
    public static boolean isThisJVM(String name) {
        return JVM_NAME.startsWith(name);
    }

    private static String androidApiLevel() {
        if(!isThisJVM(DALVIK)) {
            return null;
        }
        Class version;
        try {
            version = Class.forName("android.os.Build$VERSION");
        } catch (ClassNotFoundException e) {
            throw new ObjenesisException(e);
        }
        Field f;
        try {
            f = version.getField("SDK");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        try {
            return (String) f.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private PlatformDescription() {}
}
