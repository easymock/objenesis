#!/bin/bash

# This script expects:
# - the version to be deployed as the first parameter

# to exit in case of error
set -e
set -v

function pause {
    echo
    read -p "Press [enter]  to continue"
}

# Weird fix required by GPG. See https://github.com/keybase/keybase-issues/issues/1712. You will have to enter the passphrase on screen
export GPG_TTY=$(tty)

# Make sure we are running a Java version above 9 to get module-test in the release
javaVersion=$(mvn -N help:evaluate -Dexpression="java.version" -q -DforceStdout | cut -d'.' -f1)
if [ $javaVersion -lt 9 ]; then
   echo "Java version must be 9+ for the release"
   exit 1
fi

mvn release:prepare -Pall,full,release

# Need to push now because release:perform will checkout the remote tag
git push
git push --tags

mvn release:perform -Pall,full,release

echo "Please add the release notes and copy binaries (main, tck, exotic) in github"
open "https://github.com/easymock/objenesis/tags"
pause

# Release the jars now on central staging
echo "Check everything is alright, next step will release to central"
echo "Right now you need to delete some of the projects from staging (i.e. benchmark, gae, website) unless it was fixed by the skipStaging flag"
open "https://oss.sonatype.org/#welcome"
pause
pushd target/checkout
mvn -N nexus-staging:release
popd

echo "Close the milestone in GitHub and create the new one"
open "https://github.com/easymock/objenesis/milestones"
pause

echo
echo "Job done!"
echo
