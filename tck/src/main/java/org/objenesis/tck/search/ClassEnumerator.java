/**
 * Copyright 2006-2016 the original author or authors.
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
package org.objenesis.tck.search;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Taken and adapted from <a href="https://raw.githubusercontent.com/ddopson/java-class-enumerator/master/src/pro/ddopson/ClassEnumerator.java">here</a>
 *
 * @author Henri Tremblay
 */
public class ClassEnumerator {

    private static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("Unexpected ClassNotFoundException loading class '" + className + "'");
        }
    }

    private static void processDirectory(File directory, String pkgname, SortedSet<Class<?>> classes) {

        // Get the list of the files contained in the package
        String[] files = directory.list();

        for (int i = 0; i < files.length; i++) {
            String fileName = files[i];
            // we are only interested in .class files
            if (fileName.endsWith(".class")) {
                // removes the .class extension
                String className = pkgname + '.' + fileName.substring(0, fileName.length() - 6);
                classes.add(loadClass(className));
                continue;
            }

            File subdir = new File(directory, fileName);
            if (subdir.isDirectory()) {
                processDirectory(subdir, pkgname + '.' + fileName, classes);
            }
        }
    }

    private static void processJarfile(URL resource, String pkgname, SortedSet<Class<?>> classes) {
        String relPath = pkgname.replace('.', '/');
        String resPath = resource.getPath();
        String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");

        JarFile jarFile;
        try {
            jarFile = new JarFile(jarPath);
        } catch (IOException e) {
            throw new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", e);
        }
        Enumeration<JarEntry> entries = jarFile.entries();
        while(entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            String className = null;
            if(entryName.endsWith(".class") && entryName.startsWith(relPath) && entryName.length() > (relPath.length() + "/".length())) {
                className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
            }

            if (className != null) {
                classes.add(loadClass(className));
            }
        }
    }

    public static SortedSet<Class<?>> getClassesForPackage(Package pkg) {
        return getClassesForPackage(pkg, ClassEnumerator.class.getClassLoader());
    }

    public static SortedSet<Class<?>> getClassesForPackage(Package pkg, ClassLoader classLoader) {
        SortedSet<Class<?>> classes = new TreeSet<Class<?>>(new Comparator<Class<?>>() {
           public int compare(Class<?> o1, Class<?> o2) {
              return o1.getSimpleName().compareTo(o2.getSimpleName());
           }
        });

        String pkgname = pkg.getName();
        String relPath = pkgname.replace('.', '/');

        // Get a File object for the package
        Enumeration<URL> resources;
        try {
            resources = classLoader.getResources(relPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while(resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.toString().startsWith("jar:")) {
                processJarfile(resource, pkgname, classes);
            } else {
                processDirectory(new File(resource.getPath()), pkgname, classes);
            }
        }

        return classes;
    }

}
