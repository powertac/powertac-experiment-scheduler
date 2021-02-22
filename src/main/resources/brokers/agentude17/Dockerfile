FROM powertac/broker:java-8

COPY broker.json .

RUN wget http://www.powertac.org/wiki/images/c/c1/AgentUDE17.zip \
    && unzip -p AgentUDE17.zip AgentUDE17/AgentUDE17.jar > AgentUDE17.jar \
    && rm AgentUDE17.zip

ENV BROKER_JAR_PATH=/powertac-agent/AgentUDE17.jar