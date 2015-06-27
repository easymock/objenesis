#!/usr/bin/env bash

mvn clean package

echo "Possible filters:"
echo "  org.objenesis.benchmark.CreateObject.*"
echo "  org.objenesis.benchmark.ConcurrentGetInstantiator.*"

java -jar target/benchmarks.jar $*
