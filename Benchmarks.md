Here are multiple benchmarks that are meant to verify Objenesis performance.

The source code is available [here](https://github.com/easymock/objenesis/tree/master/benchmark) if you want to challenge it.

# Sun implementations (CreateObject)

This benchmark compares two implementations working on HotSpot. They are instantiating the class Object. We have also added an instantiation with the default constructor.

The munged constructor version is a special constructor HotSpot is using to perform serialization.

The unsafe version is using `Unsafe.allocateInstance`. A bit easier to implement in Objenesis but much slower than the munged version. So we kept the later one as the default for HotSpot.

Benchmark                  |Mean     |Mean error|Units
---------------------------|---------|----------|-------
createObjectWithConstructor|3.509.724|0.017     |nsec/op
createObjectWithSun        |6.995    |0.087     |nsec/op
createObjectWithUnsafe     |20.382   |0.379     |nsec/op

# Instantiator creation (ConcurrentGetInstantiator)

When the client code ask for the instantiator of a given class, Objenesis creates one and can cache it or not. If it is cached, the next time the instantiator is requested for the same class, the cached instantiator will be returned instead of a new one. Instantiators are thread-safe so there's no need to worry.

During migration to Java 5, the cache implementation using a synchronize was replaced by a concurrent collection. This benchmark shows the difference in performance between the two cache implementations and of Objenesis without cache.

Type             |Mean       |Units
-----------------|-----------|-------
Cached (Java 1.3)|2535.952   |nsec/op
Cached (Java 5)  |238.946    |nsec/op
No cache (Java 5)|5253937.944|nsec/op

Then, the standard strategy used by Objenesis deduces the best instantiator for your platform. Another possibility is to decide that you known the platform you are on and you prefer to use the single instantiator that will create always the same type of instantiator using reflection. Finally, if you want to avoid using reflection, you can use a custom strategy returning always one instantiator using "new".

As you will see, it doesn't really matter which one you use, the performance is similar.

Type    |Mean       |Units
--------|-----------|-------
Custom  |5270008.460|nsec/op
Single  |5205981.442|nsec/op
Standard|5269205.477|nsec/op

Both benchmarks are performed in a highly concurrent environment to reflect what is expected to be normal usage.

They were executed on Windows 8 using Java 1.7.0_25 64 bits version.