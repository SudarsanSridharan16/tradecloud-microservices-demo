#!/usr/bin/env bash

service="service"
if [ -n "$1" ]; then
  service="service-$1"
fi

docker rm -f $(docker ps -aq --filter name=${service})