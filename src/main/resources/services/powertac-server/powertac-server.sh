#!/usr/bin/env bash
# control script for the powertac server

serverJar=powertac-server-jar-0.0.1-SNAPSHOT.jar
outFile=bootstrap.xml
inFile=""
propertyFile=""
brokers=""
seedFile=""

# prints a reference of arguments and options for this script
printHelp() {
    # TODO: use stdout & stderr instead
    echo -e "\nUSAGE:"
    echo -e "\n  ./powertac-server.sh <MODE> [OPTIONS]"
    echo -e "\n"
    echo "$(cat common-options.txt)"
    echo -e "\n"
}

# prints a description of the error that occurred and displays a help message
printError() {
    echo -e "An error occurred during script execution: \n"
    echo -e "$1 \n"
    echo -e "please use \`./powertac-server.sh help\` to display script usage"
}

# run the server in bootstrap mode
bootstrap() {

    configOption=""

    if [[ ! -z ${propertyFile} ]]; then
        configOption="--config ${propertyFile}"
    fi

    echo -e "server jar: \t$1"
    echo -e "output file: \t$2"
    echo -e "config file: \t${propertyFile}"
    echo -e "\nstarting server in bootstrap mode ..."

    java    -server \
            -Xmx1024m \
            -jar $1 \
            --boot $2 \
            ${configOption}

    echo -e "\nserver run in bootstrap mode complete"
}

# run the server in simulation mode
simulation() {

    bootstrapOption=""
    brokersOption=""
    configOption=""
    seedOption=""

    if [[ ! -z $2 ]]; then
        bootstrapOption="--boot-data $2"
    fi

    if [[ ! -z ${brokers} ]]; then
        brokersOption="--brokers ${brokers}"
    fi

    if [[ ! -z ${propertyFile} ]]; then
        configOption="--config ${propertyFile}"
    fi

    if [[ ! -z ${seedFile} ]]; then
        seedOption="--random-seeds ${seedFile}"
    fi

    echo -e "server jar: \t\t$1"
    echo -e "bootstrap file: \t$2"
    echo -e "brokers: \t\t$3"
    echo -e "config file: \t\t${propertyFile}"
    echo -e "\nstarting server in simulation mode ..."

    # TODO : remove debugging output
    echo -e "command:"
    echo -e "java -server -Xmx1024m -jar $1 --sim ${bootstrapOption} ${brokersOption} ${configOption} ${seedOption}\n"

    java    -server \
            -Xmx1024m \
            -jar $1 \
            --sim \
            ${bootstrapOption} \
            ${brokersOption} \
            ${configOption} \
            ${seedOption}

    echo -e "\nserver run in simulation mode complete"
}

mode=$1
shift

# check if help is requested
if [[ ${mode} =~ ^(\-\-help|\-h|help)$ ]]; then
    printHelp
    exit 0
fi

# parse options
while getopts "o:x:c:b:f:s:" opt; do
    case ${opt} in
        o ) outFile=$OPTARG
        ;;
        f ) inFile=$OPTARG
        ;;
        x ) serverJar=$OPTARG
        ;;
        c ) propertyFile=$OPTARG
        ;;
        b ) brokers=$OPTARG
        ;;
        s ) seedFile=$OPTARG
        ;;
        \? )
            printError
            exit 1
        ;;
  esac
done

if [[ ${mode} = "boot" ]]; then
    bootstrap ${serverJar}  ${outFile}
    exit $?
fi

if [[ ${mode} = "sim" ]]; then
    simulation ${serverJar} ${inFile}
    exit $?
fi

printError "unrecognized mode ${mode}"
exit 1
