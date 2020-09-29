#!/usr/bin/env bash

# just a information wrapper for the powertac-server.sh script

brokerFile () {
    cat broker.json | jq -r $1;
}

echo -e "\n---------------"
echo -e "POWERTAC BROKER"
echo -e "---------------"
echo -e "name:\t $(brokerFile .name)"
echo -e "image:\t $(brokerFile .image)"
echo -e "config:\t $(brokerFile .config)"
echo -e "---------------\n"

# debug mode for inspecting the running container
if [[ ! -z $1 ]]; then
    if [[ "$1" == "inspect" ]]; then
        echo -e "running in inspection mode ..."
        # keep script (and therefore container) running until it is stopped it is stopped externally
        while true; do
            sleep 1d
        done
        exit
    fi
fi

echo -e "   running agent ..."
/powertac-agent/run.sh $@
RUN_EXIT_STATUS=$?

if [[ ${RUN_EXIT_STATUS} -eq 0 ]];
    then
        echo -e " \u2714 run completed successfully"
    else
        echo -e " \u2716 run completed with error; exit status ${RUN_EXIT_STATUS}"
        echo -e "   exiting..."
        exit ${RUN_EXIT_STATUS}
fi

echo -e "   cleaning up ..."
/powertac-agent/cleanup.sh
CLEANUP_EXIT_STATUS=$?

if [[ ${CLEANUP_EXIT_STATUS} -eq 0 ]];
    then
        echo -e " \u2714 cleanup completed successfully"
    else
        echo -e " \u2716 cleanup completed with error; exit status ${CLEANUP_EXIT_STATUS}"
        echo -e "   exiting..."
        exit ${CLEANUP_EXIT_STATUS}
fi
