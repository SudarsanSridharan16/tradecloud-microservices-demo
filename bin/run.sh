#!/usr/bin/env bash

PROJECT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."

VERSION=`perl -n -e '/\s*version\s*:=\s*"(.*)"/ && print "$1\n"' < $PROJECT_DIR/build.sbt`

n=0
if [ -n "$1" ]; then
  n="$1"
fi

tag=${VERSION}
if [ -n "$2" ]; then
  tag="$2"
fi

: ${HOST:=$(ipconfig getifaddr en0)}
: ${HOST:=$(ipconfig getifaddr en1)}
: ${HOST:=$(ipconfig getifaddr en2)}
: ${HOST:=$(ipconfig getifaddr en3)}
: ${HOST:=$(ipconfig getifaddr en4)}

echo "Running docker image with tag: ${tag} on host: ${HOST}"

docker run \
  --detach \
  --name akkadocker-${n} \
  --publish 801${n}:8080 \
  benniekrijger/akkadocker:${tag} \
  -Dconstructr.coordination.host=${HOST} \
  -Dconstructr.consul.agent-name=${HOST}