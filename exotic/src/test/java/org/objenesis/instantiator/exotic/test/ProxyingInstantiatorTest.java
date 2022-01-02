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
package org.objenesis.instantiator.exotic.test;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.objenesis.instantiator.basic.ProxyingInstantiator;
import org.objenesis.test.EmptyClass;
import org.objenesis.instantiator.ObjectInstantiator;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This test is using the original basic.ProxyInstantiator do make sure the bridging to the new class is working.
 *
 * @author Henri Tremblay
 */
@Disabled("Because it doesn't work without -Xverify:none")
public class ProxyingInstantiatorTest {

   @Test
   public void testNewInstance() {
      ObjectInstantiator<EmptyClass> inst = new ProxyingInstantiator<>(EmptyClass.class);
      EmptyClass c = inst.newInstance();
      assertEquals("org.objenesis.test.EmptyClass$$$Objenesis", c.getClass().getName());
   }

   @Test
   public void testJavaLangInstance() {
      ObjectInstantiator<Object> inst = new ProxyingInstantiator<>(Object.class);
      Object c = inst.newInstance();
      assertEquals("org.objenesis.subclasses.java.lang.Object$$$Objenesis", c.getClass().getName());
   }

}
