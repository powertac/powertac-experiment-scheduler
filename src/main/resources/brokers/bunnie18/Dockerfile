FROM ubuntu:latest

WORKDIR powertac-agent

COPY broker.json .
COPY bunnie18.sh .

ENTRYPOINT ["/powertac-agent/bunnie18.sh"]

RUN apt-get update \
    && apt-get -y install openjdk-8-jre python3-pip wget unzip python3-venv \
    && apt-get -y autoremove \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* \
    && pip3 install virtualenv

RUN wget http://www.powertac.org/wiki/images/c/c9/Bunnie_2018.zip \
    && unzip Bunnie_2018.zip -d . bunnie\ preparation\ to\ upload/bunnielearner/* \
    && unzip Bunnie_2018.zip -d . bunnie\ preparation\ to\ upload/sample-broker-1.4.3.jar \
    && mv bunnie\ preparation\ to\ upload/* . \
    && rm Bunnie_2018.zip \
    && rm -rf bunnie\ preparation\ to\ upload \
    && cd bunnielearner \
    && pip3 install --no-cache-dir -r requirements.txt

ENV BROKER_JAR_PATH=/powertac-agent/sample-broker-1.4.3.jar