/*
 * Copyright 2006-2023 the original author or authors.
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
import org.junit.jupiter.api.Assertions;
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
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import static org.junit.jupiter.api.Assertions.assertSame;

public class OsgiTest {

   private static final List<String> EXTRA_SYSTEMPACKAGES = Arrays.asList("sun.misc", "sun.reflect");
   private static final FrameworkFactory frameworkFactory = ServiceLoader.load(FrameworkFactory.class).iterator().next();

   private static Framework framework;
   private static Bundle objenesisBundle;
   private static Bundle testBundle;

   private static String getImplementationVersion(final Class<?> c) {
      String version = c.getPackage().getImplementationVersion();
      // Null means we are an IDE, not in Maven. So we have an IDE project dependency instead
      // of a Maven dependency with the jar. Which explains why the version is null (no Manifest
      // since there's no jar). In that case we get the version from the pom.xml.
      if(version == null) {
         try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            final XPath xPath = xPathFactory.newXPath();
            XPathExpression xPathExpression;
            try {
               xPathExpression = xPath.compile("/project/parent/version");
            }
            catch(final XPathExpressionException e) {
               throw new RuntimeException(e);
            }

            final DocumentBuilderFactory xmlFact = DocumentBuilderFactory.newInstance();
            xmlFact.setNamespaceAware(false);
            final DocumentBuilder builder = xmlFact.newDocumentBuilder();
            final Document doc = builder.parse(new File("pom.xml"));
            version = xPathExpression.evaluate(doc);
         }
         catch(final Exception e) {
            throw new RuntimeException(e);
         }
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
      objenesisBundle = installBundle(bundleContext, objenesisPath);

      Path testPath = Paths.get("../test/target/objenesis-test-" + version + ".jar");
      testBundle = installBundle(bundleContext, testPath);

      framework.start();
      // Start the bundle to see right away if something goes wrong
      objenesisBundle.start();
      testBundle.start();
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
   void testCanInstantiate() throws Exception {
      instantiate("newInstance");
   }

   @Test
   void testCanInstantiateSerialize() throws Exception {
      instantiate("newSerializableInstance");
   }

   private void instantiate(String instantiationMethod) throws Exception {
      // We load a class from an OSGi bundle. We will then instantiate it using Objenesis which is in another bundle
      Class<?> clazz = loadOsgiClass(testBundle, EmptyClass.class.getName());
      Class<?> objenesisHelper = loadOsgiClass(objenesisBundle, ObjenesisHelper.class.getName());
      Method method = objenesisHelper.getMethod(instantiationMethod, Class.class);
      Object result = method.invoke(null, clazz);
      assertSame(clazz, result.getClass());
   }

   private static Class<?> loadOsgiClass(Bundle bundle, String className) throws Exception {
      return bundle.loadClass(className);
   }

}

