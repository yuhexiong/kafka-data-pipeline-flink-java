FROM openjdk:11-jre-slim

WORKDIR /usr/src/app

COPY target/flinkKafka.jar .

COPY start.sh /usr/local/bin/start.sh

RUN chmod +x /usr/local/bin/start.sh

ENTRYPOINT ["/usr/local/bin/start.sh"]
