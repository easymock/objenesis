/*
 * Copyright 2006-2023 the original author or authors.
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

   /** JVM_NAME prefix for GCJ */
   public static final String GNU = "GNU libgcj";

   /** JVM_NAME prefix for Java HotSpot */
   public static final String HOTSPOT = "Java HotSpot";

   /**
    * JVM_NAME prefix for Java HotSpot
    *
    * @deprecated Use {@link #HOTSPOT} instead
    */
   @Deprecated
   public static final String SUN = HOTSPOT;

   /** JVM_NAME prefix for the OpenJDK */
   public static final String OPENJDK = "OpenJDK";

   /** JVM_NAME prefix for Aonix PERC */
   public static final String PERC = "PERC";

   /** JVM_NAME prefix for Dalvik/Android */
   public static final String DALVIK = "Dalvik";

   /** Java specification version */
   public static final String SPECIFICATION_VERSION = System
      .getProperty("java.specification.version");

   /** JVM version */
   public static final String VM_VERSION = System.getProperty("java.runtime.version");

   /** JVM version */
   public static final String VM_INFO = System.getProperty("java.vm.info");

   /** VM vendor version */
   public static final String VENDOR_VERSION = System.getProperty("java.vm.version");

   /** VM vendor name */
   public static final String VENDOR = System.getProperty("java.vm.vendor");

   /** JVM name */
   public static final String JVM_NAME = System.getProperty("java.vm.name");

   /** Android version. Will be 0 for none android platform */
   public static final int ANDROID_VERSION = getAndroidVersion();

   /** Flag telling if this version of Android is based on the OpenJDK */
   public static final boolean IS_ANDROID_OPENJDK = getIsAndroidOpenJDK();

   /** Google App Engine version or null is we are not on GAE
    *
    * @deprecated No need to differentiate GAE anymore since they have stopped using a security manager
    */
   @Deprecated
   public static final String GAE_VERSION = getGaeRuntimeVersion();

   /**
    * Describes the platform. Outputs Java version and vendor.
    *
    * @return Description of the current platform
    */
   public static String describePlatform() {
      String desc = "Java " + SPECIFICATION_VERSION + " ("
              + "VM vendor name=\"" + VENDOR + "\", "
              + "VM vendor version=" + VENDOR_VERSION + ", "
              + "JVM name=\"" + JVM_NAME + "\", "
              + "JVM version=" + VM_VERSION + ", "
              + "JVM info=" + VM_INFO;

      // Add the API level if it's an Android platform
      if(ANDROID_VERSION != 0) {
         desc += ", API level=" + ANDROID_VERSION;
      }
      desc += ")";

      return desc;
   }

   /**
    * Check if the current JVM is of the type passed in parameter. Normally, this will be a constant
    * from this class. We basically do
    * <code>System.getProperty("java.vm.name").startWith(name)</code>.
    *
    * @param name jvm name we are looking for
    * @return if it's the requested JVM
    */
   public static boolean isThisJVM(String name) {
      return JVM_NAME.startsWith(name);
   }

   /**
    * Check if this JVM is an Android JVM based on OpenJDK.
    *
    * @return if it's an Android version based on the OpenJDK. Will return false if this JVM isn't an Android JVM at all
     */
   public static boolean isAndroidOpenJDK() {
      return IS_ANDROID_OPENJDK;
   }

   private static boolean getIsAndroidOpenJDK() {
      if(getAndroidVersion() == 0) {
         return false; // Not android at all
      }
      // Sadly, Android N is still API 23. So we can't base ourselves on the API level to know if it is an OpenJDK
      // version or not
      String bootClasspath = System.getProperty("java.boot.class.path");
      return bootClasspath != null && bootClasspath.toLowerCase().contains("core-oj.jar");
   }

   /**
    * Tells if the current JVM is running Java 9 or above
    *
    * @return if the current JVM is Java 9 or above
    */
   public static boolean isAfterJigsaw() {
      String version = PlatformDescription.SPECIFICATION_VERSION;
      return version.indexOf('.') < 0; // No dot means the version is 9, 10, 11, ... not 1.6, 1.7, 1.8
   }

   /**
    * Tells if the current JVM is running Java 11 or above
    *
    * @return if the current JVM is Java 11 or above
    */
   public static boolean isAfterJava11() {
      if(!isAfterJigsaw()) {
         return false;
      }
      int version = Integer.parseInt(PlatformDescription.SPECIFICATION_VERSION);
      return version >= 11;
   }

   /**
    * Tells if the platform running the JVM is Google App Engine.
    *
    * @return are we running on Google App Engine
    * @deprecated No need to differentiate GAE anymore since they have stopped using a security manager
    */
   @Deprecated
   public static boolean isGoogleAppEngine() {
      return GAE_VERSION != null;
   }

   /**
    * Give the GAE Java version used. To know if it was before or after the restricting security manager.
    *
    * @return Java runtime version on GAE. Null if not on GEA
    * @deprecated No need to differentiate GAE anymore since they have stopped using a security manager
    */
   @Deprecated
   private static String getGaeRuntimeVersion() {
      return System.getProperty("com.google.appengine.runtime.version");
   }

   private static int getAndroidVersion() {
      if(!isThisJVM(DALVIK)) {
         return 0;
      }
      return getAndroidVersion0();
   }

   private static int getAndroidVersion0() {
      Class<?> clazz;
      try {
         clazz = Class.forName("android.os.Build$VERSION");
      }
      catch(ClassNotFoundException e) {
         throw new ObjenesisException(e);
      }
      Field field;
      try {
         field = clazz.getField("SDK_INT");
      }
      catch(NoSuchFieldException e) {
         // Might be a really old API (before 4), go for SDK
         return getOldAndroidVersion(clazz);
      }
      int version;
      try {
         version = (Integer) field.get(null);
      }
      catch(IllegalAccessException e) {
         throw new RuntimeException(e);
      }
      return version;
   }

   private static int getOldAndroidVersion(Class<?> versionClass) {
      Field field;
      try {
         field = versionClass.getField("SDK");
      }
      catch(NoSuchFieldException e) {
         throw new ObjenesisException(e);
      }
      String version;
      try {
         version = (String) field.get(null);
      }
      catch(IllegalAccessException e) {
         throw new RuntimeException(e);
      }
      return Integer.parseInt(version);
   }

   private PlatformDescription() {
   }
}
