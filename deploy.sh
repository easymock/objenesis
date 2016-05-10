#!/bin/bash

# This script expects:
# - the version to be deployed as the first parameter
# - bintray_user to be an environment variable
# - bintray_api_key to be an environment variable

# to exit in case of error
set -e

function pause {
    echo
    read -p "Press [enter]  to continue"
}

# make sure the version is passed in parameter
if [ "$1" == "" ]; then
    echo "Version to deploy should be provided"
    exit 1
fi

version=$1

message="should be an environment variable"
[ -z "$gpg_passphrase" ] && echo "gpg_passphrase $message" && exit 1
[ -z "$bintray_api_key" ] && echo "bintray_api_key $message" && exit 1
[ -z "$bintray_user" ] && echo "bintray_user $message" && exit 1

mvn release:prepare -Pall,full,release
git push
git push --tags
mvn release:perform -Pall,full,release

# Create the distribution in bintray
content="{ \"name\": \"$version\", \"desc\": \"$version\", \"released\": \"${date}T00:00:00.000Z\", \"github_use_tag_release_notes\": true, \"vcs_tag\": \"$version\" }"
curl -XPOST -H "Content-Type: application/json" -u$bintray_user:$bintray_api_key \
  -d "$content" https://api.bintray.com/packages/easymock/distributions/objenesis/versions

curl -v -H "X-GPG-PASSPHRASE: $gpg_passphrase" -u$bintray_user:$bintray_api_key -T "main/target/objenesis-${version}-bin.zip" https://api.bintray.com/content/easymock/distributions/objenesis/${version}/objenesis-${version}-bin.zip?publish=1
curl -v -H "X-GPG-PASSPHRASE: $gpg_passphrase" -u$bintray_user:$bintray_api_key -T "tck/target/objenesis-tck-${version}.jar" https://api.bintray.com/content/easymock/distributions/objenesis/${version}/objenesis-tck-${version}.jar?publish=1
curl -v -H "X-GPG-PASSPHRASE: $gpg_passphrase" -u$bintray_user:$bintray_api_key -T "tck-android/target/objenesis-tck-android-${version}.apk" https://api.bintray.com/content/easymock/distributions/objenesis/${version}/objenesis-tck-android-${version}.apk?publish=1
