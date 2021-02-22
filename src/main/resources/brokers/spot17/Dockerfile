FROM powertac/broker:java-8

RUN wget http://www.powertac.org/wiki/images/f/f3/SPOT17-Binary-Release.zip \
    && unzip -p SPOT17-Binary-Release.zip SPOT17-Binary_Release/SPOT17.jar > SPOT17.jar \
    && rm SPOT17-Binary-Release.zip

COPY broker.json .

ENV BROKER_JAR_PATH=/powertac-agent/SPOT17.jar