/*
 * Copyright 2006-2018 the original author or authors.
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
package org.objenesis.instantiator.basic;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.instantiator.annotations.Instantiator;
import org.objenesis.instantiator.annotations.Typology;

/**
 * The instantiator that always throws an exception. Mainly used for tests
 *
 * @author Henri Tremblay
 */
@Instantiator(Typology.NOT_COMPLIANT)
public class FailingInstantiator<T> implements ObjectInstantiator<T> {

   public FailingInstantiator(Class<T> type) {
   }

   /**
    * @return Always throwing an exception
    */
   public T newInstance() {
      throw new ObjenesisException("Always failing");
   }
}
