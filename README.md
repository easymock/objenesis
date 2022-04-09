# Objenesis

Objenesis is a library dedicated to bypass the constructor when creating an object. On any JVM there is.

You can find the website and user documentation at [objenesis.org](http://objenesis.org).

# Developer information

## Project status

[![Build Status](https://travis-ci.org/easymock/objenesis.svg?branch=master)](https://travis-ci.org/easymock/objenesis)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.objenesis/objenesis/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.objenesis/objenesis)

## Environment setup

I'm using:
- Maven 3.8.4
- IntelliJ Ultimate 2021.3.3 (thanks to JetBrains for the license) (it should also work with Eclipse)

To configure your local workspace:
- Import the Maven parent project to Eclipse or IntelliJ
- Import the Eclipse formatting file `objenesis-formatting.xml` (usable in Eclipse or IntelliJ)

## To build with Maven

There are two different levels of build.

### Build without any active profile

It is a basic compilation of the application.

`mvn install`

### Full build

This build will create the source and javadoc jars and run spotbugs.

`mvn install -Pfull`

## To run special builds

### Run the Android TCK

- Install the Android SDK (`brew cask install android-sdk`)
- Install `platform-tools` and `build-tools` using the sdkmanager (`sdkmanager "platform-tools" "build-tools"`)
- Add an `ANDROID_HOME` to target the Android SDK (`export ANDROID_HOME=$(realpath $(echo "$(dirname $(readlink $(which sdkmanager)))/../.."))`)
- Configure a device (real or simulated) and launch it (use API 26, after that it asks for a signature, that isn't supported yet)
- Activate the debug mode if it's a real device
- `mvn package -Pandroid`

### Run the benchmarks

```bash
mvn package -Pbenchmark
cd benchmark
./launch.sh
```

### Generate the website

`mvn package -Pwebsite`

## To update the versions

- `mvn versions:set -DnewVersion=X.Y -Pall`
- `mvn versions:commit -Pall` if everything is ok, `mvn versions:revert -Pall` otherwise

## Configure to deploy to the Sonatype maven repository

- You will first need to add something like this to your settings.xml
```xml
<servers>
   <server>
      <id>ossrh</id>
      <username>sonatypeuser</username>
      <password>sonatypepassword</password>
   </server>
</servers>
```
- Then follow the [instructions](https://central.sonatype.org/pages/working-with-pgp-signatures.html) from the site below to create your key to sign the deployed items

## To check dependencies and plugins versions

`mvn versions:display-dependency-updates versions:display-plugin-updates -Pall`

## To upgrade the Maven wrapper

`mvn -N io.takari:maven:wrapper -Dmaven=3.6.3`

## To update the license

`mvn validate license:format -Pall`

## To release

* Add the release notes in `website/site/content/notes.html` You use this code to generate it

```bash
# Get the milestone matching the version
version=(mvn help:evaluate -Dexpression=project.version -q -DforceStdout | cut -d'-' -f1)
milestone=$(curl -s "https://api.github.com/repos/easymock/objenesis/milestones" | jq ".[] | select(.title==\"$version\") | .number")
echo "<h1>Version $version ($(date '+%Y-%m-%d'))</h1>"
echo
echo "<ul>"  
curl -s "https://api.github.com/repos/easymock/objenesis/issues?milestone=${milestone}&state=all" | jq -r '.[] | "  <li>" + .title + " (#" + (.number|tostring) + ")</li>"'
echo "</ul>"
```

* Launch an Android device (virtual or physical) 
* Launch `./deploy.sh`
* Answer the questions (normally, just acknowledge the proposed default)
* Follow the instructions

If something fails, and you need to rollback a bit, the following commands might help:
```bash
mvn release:rollback -Pall
git tag -d $version
git push origin :refs/tags/$version
git reset --hard HEAD~2
```

If you find something went wrong you can drop the staging repository with `mvn nexus-staging:drop`.

## Deploy the website

* Make sure the pom is at the version you want to release
* Launch `./deploy-website.sh`
