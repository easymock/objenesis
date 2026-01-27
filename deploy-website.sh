#!/bin/bash

function pause {
    echo
    read -p "Press [enter]  to continue"
}

# to exit in case of error
set -e

# make sure the script is launched from the project root directory
if [ "$(dirname $0)" != "." ]; then
    echo "The script should be launched from Objenesis root directory"
    exit 1
fi

# clone the website branch
echo "************** CLONE ************************"
git clone --depth=1 --branch gh-pages --single-branch git@github.com:easymock/objenesis.git site

pushd site

# delete all none hidden directories (keep .git for instance)
ls -1 | xargs rm -rf

# compile de new website
pushd ../website
mvn clean package
popd

# copy the new site to the branch
cp -R ../website/target/xsite/* .

# to help debugging in case of issue
echo "************** STATUS************************"
git status

# push the site
echo "************** COMMIT ***********************"
git add --ignore-removal .
git commit -m "from master $(git log | head -n 1)"

pause
echo "************** PUSH ************************"
git push origin gh-pages

popd

rm -rf site
