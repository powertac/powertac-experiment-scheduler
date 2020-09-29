#!/usr/bin/env bash

# just a information wrapper for the powertac-server.sh script
echo -e "\n---------------"
echo -e "POWERTAC SERVER"
echo -e "---------------"

printHelp() {
    echo -e "\nUSAGE:"
    echo -e "\n  docker run [DOCKER_OPTIONS] powertac-server:<VERSION> <MODE> [OPTIONS]"
    echo -e "\n\n$(cat docker-options.txt)"
    echo -e "\n$(cat common-options.txt)"
    echo -e "\nEXAMPLE"
    echo -e "\n   docker run --rm -v /my/local/directory:/data/bootstrap boot -o /data/bootstrap/bootstrap.xml"
    echo -e "\n      this command will start the server in bootstrap mode; after completion the bootstrap.xml file"
    echo -e "      can be found on your local computer in /my/local/directory/bootstrap.xml\n"
}

# debug mode for inspecting the running container
if [[ $1 =~ "inspect" ]]; then
    echo -e "running in inspection mode ..."
    while true; do
        sleep 1h
    done
    exit
fi

# catch help command to deliver docker image specific help message
# otherwise call server script with the passed arguments
if [[ $1 =~ ^(\-\-help|\-h|help)$ ]];
    then
        printHelp
        exit 0
    else
        echo -e "\nstart the docker image with the \`help\` command to get detailed usage information;"
        echo -e "for example: docker run --rm powertac-server help\n"
        ./powertac-server.sh "$@"
        SERVER_EXIT_CODE=$?
        chmod a+rw /powertac/
        exit ${SERVER_EXIT_CODE}
fi