/*
 * Copyright 2006-2025 the original author or authors.
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
 * Class loading a property file and delegating the treatment of each line to a concrete implementations.
 *
 * @author Henri Tremblay
 */
public abstract class AbstractLoader {

   private final ClassLoader classloader;
   private final ErrorHandler errorHandler;

   /**
    * Handler for reporting errors from the AbstractLoader.
    */
   public interface ErrorHandler {
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
    * @param classloader ClassLoader from which candidates classes are loaded
    * @param errorHandler Handler called in case of error
    */
   public AbstractLoader(ClassLoader classloader, ErrorHandler errorHandler) {
      this.classloader = classloader;
      this.errorHandler = errorHandler;
   }

   /**
    * @param inputStream Stream containing the properties
    * @param type Type of the candidate loaded from the stream
    * @throws IOException If something goes wrong while reading the stream
    */
   public void loadFrom(InputStream inputStream, final Candidate.CandidateType type) throws IOException {
      // Properties contains a convenient key=value parser, however it stores
      // the entries in a Hashtable which loses the original order.
      // So, we create a special Properties instance that writes its
      // entries directly to the concrete implementations.
      Properties properties = new Properties() {
         private static final long serialVersionUID = 1L;
         @Override
         public Object put(Object key, Object value) {
            handlePropertyEntry((String) key, (String) value, type);
            return null;
         }
      };
      properties.load(inputStream);
   }

   /**
    * Load a candidate property file
    *
    * @param resource File name
    * @param type Type of the candidate loaded from the stream
    * @throws IOException If there's problem reading the file
    */
   public void loadFromResource(String resource, Candidate.CandidateType type) throws IOException {
      InputStream candidatesConfig = classloader.getResourceAsStream(resource);
      if(candidatesConfig == null) {
         throw new IOException("Resource '" + resource + "' not found");
      }
      try {
         loadFrom(candidatesConfig, type);
      }
      finally {
         candidatesConfig.close();
      }
   }

   private void handlePropertyEntry(String key, String value, Candidate.CandidateType type) {
      try {
         Class<?> candidate = Class.forName(key, true, classloader);
         handlePropertyEntry(candidate, value, type);
      }
      catch(ClassNotFoundException e) {
         errorHandler.classNotFound(key);
      }
   }

   /**
    * Will receive one class and its description pairs from the file
    *
    * @param clazz class on the line
    * @param description description of the class
    * @param type type of the candidate
    */
   protected abstract void handlePropertyEntry(Class<?> clazz, String description, Candidate.CandidateType type);

}
