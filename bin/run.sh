#!/usr/bin/env bash

n=0
if [ -n "$1" ]; then
  n="$1"
fi

tag=latest
if [ -n "$2" ]; then
  tag="$2"
fi

: ${HOST:=$(ipconfig getifaddr en0)}
: ${HOST:=$(ipconfig getifaddr en1)}
: ${HOST:=$(ipconfig getifaddr en2)}
: ${HOST:=$(ipconfig getifaddr en3)}
: ${HOST:=$(ipconfig getifaddr en4)}

docker run \
  --detach \
  --name akkadocker-${n} \
  --publish 801${n}:8080 \
  akkadocker:${tag} \
  -Dconstructr.coordination.host=${HOST}