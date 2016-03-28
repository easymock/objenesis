Objenesis
========

Objenesis is a library dedicated to bypass the constructor when creating an object. On any JVM there is.

You can find the website and user documentation at http://objenesis.org.

Developer information
=====================

Travis status
-------------
[![Build Status](https://travis-ci.org/easymock/objenesis.svg?branch=master)](https://travis-ci.org/easymock/objenesis)

Environment setup
-----------------

I'm using:
- Maven 3.3.9
- IntelliJ 15 Ultimate (thanks to JetBrains for the license) (it should also work with Eclipse)

To configure your local workspace:
- Import the Maven parent project to Eclipse or IntelliJ
- Import the Eclipse formatting file `objenesis-formatting.xml` (usable in Eclipse or IntelliJ)

To build with Maven
----------------------------

There are two different levels of build.

### Build without any active profile

It is a basic compilation of the application.

`mvn install`

### Full build

This build will create the source and javadoc jars and run findbugs.

`mvn install -Pfull`

To run special builds
-----------------------------

### Run the Android TCK

- Install the Android SDK
- Configure a device (real or simulated)
- Add an `ANDROID_HOME` to target the Android SDK
- Add `$ANDROID_HOME/platform-tools` to your path
- Activate the debug mode if it's a real device
- `mvn package -Pandroid`

### Run the benchmarks

`mvn package -Pbenchmark`

### Generate the website

`mvn package -Pwebsite`

To update the versions
----------------------

- `mvn versions:set -DnewVersion=X.Y -Pall`
- `mvn versions:commit -Pall` if everything is ok, `mvn versions:revert -Pall` otherwise

Configure to deploy to the Sonatype maven repository
----------------------------------------------------
- You will first need to add something like this to your settings.xml
```xml
<servers>
  <server>
    <id>sonatype-nexus-snapshots</id>
    <username>sonatypeuser</username>
    <password>sonatypepassword</password>
  </server>
  <server>
    <id>sonatype-nexus-staging</id>
    <username>sonatypeuser</username>
    <password>sonatypepassword</password>
  </server>
</servers>
```
- Then follow the instructions from the site below to create your key to sign the deployed items

http://www.sonatype.com/people/2010/01/how-to-generate-pgp-signatures-with-maven/

To check dependencies and plugins versions
--------------------------------------------------------------------------------------
`mvn versions:display-dependency-updates versions:display-plugin-updates -Pall`

To update the license
--------------------------------------------------------------------------------------
`mvn validate license:format -Pall`

To release
--------------------------------------------------------------------------------------
* Add the release notes in `website/site/content/notes.html`
* Add this server to your `settings.xml`
```
<server>
   <id>bintray</id>
   <username>your-user-name</username>
   <password>your-api-key</password>
</server> 
```

* Launch `deploy.sh`
* Flag the bin, tck and tck-android as "Show in download list" in bintray
* Add release notes on the tag in GitHub. Also add the bin, tck and tck-android jars to the release
* Close the milestone in GitHub and create the new one

Deploy the website
--------------------------------------------------------------------------------------
* Make sure the pom is at the version you want to release
* Launch `deploy_website/sh`
