/**
 * Copyright 2006-2012 the original author or authors.
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
package org.objenesis.tck;

import java.io.IOException;
import java.io.Serializable;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisHelper;
import org.springframework.osgi.test.AbstractConfigurableBundleCreatorTests;

/**
 * @author Henri Tremblay
 */
public class OsgiTest extends AbstractConfigurableBundleCreatorTests implements Serializable {

   private static final long serialVersionUID = 1L;

   protected String[] getTestBundlesNames() {
      final String version = getImplementationVersion(Objenesis.class);
      return new String[] {"org.objenesis, objenesis, " + version};
   }

   public void testCanInstantiate() throws IOException {
      assertSame(OsgiTest.class, ObjenesisHelper.newInstance(getClass()).getClass());
   }

   public void testCanInstantiateSerialize() throws IOException {
      assertSame(OsgiTest.class, ObjenesisHelper.newSerializableInstance(getClass()).getClass());
   }

   protected String getImplementationVersion(final Class c) {
      return c.getPackage().getImplementationVersion();
   }
}
