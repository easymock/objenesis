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
package org.objenesis.instantiator.perc;

import java.io.ObjectInputStream;
import java.lang.reflect.Method;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 * Instantiates a class by making a call to internal Perc private methods. It is only supposed to
 * work on Perc JVMs. This instantiator will not call any constructors. The code was provided by
 * Aonix Perc support team.
 * 
 * @author Henri Tremblay
 * @see org.objenesis.instantiator.ObjectInstantiator
 */
public class PercInstantiator implements ObjectInstantiator {

	private final Method newInstanceMethod;

	private final Object[] typeArgs = new Object[] { null, Boolean.FALSE };

	public PercInstantiator(Class type) {

		typeArgs[0] = type;

		try {
			newInstanceMethod = ObjectInputStream.class.getDeclaredMethod("newInstance",
					new Class[] { Class.class, Boolean.TYPE });
			newInstanceMethod.setAccessible(true);
		}
      catch(RuntimeException e) {
			throw new ObjenesisException(e);
		}
      catch(NoSuchMethodException e) {
         throw new ObjenesisException(e);
      }
	}

	public Object newInstance() {
		try {
			return newInstanceMethod.invoke(null, typeArgs);
		} catch (Exception e) {
			throw new ObjenesisException(e);
		}
	}

}
