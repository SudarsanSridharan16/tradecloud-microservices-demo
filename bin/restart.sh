#!/usr/bin/env bash

PROJECT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."

docker rm -f $(docker ps -aq --filter name=service)

ID1=$(bash ${PROJECT_DIR}/bin/run-service.sh identity | tail -n-1)
ID2=$(bash ${PROJECT_DIR}/bin/run-service.sh identity 1 | tail -n-1)
ID3=$(bash ${PROJECT_DIR}/bin/run-service.sh item 2 | tail -n-1)
ID4=$(bash ${PROJECT_DIR}/bin/run-service.sh item 3 | tail -n-1)

docker logs -f ${ID3}
