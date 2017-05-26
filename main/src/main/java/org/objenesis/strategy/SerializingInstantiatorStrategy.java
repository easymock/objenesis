/**
 * Copyright 2006-2017 the original author or authors.
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

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.instantiator.android.AndroidSerializationInstantiator;
import org.objenesis.instantiator.basic.ObjectInputStreamInstantiator;
import org.objenesis.instantiator.basic.ObjectStreamClassInstantiator;
import org.objenesis.instantiator.gcj.GCJSerializationInstantiator;
import org.objenesis.instantiator.perc.PercSerializationInstantiator;
import org.objenesis.instantiator.sun.SunReflectionFactorySerializationInstantiator;

import java.io.NotSerializableException;
import java.io.Serializable;

import static org.objenesis.strategy.PlatformDescription.*;

/**
 * Guess the best serializing instantiator for a given class. The returned instantiator will
 * instantiate classes like the genuine java serialization framework (the constructor of the first
 * not serializable class will be called). Currently, the selection doesn't depend on the class. It
 * relies on the
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
public class SerializingInstantiatorStrategy extends BaseInstantiatorStrategy {

   /**
    * Return an {@link ObjectInstantiator} allowing to create instance following the java
    * serialization framework specifications.
    *
    * @param type Class to instantiate
    * @return The ObjectInstantiator for the class
    */
   public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {
      if(!Serializable.class.isAssignableFrom(type)) {
         throw new ObjenesisException(new NotSerializableException(type+" not serializable"));
      }
      if(JVM_NAME.startsWith(HOTSPOT) || PlatformDescription.isThisJVM(OPENJDK)) {
         // Java 7 GAE was under a security manager so we use a degraded system
         if(isGoogleAppEngine() && PlatformDescription.SPECIFICATION_VERSION.equals("1.7")) {
            return new ObjectInputStreamInstantiator<T>(type);
         }
         // ObjectStreamClassInstantiator uses setAccessible which isn't supported on JDK9
         if(PlatformDescription.SPECIFICATION_VERSION.equals("9")) {
            return new SunReflectionFactorySerializationInstantiator<T>(type);
         }
         return new ObjectStreamClassInstantiator<T>(type);
      }
      else if(JVM_NAME.startsWith(DALVIK)) {
         if(PlatformDescription.isAndroidOpenJDK()) {
            return new ObjectStreamClassInstantiator<T>(type);
         }
         return new AndroidSerializationInstantiator<T>(type);
      }
      else if(JVM_NAME.startsWith(GNU)) {
         return new GCJSerializationInstantiator<T>(type);
      }
      else if(JVM_NAME.startsWith(PERC)) {
         return new PercSerializationInstantiator<T>(type);
      }

      return new ObjectStreamClassInstantiator<T>(type);
   }

}
