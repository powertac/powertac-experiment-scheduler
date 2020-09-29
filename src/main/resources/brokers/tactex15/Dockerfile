FROM powertac/broker-base:java

RUN wget http://www.powertac.org/wiki/images/1/1a/TacTex15ReleasePackage.tar.gz \
    && tar -xvzf TacTex15ReleasePackage.tar.gz --strip-components=1 TacTex15ReleasePackage \
    && rm broker.properties \
    && rm TacTex15ReleasePackage.tar.gz

COPY broker.json .

ENV BROKER_JAR_PATH=/powertac-agent/TacTex15.jar