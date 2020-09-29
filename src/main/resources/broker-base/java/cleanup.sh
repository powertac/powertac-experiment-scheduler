#!/usr/bin/env bash

# TODO : might no longer be necessary

LOG_DIR=/powertac-agent/log

# copy files from log dir to shared dir (/powertac) if any exist
if [[ -n "$(ls -A ${LOG_DIR})" ]]; then
    cp -r ${LOG_DIR}/* /powertac
fi

# set read/write permissions on all files if any exist
if [[ -n "$(ls -A /powertac)" ]]; then
    chmod -R a+rw /powertac/*
fi

exit $?

