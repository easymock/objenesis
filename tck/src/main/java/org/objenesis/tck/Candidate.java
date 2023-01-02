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

import java.util.Arrays;
import java.util.EnumSet;

/**
 * Represents a candidate for instantiation
 *
 * @author Henri Tremblay
 */
public class Candidate implements Comparable<Candidate> {

   public enum CandidateType {
      STANDARD,
      SERIALIZATION,
   }

   private final Class<?> clazz;
   private final String description;
   private final EnumSet<CandidateType> types;

   public Candidate(Class<?> clazz, String description, CandidateType... types) {
      this.clazz = clazz;
      this.description = description;
      this.types = EnumSet.copyOf(Arrays.asList(types));
   }

   public Class<?> getClazz() {
      return clazz;
   }

   public String getDescription() {
      return description;
   }

   public EnumSet<CandidateType> getTypes() {
      return types;
   }

   @Override
   public boolean equals(Object o) {
      if(this == o) {
         return true;
      }
      if(o == null || getClass() != o.getClass()) {
         return false;
      }

      Candidate candidate = (Candidate) o;

      return clazz.equals(candidate.clazz);
   }

   @Override
   public int hashCode() {
      return clazz.hashCode();
   }

   @Override
   public int compareTo(Candidate o) {
      return description.compareTo(o.description);
   }

   @Override
   public String toString() {
      return clazz.getName() + "[" + description + "]";
   }
}
