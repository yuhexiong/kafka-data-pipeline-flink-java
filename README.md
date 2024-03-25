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

topic1 in localhost:9092 -> topic2 in localhost:9092  

### KafkaRegexTopicsToKafka

All topics matching "^topicV.*" in localhost:9092 will be backed up to the same topics in localhost:9093, localhost:9094, localhost:9095.  

```
Example: topicV1 in localhost:9092 
  -> topicV1 in localhost:9093
  -> topicV1 in localhost:9094
  -> topicV1 in localhost:9095.  
```

### KafkaToDoris

- Kafka Data Structure
```
{
    "location": "Machine A",
    "timestamp": "2024-03-25T08:00:00",
    "data": [
        {
            "sensorId": "sensor001",
            "sensorType": "Temperature",
            "value": 25.5,
            "unit": "Celsius"
        },
        {
            "sensorId": "sensor002",
            "sensorType": "Humidity",
            "value": 60.2,
            "unit": "%"
        }
    ]
}
```

- doris table

| Sensor ID | Sensor Type      | Location    | Timestamp           | Value | Unit    |  
|-----------|------------------|-------------|---------------------|-------|---------|  
| sensor001 | Temperature      | Machine A   | 2024-03-25T08:00:00 | 25.5  | Celsius |  
| sensor002 | Humidity         | Machine A   | 2024-03-25T08:00:00 | 60.2  | %       |  