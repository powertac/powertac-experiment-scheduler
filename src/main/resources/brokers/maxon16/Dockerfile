FROM powertac/broker-base:java

RUN wget http://www.powertac.org/wiki/images/c/c4/Maxon16.zip \
    && unzip -p Maxon16.zip maxon16.jar > maxon16.jar \
    && rm Maxon16.zip

COPY broker.json .

ENV BROKER_JAR_PATH=/powertac-agent/maxon16.jar