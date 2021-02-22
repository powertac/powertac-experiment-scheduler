FROM powertac/broker:java-11

RUN wget https://www.powertac.org/wiki/images/3/31/TUC_TAC.zip \
    && unzip -p TUC_TAC.zip TUC_TAC/TUC_TAC_2020.jar > TUC_TAC_2020.jar \
    && rm TUC_TAC.zip

COPY broker.json .

ENV BROKER_JAR_PATH=/powertac-agent/TUC_TAC_2020.jar