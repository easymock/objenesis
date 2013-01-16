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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

/**
 * Loads a set of candidate classes from a properties file into the TCK. <p/> The properties file
 * takes the form of candidateClassName=shortDescription.
 * 
 * @author Joe Walnes
 * @see TCK
 */
public class CandidateLoader {

   private final TCK tck;
   private final ClassLoader classloader;
   private final ErrorHandler errorHandler;

   /**
    * Handler for reporting errors from the CandidateLoader.
    */
   public static interface ErrorHandler {
      /**
       * Called whenever, trying to retrieve a candidate class from its name, a
       * ClassNotFoundException is thrown
       * 
       * @param name Candidate class name
       */
      void classNotFound(String name);
   }

   /**
    * Error handler that logs errors to a text stream.
    */
   public static class LoggingErrorHandler implements CandidateLoader.ErrorHandler {

      private final PrintStream out;

      /**
       * @param out Stream in which to log
       */
      public LoggingErrorHandler(PrintStream out) {
         this.out = out;
      }

      public void classNotFound(String name) {
         out.println("Class not found : " + name);
      }

   }

   /**
    * @param tck TCK that will use the candidates
    * @param classloader ClassLoader from which candidates classes are loaded
    * @param errorHandler Handler called in case of error
    */
   public CandidateLoader(TCK tck, ClassLoader classloader, ErrorHandler errorHandler) {
      this.tck = tck;
      this.classloader = classloader;
      this.errorHandler = errorHandler;
   }

   /**
    * @param inputStream Stream containing the properties
    * @throws IOException If something goes wrong while reading the stream
    */
   public void loadFrom(InputStream inputStream) throws IOException {
      // Properties contains a convenient key=value parser, however it stores
      // the entries in a Hashtable which loses the original order.
      // So, we create a special Properties instance that writes its
      // entries directly to the TCK (which retains order).
      Properties properties = new Properties() {
         private static final long serialVersionUID = 1L;
         public Object put(Object key, Object value) {
            handlePropertyEntry((String) key, (String) value);
            return null;
         }
      };
      properties.load(inputStream);
   }

   /**
    * Load a candidate property file
    * 
    * @param cls Class on which <code>getResourceAsStream</code> is called
    * @param resource File name
    * @throws IOException If there's problem reading the file
    */
   public void loadFromResource(Class cls, String resource) throws IOException {
      InputStream candidatesConfig = cls.getResourceAsStream(resource);
      if(candidatesConfig == null) {
         throw new IOException("Resource '" + resource + "' not found relative to " + cls.getName());
      }
      try {
         loadFrom(candidatesConfig);
      }
      finally {
         candidatesConfig.close();
      }
   }

   private void handlePropertyEntry(String key, String value) {
      try {
         Class candidate = Class.forName(key, true, classloader);
         tck.registerCandidate(candidate, value);
      }
      catch(ClassNotFoundException e) {
         errorHandler.classNotFound(key);
      }
   }

}
