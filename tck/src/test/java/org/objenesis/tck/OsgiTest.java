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
package org.objenesis.tck;

import java.io.File;
import java.io.Serializable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisHelper;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;

/**
 * @author Henri Tremblay
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class OsgiTest implements Serializable{

   private static final long serialVersionUID = 1L;

   @Configuration
   public Option[] config() {
      String version = getImplementationVersion(Objenesis.class);
      return options(
         bundle("file:../main/target/objenesis-" + version + ".jar"),
         junitBundles()
      );
   }

   @Test
   public void testCanInstantiate() {
      assertSame(OsgiTest.class, ObjenesisHelper.newInstance(getClass()).getClass());
   }

   @Test
   public void testCanInstantiateSerialize() {
      assertSame(OsgiTest.class, ObjenesisHelper.newSerializableInstance(getClass()).getClass());
   }

   private String getImplementationVersion(final Class<?> c) {
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
}
