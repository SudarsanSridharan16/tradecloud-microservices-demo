#!/usr/bin/env bash

docker rm $(docker ps -aq --filter name=akkadocker)