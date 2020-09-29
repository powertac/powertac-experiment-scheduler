#!/usr/bin/env bash

indent() {
    sed  's/^/   /'
}

# check if shared volume exists
if [[ -d /powertac ]];
    then
    echo -e "\u2714 shared volume exists" | indent
  else
    echo -e "\u2716 shared volume does not exist" | indent
fi

# check if run script exists
if [[ -f /powertac-agent/run.sh ]];
    then
    echo -e "\u2714 run script exists" | indent
  else
    echo -e "\u2716 run script does not exist" | indent
fi

# check if cleanup script exists
if [[ -f /powertac-agent/cleanup.sh ]];
    then
    echo -e "\u2714 cleanup script exists" | indent
  else
    echo -e "\u2716 cleanup script does not exist" | indent
fi