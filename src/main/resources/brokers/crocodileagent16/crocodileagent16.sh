#!/usr/bin/env bash

# run Rserve
cat rserve.r | R --no-save

# redirect all arguments to entrypoint.sh
./entrypoint.sh "$@"
