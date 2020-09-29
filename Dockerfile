FROM maven:3.6-jdk-8-alpine AS rachma-build
WORKDIR /rachma
COPY . .
RUN mvn clean package

FROM openjdk:8-jre-alpine
WORKDIR /opt/rachma
ENV RACHMA_JAR=rachma-0.1.0-SNAPSHOT.jar
COPY --from=rachma-build /rachma/target/${RACHMA_JAR} ./rachma.jar

VOLUME /var/run/docker.sock
VOLUME /var/opt/jobs
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/opt/rachma/rachma.jar"]

CMD ["--directory.base=/var/opt/jobs/", "--application.environment=container"]