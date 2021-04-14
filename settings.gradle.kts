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

pluginManagement {
   repositories {
      gradlePluginPortal()
      google()
   }
   resolutionStrategy {
      eachPlugin {
         if(requested.id.namespace == "com.android"){
            useModule("com.android.tools.build:gradle:4.1.3")
         }
      }
   }
}

rootProject.name = "objenesis-parent"
include(":objenesis-tck-android")
include(":gae")
include(":objenesis-benchmark")
include(":objenesis")
include(":objenesis-tck")
include(":objenesis-website")
project(":objenesis-tck-android").projectDir = file("tck-android")
project(":objenesis-benchmark").projectDir = file("benchmark")
project(":objenesis").projectDir = file("main")
project(":objenesis-tck").projectDir = file("tck")
project(":objenesis-website").projectDir = file("website")
