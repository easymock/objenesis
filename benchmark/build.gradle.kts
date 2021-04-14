/*
 * Copyright 2006-2020 the original author or authors.
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

plugins {
    id("org.objenesis.java-conventions")
}

dependencies {
    implementation(project(":objenesis"))
    implementation("org.openjdk.jmh:jmh-core:1.25.2")
    implementation("cglib:cglib-nodep:3.3.0")
    compileOnly("org.openjdk.jmh:jmh-generator-annprocess:1.25.2")
}

description = "Objenesis Benchmark"
