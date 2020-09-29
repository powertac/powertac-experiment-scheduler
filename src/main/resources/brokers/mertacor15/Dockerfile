FROM powertac/broker-base:java

COPY broker.json .

RUN wget http://www.powertac.org/wiki/images/f/ff/Mertacor2015.zip \
    && unzip -p Mertacor2015.zip Mertacor2015/Mertacor15.jar > Mertacor15.jar \
    && rm Mertacor2015.zip

ENV BROKER_JAR_PATH=/powertac-agent/Mertacor15.jar