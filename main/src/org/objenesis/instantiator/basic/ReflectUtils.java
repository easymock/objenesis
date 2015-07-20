package org.objenesis.instantiator.basic;

/*
 * Copyright 2003,2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.objenesis.ObjenesisException;

import java.lang.reflect.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;

/**
 * @version $Id: ReflectUtils.java,v 1.30 2009/01/11 19:47:49 herbyderby Exp $
 */
public final class ReflectUtils {
   private ReflectUtils() { }

   private static Method DEFINE_CLASS;
   private static final ProtectionDomain PROTECTION_DOMAIN;

   static {
      PROTECTION_DOMAIN = (ProtectionDomain)AccessController.doPrivileged(new PrivilegedAction<ProtectionDomain>() {
         public ProtectionDomain run() {
            return ReflectUtils.class.getProtectionDomain();
         }
      });

      AccessController.doPrivileged(new PrivilegedAction<Object>() {
         public Object run() {
            try {
               Class<?> loader = Class.forName("java.lang.ClassLoader"); // JVM crash w/o this
               DEFINE_CLASS = loader.getDeclaredMethod("defineClass",
                  new Class[]{ String.class,
                     byte[].class,
                     Integer.TYPE,
                     Integer.TYPE,
                     ProtectionDomain.class });
               DEFINE_CLASS.setAccessible(true);
            } catch (ClassNotFoundException e) {
               throw new ObjenesisException(e);
            } catch (NoSuchMethodException e) {
               throw new ObjenesisException(e);
            }
            return null;
         }
      });
   }

   public static Class defineClass(String className, byte[] b, ClassLoader loader) throws Exception {
      Object[] args = new Object[]{className, b, new Integer(0), new Integer(b.length), PROTECTION_DOMAIN };
      Class c = (Class)DEFINE_CLASS.invoke(loader, args);
      // Force static initializers to run.
      Class.forName(className, true, loader);
      return c;
   }
}
