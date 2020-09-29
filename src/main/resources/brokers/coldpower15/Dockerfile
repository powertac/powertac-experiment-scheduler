FROM powertac/broker-base:java

COPY broker.json .

RUN wget http://www.powertac.org/wiki/images/5/5f/COLDPower2015.tar.gz \
    && tar -xvzf COLDPower2015.tar.gz --strip-components=1 COLDPower2015

ENV BROKER_JAR_PATH=/powertac-agent/COLDPower.jar