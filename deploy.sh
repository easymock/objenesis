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

mvn release:prepare -Pall,full,release

# Need to push now because release:perform will checkout the remote tag
git push
git push --tags

mvn release:perform -Pall,full,release

echo "Please add the release notes in github"
open "https://github.com/easymock/objenesis/tags"
pause

# Release the jars now on central staging
echo "Check everything is alright, next step will release to central"
open "https://oss.sonatype.org/#welcome"
pause
mvn nexus-staging:release

echo "Close the milestone in GitHub and create the new one"
open "https://github.com/easymock/objenesis/milestones"
pause

echo
echo "Job done!"
echo
