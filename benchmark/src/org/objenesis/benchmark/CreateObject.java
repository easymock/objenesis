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

import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.instantiator.sun.SunReflectionFactoryInstantiator;
import org.objenesis.instantiator.sun.UnsafeFactoryInstantiator;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.logic.BlackHole;

/**
 * Benchmark comparing different instantiators
 *
 * @author Henri Tremblay
 */
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class CreateObject {

   ObjectInstantiator<Object> sun = new SunReflectionFactoryInstantiator<>(Object.class);

   ObjectInstantiator<Object> unsafe = new UnsafeFactoryInstantiator<>(Object.class);

   @GenerateMicroBenchmark
   public void createObjectWithConstructor(BlackHole bh) {
      bh.consume(new Object());
   }

    @GenerateMicroBenchmark
    public void createObjectWithMungedConstructor(BlackHole bh) {
        bh.consume(sun.newInstance());
    }

    @GenerateMicroBenchmark
    public void createObjectWithUnsafe(BlackHole bh) {
        bh.consume(unsafe.newInstance());
    }
}
