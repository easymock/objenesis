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
package org.objenesis.benchmark;

import java.util.concurrent.TimeUnit;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.instantiator.sun.SunReflectionFactoryInstantiator;
import org.objenesis.strategy.InstantiatorStrategy;
import org.objenesis.strategy.SingleInstantiatorStrategy;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.logic.BlackHole;

/**
 * Benchmark comparing different instantiator strategies
 *
 * @author Henri Tremblay
 */
@BenchmarkMode(Mode.AverageTime)
@Threads(32)
@Warmup(iterations = 2, time = 5000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 2, time = 10000, timeUnit = TimeUnit.MILLISECONDS)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class ConcurrentGetInstantiator {

   private static final int COUNT = 1000;
   
   InstantiatorStrategy std = new StdInstantiatorStrategy();
   InstantiatorStrategy single = new SingleInstantiatorStrategy(SunReflectionFactoryInstantiator.class);
   
   Objenesis cachedStd = new ObjenesisStd();
   Objenesis uncachedStd = new ObjenesisStd(false);
   
   Class<?>[] toInstantiate = new Class[COUNT];

   @State(Scope.Thread)
   public static class ThreadState {
      int index = 0;
   }

   @Setup
   public void setUp() {
      for(int i = 0; i < COUNT; i++) {
         Enhancer enhancer = new Enhancer();
         enhancer.setUseCache(false); // deactivate the cache to get a new instance each time
         enhancer.setCallbackType(NoOp.class);
         Class<?> c = enhancer.createClass();
         toInstantiate[i] = c;
      }
   }

   @GenerateMicroBenchmark
   public void std(ThreadState state, BlackHole bh) {
     ObjectInstantiator inst = std.newInstantiatorOf(toInstantiate[state.index++ % COUNT]);
     bh.consume(inst);
   }

   @GenerateMicroBenchmark
   public void single(ThreadState state, BlackHole bh) {
      ObjectInstantiator inst = single.newInstantiatorOf(toInstantiate[state.index++ % COUNT]);
      bh.consume(inst);
   }
   
   @GenerateMicroBenchmark
   public void cachedStd(ThreadState state, BlackHole bh) {
      System.out.println(Thread.currentThread() + ": " + cachedStd.hashCode()); 
      ObjectInstantiator inst = cachedStd.getInstantiatorOf(toInstantiate[state.index++ % COUNT]);
      bh.consume(inst);
   }
   
   @GenerateMicroBenchmark
   public void uncachedStd(ThreadState state, BlackHole bh) {
      ObjectInstantiator inst = uncachedStd.getInstantiatorOf(toInstantiate[state.index++ % COUNT]);
      bh.consume(inst);
   }
}
