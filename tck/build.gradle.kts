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
    testImplementation("org.ops4j.pax.exam:pax-exam-junit4:4.13.2")
    testImplementation("org.ops4j.pax.exam:pax-exam-container-native:4.13.2")
    testImplementation("org.ops4j.pax.exam:pax-exam-link-mvn:4.13.2")
    testImplementation("org.apache.felix:org.apache.felix.framework:6.0.3")
    testImplementation("ch.qos.logback:logback-classic:1.2.3")
}

description = "Objenesis TCK"
