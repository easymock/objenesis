/*
 * Copyright 2006-2022 the original author or authors.
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

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.instantiator.sun.SunReflectionFactoryInstantiator;
import org.objenesis.strategy.BaseInstantiatorStrategy;
import org.objenesis.strategy.InstantiatorStrategy;
import org.objenesis.strategy.SingleInstantiatorStrategy;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Benchmark comparing different instantiator strategies
 *
 * @author Henri Tremblay
 */
@BenchmarkMode(Mode.AverageTime)
@Threads(32)
@Fork(2)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class ConcurrentGetInstantiator {

   private static final int COUNT = 1000;
   
   public static class SunInstantiatorStrategy extends BaseInstantiatorStrategy {
      @Override
      public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {
         return new SunReflectionFactoryInstantiator<>(type);
      }
   }

   InstantiatorStrategy std = new StdInstantiatorStrategy();
   InstantiatorStrategy single = new SingleInstantiatorStrategy(SunReflectionFactoryInstantiator.class);
   InstantiatorStrategy custom = new SunInstantiatorStrategy();
   
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

   @Benchmark
   public ObjectInstantiator<?> std(ThreadState state) {
      return std.newInstantiatorOf(toInstantiate[state.index++ % COUNT]);
   }

   @Benchmark
   public ObjectInstantiator<?> single(ThreadState state) {
      return single.newInstantiatorOf(toInstantiate[state.index++ % COUNT]);
   }

   @Benchmark
   public ObjectInstantiator<?> custom(ThreadState state) {
      return custom.newInstantiatorOf(toInstantiate[state.index++ % COUNT]);
   }
   
   @Benchmark
   public ObjectInstantiator<?> cachedStd(ThreadState state) {
      return cachedStd.getInstantiatorOf(toInstantiate[state.index++ % COUNT]);
   }
   
   @Benchmark
   public ObjectInstantiator<?> uncachedStd(ThreadState state) {
      return uncachedStd.getInstantiatorOf(toInstantiate[state.index++ % COUNT]);
   }
}
