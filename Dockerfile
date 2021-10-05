FROM maven:3-openjdk-11 AS build
WORKDIR /opt/powertac/orchestrator/build
COPY . .
RUN mvn clean package

FROM openjdk:11-jre-slim
WORKDIR /opt/powertac/orchestrator
ENV ORCHESTRATOR_JAR=experiment-scheduler-0.1.1-SNAPSHOT.jar
COPY --from=build /opt/powertac/orchestrator/build/target/${ORCHESTRATOR_JAR} ./${ORCHESTRATOR_JAR}
ENTRYPOINT java -jar /opt/powertac/orchestrator/${ORCHESTRATOR_JAR}