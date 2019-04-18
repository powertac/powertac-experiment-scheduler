#!/bin/bash

HOST="192.168.56.101:8080"
USER="admin"
TOKEN="1186ff617e130303ef797a99a8db55dd2e"

AUTH=$USER:$TOKEN
URL="http://"$HOST"/jenkins/createItem?name=JOBNAME"
PAYLOAD="--data-binary @FILENAME -H Content-Type:text/xml"

install () {
    echo -e "\nConfiguring $1 Job..."
    url="${URL/JOBNAME/$1}"
    payload="${PAYLOAD/FILENAME/$2}"
    cmd="curl -s --write-out %{http_code} -XPOST $payload -u $AUTH $url"

    status=`$cmd`
    if [ "$status" != "200" ] ; then
        echo "Configuration of $1 Failed!"
    else
        echo "Configuration of $1 Success!"
    fi
}

install abort-server-instance config.abort.xml
install start-agent           config.agent.xml
install start-boot-server     config.boot.xml
install kill-server-instance  config.kill.xml
install start-sim-server      config.sim.xml

echo -e "\ndone"
