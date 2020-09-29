FROM maven:3.6-jdk-8-alpine AS build
WORKDIR /powertac-server
COPY pom.xml .
RUN mvn clean package

FROM openjdk:8-jre-alpine
RUN apk add --update --no-cache bash
WORKDIR /powertac-server
ENV POWERTAC_SERVER_JAR powertac-server-jar-0.0.1-SNAPSHOT.jar
COPY powertac-server.sh .
COPY powertac-server-entrypoint.sh .
COPY common-options.txt .
COPY docker-options.txt .
COPY --from=build /powertac-server/target/${POWERTAC_SERVER_JAR} .
COPY server.properties .
VOLUME /powertac
ENTRYPOINT ["/powertac-server/powertac-server-entrypoint.sh"]
CMD ["help"]