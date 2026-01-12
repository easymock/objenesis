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
mvn release:perform -Pall,full,release
echo "Check deployment to central"
pause

# Need to push now because release:perform will checkout the remote tag
git push
git push --tags

echo "Please add the release notes and copy binaries (main, tck, exotic) in github"
open "https://github.com/easymock/objenesis/tags"
pause

echo "Close the milestone in GitHub and create the new one"
open "https://github.com/easymock/objenesis/milestones"
pause

echo
echo "Job done!"
echo
