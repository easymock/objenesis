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
package org.objenesis.tck;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisHelper;
import org.springframework.osgi.test.AbstractConfigurableBundleCreatorTests;
import org.w3c.dom.Document;

/**
 * @author Henri Tremblay
 */
public class OsgiTest extends AbstractConfigurableBundleCreatorTests implements Serializable {

   private static final long serialVersionUID = 1L;

   protected String[] getTestBundlesNames() {
      String version = getImplementationVersion(Objenesis.class);
      // Null means we are an IDE, not in Maven. So we have an IDE project dependency instead
      // of a Maven dependency with the jar. Which explains why the version is null (no Manifest
      // since there's no jar. In that case we get the version from the pom.xml and hope the Maven
      // jar is up-to-date in the local repository
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
