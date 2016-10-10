#!/usr/bin/env bash

containers=$(docker ps --quiet --filter "name=akkadocker")
[[ -n ${containers} ]] && docker rm -f ${containers}