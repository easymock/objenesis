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
    war
}

dependencies {
    implementation("com.google.appengine:appengine-api-1.0-sdk:1.9.82")
    implementation("jstl:jstl:1.2")
    implementation(project(":objenesis-tck"))
    testImplementation("com.google.appengine:appengine-testing:1.9.82")
    testImplementation("com.google.appengine:appengine-api-stubs:1.9.82")
    providedCompile("javax.servlet:servlet-api:2.5")
    providedCompile("javax.servlet.jsp:jsp-api:2.2")
    providedCompile(project(":objenesis"))
}

description = "Objenesis GAE"
