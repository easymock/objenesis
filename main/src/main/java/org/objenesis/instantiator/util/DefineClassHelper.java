/*
 * Copyright 2006-2021 the original author or authors.
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
package org.objenesis.instantiator.util;

import sun.misc.Unsafe;
import org.objenesis.ObjenesisException;
import org.objenesis.strategy.PlatformDescription;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.security.ProtectionDomain;

/**
 * Java 11+ removed sun.misc.Unsafe.defineClass. This class bridges the gap to work from Java 1.8 up to 11.
 * <p>
 * It was inspired from <a href="https://github.com/jboss-javassist/javassist/blob/master/src/main/javassist/util/proxy/DefineClassHelper.java">javassist</a>.
 *
 * @author Henri Tremblay
 */
public final class DefineClassHelper {

   private static abstract class Helper {
      abstract Class<?> defineClass(String name, byte[] b, int off, int len, Class<?> neighbor,
                                    ClassLoader loader, ProtectionDomain protectionDomain);
   }

   private static class Java8 extends Helper {

      private final MethodHandle defineClass = defineClass();

      private MethodHandle defineClass() {
         MethodType mt = MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class);
         MethodHandle m;
         try {
            m = MethodHandles.publicLookup().findVirtual(Unsafe.class, "defineClass", mt);
         } catch(NoSuchMethodException | IllegalAccessException e) {
            throw new ObjenesisException(e);
         }
         Unsafe unsafe = UnsafeUtils.getUnsafe();
         return m.bindTo(unsafe);
      }

      @Override
      Class<?> defineClass(String className, byte[] b, int off, int len, Class<?> neighbor, ClassLoader loader, ProtectionDomain protectionDomain) {
         try {
            return (Class<?>) defineClass.invokeExact(className, b, off, len, loader, protectionDomain);
         } catch (Throwable e) {
            if(e instanceof Error) {
               throw (Error) e;
            }
            if(e instanceof RuntimeException) {
               throw (RuntimeException) e;
            }
            throw new ObjenesisException(e);
         }
      }
   }

   private static class Java11 extends Helper {

      private final Class<?> module = module();
      private final MethodHandles.Lookup lookup = MethodHandles.lookup();
      private final MethodHandle getModule = getModule();
      private final MethodHandle addReads = addReads();
      private final MethodHandle privateLookupIn = privateLookupIn();
      private final MethodHandle defineClass = defineClass();

      private Class<?> module() {
         try {
            return Class.forName("java.lang.Module");
         } catch (ClassNotFoundException e) {
            throw new ObjenesisException(e);
         }
      }

      private MethodHandle getModule() {
         try {
            return lookup.findVirtual(Class.class, "getModule", MethodType.methodType(module));
         } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new ObjenesisException(e);
         }
      }

      private MethodHandle addReads() {
         try {
            return lookup.findVirtual(module, "addReads", MethodType.methodType(module, module));
         } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new ObjenesisException(e);
         }
      }

      private MethodHandle privateLookupIn() {
         try {
            return lookup.findStatic(MethodHandles.class, "privateLookupIn", MethodType.methodType(MethodHandles.Lookup.class, Class.class, MethodHandles.Lookup.class));
         } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new ObjenesisException(e);
         }
      }

      private MethodHandle defineClass() {
         try {
            return lookup.findVirtual(MethodHandles.Lookup.class, "defineClass", MethodType.methodType(Class.class, byte[].class));
         } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new ObjenesisException(e);
         }
      }

      @Override
      Class<?> defineClass(String className, byte[] b, int off, int len, Class<?> neighbor, ClassLoader loader, ProtectionDomain protectionDomain) {
         try {
            Object module = getModule.invokeWithArguments(DefineClassHelper.class);
            Object neighborModule = getModule.invokeWithArguments(neighbor);
            addReads.invokeWithArguments(module, neighborModule);
            MethodHandles.Lookup prvlookup = (MethodHandles.Lookup) privateLookupIn.invokeExact(neighbor, lookup);
            return (Class<?>) defineClass.invokeExact(prvlookup, b);
         } catch (Throwable e) {
            throw new ObjenesisException(neighbor.getName() + " has no permission to define the class", e);
         }
      }
   }

   // Java 11+ removed sun.misc.Unsafe.defineClass, so we fallback to invoking defineClass on
   // ClassLoader until we have an implementation that uses MethodHandles.Lookup.defineClass
   private static final Helper privileged = PlatformDescription.isAfterJava11() ?
      new Java11() : new Java8();

   public static Class<?> defineClass(String name, byte[] b, int off, int len, Class<?> neighbor,
                                      ClassLoader loader, ProtectionDomain protectionDomain) {
      return privileged.defineClass(name, b, off, len, neighbor, loader, protectionDomain);
   }

   private DefineClassHelper() {}
}
