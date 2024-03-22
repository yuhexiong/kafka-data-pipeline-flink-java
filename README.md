# Kafka Data Pipeline Flink

## Overview

- Platform: JDK 11
- Build Tool: Apache Maven v3.9.6
- Data Processing Framework: Flink v1.18.1


## Run

### Build Maven Project
```
mvn clean package

```

### Run Docker Container
```
# build
docker compose build

# run
# example: docker compose run --rm -e MY_CLASS=KafkaToKafka myapp
docker compose run --rm -e MY_CLASS=YourJavaClass myapp
```



## Entry

### KafkaToKafka