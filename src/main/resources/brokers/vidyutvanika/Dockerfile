FROM powertac/broker-base:java

RUN wget http://www.powertac.org/wiki/images/9/9c/VidyutVanika.zip \
    && unzip -p VidyutVanika.zip VidyutVanika-1.4.1.jar > VidyutVanika-1.4.1.jar \
    && rm VidyutVanika.zip

COPY broker.json .

ENV BROKER_JAR_PATH=/powertac-agent/VidyutVanika-1.4.1.jar