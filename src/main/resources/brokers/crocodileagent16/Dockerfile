FROM powertac/broker-base:java

RUN apk add --update --no-cache R R-dev g++ \
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