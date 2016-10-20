#!/usr/bin/env bash

PROJECT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."

VERSION=`perl -n -e '/\s*version\s*:=\s*"(.*)"/ && print "$1\n"' < $PROJECT_DIR/build.sbt`

service="service-identity"
if [ -n "$1" ]; then
  service="service-$1"
fi

n=0
if [ -n "$2" ]; then
  n="$2"
fi

tag=${VERSION}
if [ -n "$3" ]; then
  tag="$3"
fi

: ${HOST:=$(ipconfig getifaddr en0)}
: ${HOST:=$(ipconfig getifaddr en1)}
: ${HOST:=$(ipconfig getifaddr en2)}
: ${HOST:=$(ipconfig getifaddr en3)}
: ${HOST:=$(ipconfig getifaddr en4)}

echo "Running docker image ${service} with tag: ${tag} on host: ${HOST}"

docker run \
  --detach \
  --name ${service}-${n} \
  --publish 801${n}:8080 \
  tradecloud/${service}:${tag} \
  -Dconstructr.coordination.host=${HOST} \
  -Dconstructr.consul.agent-name=consul-dev \
  -Dcassandra-journal.contact-points.0=${HOST}:9042 \
  -Dcassandra-snapshot-store.contact-points.0=${HOST}:9042