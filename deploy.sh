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

echo "Update the Maven version to the release version"
mvn versions:set -DremoveSnapshot=true -DgenerateBackupPoms=false -Pall

echo "Deploy"
mvn deploy -Pall,full,release
echo "Check deployment to central"
pause

echo "Commit release"
mvn scm:checkin -Dmessage='[release] Release ${project.version}' -DpushChanges=false

echo "Tag"
mvn scm:tag -Dtag='${project.version}' -DpushChanges=false

echo "Move to the next maven version"
mvn versions:set -DnextSnapshot=true -DgenerateBackupPoms=false -Pall

echo "Commit next version"
mvn scm:checkin -Dmessage='[release] Start of ${project.version}' -DpushChanges=false

echo "Check everything is alright before pushing"
pause

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
