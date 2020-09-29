FROM powertac/broker-base:java

RUN wget http://www.powertac.org/wiki/images/f/f9/CwiBroker2015.tar.gz \
    && tar -xvzf CwiBroker2015.tar.gz cwiBroker2015.jar \
    && rm CwiBroker2015.tar.gz

COPY broker.json .

ENV BROKER_JAR_PATH=/powertac-agent/cwiBroker2015.jar