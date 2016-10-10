#!/usr/bin/env bash

PROJECT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."

VERSION=`perl -n -e '/\s*version\s*:=\s*"(.*)"/ && print "$1\n"' < $PROJECT_DIR/build.sbt`

echo "Deploying version ${VERSION}"

ansible-playbook $PROJECT_DIR/deployment/deploy.yml -i $PROJECT_DIR/deployment/hosts -v --tags deploy --extra-vars "version=${VERSION}" -k -u vagrant



