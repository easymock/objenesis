/**
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

/**
 * Loads a set of candidate classes from a properties file into the TCK.
 * <p>
 * The properties file takes the form of candidateClassName=shortDescription.
 *
 * @author Joe Walnes
 * @see TCK
 */
class CandidateLoader extends AbstractLoader {

   private final TCK tck;

   /**
    * @param tck TCK that will use the candidates
    * @param errorHandler Handler called in case of error
    */
   public CandidateLoader(TCK tck, AbstractLoader.ErrorHandler errorHandler) {
      super(tck.getClass().getClassLoader(), errorHandler);
      this.tck = tck;
   }

   protected void handlePropertyEntry(Class<?> clazz, String description, Candidate.CandidateType type) {
      tck.registerCandidate(clazz, description, type);
   }

}
