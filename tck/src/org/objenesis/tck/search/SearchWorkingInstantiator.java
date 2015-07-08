/**
 * Copyright 2006-2015 the original author or authors.
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

import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.strategy.PlatformDescription;
import org.objenesis.tck.candidates.SerializableNoConstructor;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class will try every available instantiator on the platform to see which works.
 *
 * @author Henri Tremblay
 */
public class SearchWorkingInstantiator implements Serializable { // implements Serializable just for the test

    private SearchWorkingInstantiatorListener listener;

    public static void main(String[] args) throws Exception {
        System.out.println();
        System.out.println(PlatformDescription.describePlatform());
        System.out.println();

        SearchWorkingInstantiator searchWorkingInstantiator = new SearchWorkingInstantiator(new SystemOutListener());
        searchWorkingInstantiator.searchForInstantiator(SerializableNoConstructor.class);
    }

    public SearchWorkingInstantiator(SearchWorkingInstantiatorListener listener) {
        this.listener = listener;
    }

    public void searchForInstantiator(Class<?> toInstantiate) {
        List<Class<?>> classes = ClassEnumerator.getClassesForPackage(ObjectInstantiator.class.getPackage());
        Collections.sort(classes, new Comparator<Class<?>>() {
            public int compare(Class<?> o1, Class<?> o2) {
                return o1.getSimpleName().compareTo(o2.getSimpleName());
            }
        });

        for (int i = 0; i < classes.size(); i++) {
            Class<?> c = classes.get(i);
            if(c.isInterface() || !ObjectInstantiator.class.isAssignableFrom(c)) {
                continue;
            }

            Constructor<?> constructor;
            try {
                constructor = c.getConstructor(Class.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            try {
                ObjectInstantiator<?> instantiator =
                        (ObjectInstantiator<?>) constructor.newInstance(toInstantiate);
                instantiator.newInstance();
                listener.instantiatorSupported(c);
            }
            catch(Exception e) {
                Throwable t = (e instanceof InvocationTargetException) ? e.getCause() : e;
                listener.instantiatorUnsupported(c, t);
            }
        }
    }
}
