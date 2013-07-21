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

import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.instantiator.android.Android23Instantiator;
import org.objenesis.instantiator.android.Android30Instantiator;
import org.objenesis.instantiator.gcj.GCJInstantiator;
import org.objenesis.instantiator.jrockit.JRockit131Instantiator;
import org.objenesis.instantiator.jrockit.JRockitLegacyInstantiator;
import org.objenesis.instantiator.perc.PercInstantiator;
import org.objenesis.instantiator.sun.Sun13Instantiator;
import org.objenesis.instantiator.sun.SunReflectionFactoryInstantiator;
import org.objenesis.instantiator.sun.UnsafeFactoryInstantiator;

/**
 * Guess the best instantiator for a given class. The instantiator will instantiate the class
 * without calling any constructor. Currently, the selection doesn't depend on the class. It relies
 * on the
 * <ul>
 * <li>JVM version</li>
 * <li>JVM vendor</li>
 * <li>JVM vendor version</li>
 * </ul>
 * However, instantiators are stateful and so dedicated to their class.
 * 
 * @author Henri Tremblay
 * @see ObjectInstantiator
 */
public class StdInstantiatorStrategy extends BaseInstantiatorStrategy {

   /**
    * Return an {@link ObjectInstantiator} allowing to create instance without any constructor being
    * called.
    * 
    * @param type Class to instantiate
    * @return The ObjectInstantiator for the class
    */
   public ObjectInstantiator newInstantiatorOf(Class type) {

      if(JVM_NAME.startsWith(SUN)) {
         if(VM_VERSION.startsWith("1.3")) {
            return new Sun13Instantiator(type);
         }
         // The UnsafeFactoryInstantiator would also work. But according to benchmarks, it is 2.5 times slower. So
         // I prefer to use this one
         return new SunReflectionFactoryInstantiator(type);
      }
      else if(JVM_NAME.startsWith(JROCKIT)) {
         if(VM_VERSION.startsWith("1.3")) {
            return new JRockit131Instantiator(type);
         }
         else if(VM_VERSION.startsWith("1.4")) {
            // JRockit vendor version will be RXX where XX is the version
            // Versions prior to 26 need special handling
            // From R26 on, java.vm.version starts with R
            if(!VENDOR_VERSION.startsWith("R")) {
               // On R25.1 and R25.2, ReflectionFactory should work. Otherwise, we must use the
               // Legacy instantiator.
               if(VM_INFO == null || !VM_INFO.startsWith("R25.1") || !VM_INFO.startsWith("R25.2")) {
                  return new JRockitLegacyInstantiator(type);
               }
            }
         }
      }
      else if(JVM_NAME.startsWith(DALVIK)) {
        // System property "java.vm.version" seems to be 1.4.0 for Android 2.3 and 1.5.0 for Android 3.0
        // so we use it here to choose the relevant implementation.
        if(VENDOR_VERSION.compareTo("1.5.0") < 0) {
          // Android 2.3 Gingerbread and lower
          return new Android23Instantiator(type);
        } else {
          // Android 3.0 Honeycomb and higher
          return new Android30Instantiator(type);
        }
      }
      else if(JVM_NAME.startsWith(GNU)) {
         return new GCJInstantiator(type);
      }
      else if(JVM_NAME.startsWith(PERC)) {
    	  return new PercInstantiator(type);
      }

      // Fallback instantiator, should work with most modern JVM
      return new UnsafeFactoryInstantiator(type);
   }
}
