/*
 * Copyright 2006-2024 the original author or authors.
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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisHelper;
import org.objenesis.test.EmptyClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

public class OsgiTest {

   private static final List<String> EXTRA_SYSTEMPACKAGES = Arrays.asList("sun.misc", "sun.reflect");
   private static final FrameworkFactory frameworkFactory = ServiceLoader.load(FrameworkFactory.class).iterator().next();

   private static MethodHandle newInstance;
   private static MethodHandle newSerializableInstance;

   private static Framework framework;
   private static Bundle testBundle;

   private static MethodHandle methodHandle(Class<?> objenesisHelper, String name, Class<?> returnType) {
      MethodHandles.Lookup lookUp  = MethodHandles.publicLookup();
      try {
         return lookUp.findStatic(objenesisHelper, name, MethodType.methodType(returnType, Class.class));
      } catch (NoSuchMethodException | IllegalAccessException e) {
         throw new RuntimeException(e);
      }
   }

   private static String getImplementationVersion(final Class<?> c) throws Exception {
      String version = c.getPackage().getImplementationVersion();
      // Null means we are an IDE, not in Maven. So we have an IDE project dependency instead
      // of a Maven dependency with the jar. Which explains why the version is null (no Manifest
      // since there's no jar). In that case we get the version from the pom.xml.
      if(version == null) {
         XPathFactory xPathFactory = XPathFactory.newInstance();
         XPath xPath = xPathFactory.newXPath();
         XPathExpression xPathExpression = xPath.compile("/project/parent/version");

         DocumentBuilderFactory xmlFact = DocumentBuilderFactory.newInstance();
         xmlFact.setNamespaceAware(false);
         DocumentBuilder builder = xmlFact.newDocumentBuilder();
         Document doc = builder.parse(Paths.get("pom.xml").toFile());
         version = xPathExpression.evaluate(doc);
      }
      return version;
   }

   @BeforeAll
   static void before(@TempDir Path frameworkStorage) throws Exception {
      Map<String, String> configuration = new HashMap<>();
      configuration.put(Constants.FRAMEWORK_STORAGE, frameworkStorage.toString());
      configuration.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, String.join(",", EXTRA_SYSTEMPACKAGES));
      framework = frameworkFactory.newFramework(configuration);
      framework.init();

      BundleContext bundleContext = framework.getBundleContext();
      String version = getImplementationVersion(Objenesis.class);

      Path objenesisPath = Paths.get("../main/target/objenesis-" + version + ".jar");
      Bundle objenesisBundle = installBundle(bundleContext, objenesisPath);

      Path testPath = Paths.get("../test/target/objenesis-test-" + version + ".jar");
      testBundle = installBundle(bundleContext, testPath);

      framework.start();
      // Start the bundle to see right away if something goes wrong
      objenesisBundle.start();
      testBundle.start();

      Class<?> objenesisHelper = loadOsgiClass(objenesisBundle, ObjenesisHelper.class.getName());
      newInstance = methodHandle(objenesisHelper, "newInstance", Object.class);
      newSerializableInstance = methodHandle(objenesisHelper, "newSerializableInstance", Serializable.class);
   }

   @AfterAll
   static void tearDown() throws Exception {
      if (framework != null) {
         framework.stop();
         framework.waitForStop(10_000);
      }
   }

   private static Bundle installBundle(BundleContext bundleContext, Path bundlePath) {
      try {
         return bundleContext.installBundle(bundlePath.toUri().toString());
      } catch (BundleException e) {
         throw new IllegalStateException("Failed to install bundle: " + bundlePath.getFileName(), e);
      }
   }

   @Test
   void testCanInstantiate() throws Throwable {
      instantiate(newInstance);
   }

   @Test
   void testCanInstantiateSerialize() throws Throwable {
      instantiate(newSerializableInstance);
   }

   private void instantiate(MethodHandle instantiationMethod) throws Throwable {
      // We load a class from an OSGi bundle. We will then instantiate it using Objenesis which is in another bundle
      Class<?> clazz = loadOsgiClass(testBundle, EmptyClass.class.getName());
      // should be different since one is from OSGi and the other is from the classpath
      // This is to make sure our test is actually working as expected
      assertNotSame(clazz, EmptyClass.class);
      Object result = instantiationMethod.invoke(clazz);
      assertSame(clazz, result.getClass());
   }

   private static Class<?> loadOsgiClass(Bundle bundle, String className) throws Exception {
      return bundle.loadClass(className);
   }

}

