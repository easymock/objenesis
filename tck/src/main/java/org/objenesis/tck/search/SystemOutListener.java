/*
 * Copyright 2006-2022 the original author or authors.
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
package org.objenesis.tck.search;

import org.objenesis.instantiator.annotations.Instantiator;
import org.objenesis.instantiator.annotations.Typology;

/**
 * @author Henri Tremblay
 */
public class SystemOutListener implements SearchWorkingInstantiatorListener {

    private static final String PATTERN = "%-65s: %s%n";

    public void instantiatorSupported(Class<?> c) {
        System.out.printf(PATTERN, c.getSimpleName() + " (" + getTypology(c) + ")", "Working!");
    }

    public void instantiatorUnsupported(Class<?> c, Throwable t) {
        System.out.printf(PATTERN, c.getSimpleName() + "(" + getTypology(c) + ")", "KO - " + t);
    }

    @Override
   public void instantiatorNotFound(String className, Throwable t) {
       System.out.printf(PATTERN, className + " not found", "KO - " + t);
   }

   private Typology getTypology(Class<?> c) {
       Instantiator instantiatorAnn = c.getAnnotation(Instantiator.class);
       return instantiatorAnn == null ? Typology.UNKNOWN : instantiatorAnn.value();
    }
}
