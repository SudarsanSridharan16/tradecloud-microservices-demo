#!/usr/bin/env bash

docker run -d \
  -p 8300:8300 -p 8400:8400 -p 8500:8500 -p 8600:53/udp \
  -e 'CONSUL_LOCAL_CONFIG={"node_name": "consul-dev" }' \
  --name=dev-consul consul

