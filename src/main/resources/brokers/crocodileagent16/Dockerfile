FROM powertac/broker:java-8

RUN apt-get update \
    && apt-get -y install --no-install-recommends build-essential r-base r-base-dev \
    && apt-get -y autoremove \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* \
    && mkdir -p /var/lib/R/packages/ \
    && chmod a+rwx /var/lib/R/packages/ \
    && echo "install.packages('Rserve', lib='/var/lib/R/packages/', repos='http://cran.us.r-project.org')" | R --save

RUN wget http://www.powertac.org/wiki/images/0/0d/CrocodileAgent16.zip \
    && unzip -p CrocodileAgent16.zip Broker/CrocodileAgent.jar > CrocodileAgent.jar \
    && rm CrocodileAgent16.zip

COPY broker.json .
COPY rserve.r .
COPY crocodileagent16.sh .

ENV BROKER_JAR_PATH=/powertac-agent/CrocodileAgent.jar

ENTRYPOINT ["/powertac-agent/crocodileagent16.sh"]