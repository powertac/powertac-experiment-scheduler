#!/usr/bin/env bash

# start bunnielearner detached
python3 ./bunnielearner/src/servo.py &

# redirect all arguments to entrypoint.sh
java -jar ${BROKER_JAR_PATH} "$@"