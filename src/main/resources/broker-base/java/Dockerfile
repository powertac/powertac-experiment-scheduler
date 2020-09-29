FROM openjdk:8-jre-alpine
WORKDIR /powertac-agent
COPY entrypoint.sh .
COPY run.sh .
COPY cleanup.sh .
RUN apk add --update --no-cache jq bash
VOLUME /powertac-agent/log
ENTRYPOINT ["/powertac-agent/entrypoint.sh"]
