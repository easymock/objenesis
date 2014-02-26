/**
 * Copyright 2006-2014 the original author or authors.
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
package org.objenesis.benchmark;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.instantiator.sun.SunReflectionFactoryInstantiator;
import org.objenesis.instantiator.sun.UnsafeFactoryInstantiator;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.logic.BlackHole;
import sun.misc.Unsafe;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark comparing different instantiators
 *
 * @author Henri Tremblay
 */
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 1, time = 5000, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
@Measurement(iterations = 2, time = 5000, timeUnit = TimeUnit.MILLISECONDS)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class CreateObject {

   ObjectInstantiator<Object> sunInstantiator;

   ObjectInstantiator<Object> unsafeInstantiator;

   Unsafe unsafe;

   Constructor<Object> constructor;

   Class<Object> type = Object.class;

   private Constructor<Object> getConstructor() {
      try {
         ReflectionFactory factory = ReflectionFactory.getReflectionFactory();
         Constructor<Object> objectConstructor =type.getConstructor((Class[]) null);
         Constructor<Object> c = (Constructor<Object>) factory.newConstructorForSerialization(type, objectConstructor);
         return c;
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   private Unsafe getUnsafe() {
      Field f;
      try {
         f = Unsafe.class.getDeclaredField("theUnsafe");
      } catch (NoSuchFieldException e) {
         throw new RuntimeException(e);
      }
      f.setAccessible(true);
      try {
         return (Unsafe) f.get(null);
      } catch (IllegalAccessException e) {
         throw new RuntimeException(e);
      }
   }

   @Setup
   public void prepare() {
      sunInstantiator = new SunReflectionFactoryInstantiator<Object>(type);
      unsafeInstantiator = new UnsafeFactoryInstantiator<Object>(type);
      unsafe = getUnsafe();
      constructor = getConstructor();
   }

   @GenerateMicroBenchmark
   public void createObjectWithConstructor(BlackHole bh) {
      bh.consume(new Object());
   }

   @GenerateMicroBenchmark
   public void createObjectWithMungedConstructor(BlackHole bh) {
      bh.consume(sunInstantiator.newInstance());
   }
   
   @GenerateMicroBenchmark
   public void createObjectWithMungedConstructorRaw(BlackHole bh) throws Exception {
      bh.consume(constructor.newInstance());
   }

   @GenerateMicroBenchmark
   public void createObjectWithMungedConstructorRawAndCast(BlackHole bh) throws Exception {
      bh.consume(type.cast(constructor.newInstance()));
   }

   @GenerateMicroBenchmark
   public void createObjectWithUnsafe(BlackHole bh) {
      bh.consume(unsafeInstantiator.newInstance());
   }

   @GenerateMicroBenchmark
   public void createObjectWithUnsafeRaw(BlackHole bh) throws Exception {
      bh.consume(unsafe.allocateInstance(type));
   }
   
   @GenerateMicroBenchmark
   public void createObjectWithUnsafeRawInline(BlackHole bh) throws Exception {
      bh.consume(unsafe.allocateInstance(Object.class));
   }

   @GenerateMicroBenchmark
   public void createObjectWithUnsafeRawAndCast(BlackHole bh) throws Exception{
      bh.consume(type.cast(unsafe.allocateInstance(type)));
   }

   @GenerateMicroBenchmark
   public void createObjectWithUnsafeRawAndCastInline(BlackHole bh) throws Exception {
      bh.consume(type.cast(unsafe.allocateInstance(Object.class)));
   }
     
   @GenerateMicroBenchmark
   public void createObjectWithUnsafeRawException(BlackHole bh) {
      try {
         bh.consume(unsafe.allocateInstance(type));
      }
      catch(InstantiationException e) {
         throw new ObjenesisException(e);
      }
   }
   
   @GenerateMicroBenchmark
   public void createObjectWithUnsafeRawExceptionInline(BlackHole bh) {
      try {
         bh.consume(unsafe.allocateInstance(Object.class));
      }
      catch(InstantiationException e) {
         throw new ObjenesisException(e);
      }
   }   
}
