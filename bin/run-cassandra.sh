#!/usr/bin/env bash

docker run \
  --detach \
  --name cassandra \
  --publish 9042:9042 \
  cassandra:3.7