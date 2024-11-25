# Kafka Data Pipeline Flink

**(also provided Traditional Chinese version document [README-CH.md](README-CH.md).)**  

Data pipeline written by Flink to transfer Kafka to Kafka, Doris and MongoDB, and also merge the two data sources.  

## Overview

- Platform: JDK 11
- Build Tool: Apache Maven v3.9.6
- Data Processing Framework: Flink v1.18.1


## Run

### Build Maven Project
```
mvn clean package
```

### Build Image
```
docker compose build
```

### Run Docker Container

edit `YourJavaClass` to Class you want to run  
```
docker compose run --rm -e MY_CLASS=YourJavaClass myFlinkJob
```



## Entry

### 1. KafkaToKafka

Transfer all messages from `topic-source` in Kafka (localhost:9092) to `topic-sink` in Kafka (localhost:9092).  


### 2. KafkaRegexTopicsToKafka

Backed up all topics matching `^topicV.*` in Kafka (localhost:9092) to the same topics in Kafka (localhost:9093), Kafka(localhost:9094), Kafka(localhost:9095).  

### 3. KafkaToDorisByJDBCSink / KafkaToDorisByDorisSink

Split the `data` array/list in `topic-sensor` in Kafka (localhost:9092) and insert it into the Doris (localhost:9030) database `database.sensor`.

- Kafka Topic `topic-sensor` Message
```json
{
    "location": "Area A",
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

- Doris Table `database.sensor`
```
| id        | type          | location    | timestamp           | value | unit    |  
|-----------|---------------|-------------|---------------------|-------|---------|  
| sensor001 | Temperature   | Area A      | 2024-03-25T08:00:00 | 25.5  | Celsius |  
| sensor002 | Humidity      | Area A      | 2024-03-25T08:00:00 | 60.2  | %       |  
```

### 4. DorisToKafka

Convert the data from the Doris (localhost:9030) database `database.sensor` into an array/list named `data` and transfer it to `topic-sensor` in Kafka (localhost:9092).  

- Doris Table `database.sensor`

```
| id        | type          | location    | timestamp           | value | unit    |  
|-----------|---------------|-------------|---------------------|-------|---------|  
| sensor001 | Temperature   | Area A      | 2024-03-25T08:00:00 | 25.5  | Celsius |  
| sensor002 | Humidity      | Area A      | 2024-03-25T08:00:00 | 60.2  | %       |  
```

- Kafka Topic `topic-sensor` Message
```json
{
    "location": "Area A",
    "timestamp": "2024-03-25T08:00:00",
    "data": [
        {
            "sensorId": "sensor001",
            "sensorType": "Temperature",
            "value": 25.5,
            "unit": "Celsius"
        }
    ]
}
```



### 5. TwoKafkaToDoris

Break down the `data` array/list from `topic-sensor` in Kafka (localhost:9092) and combine it with the equipment and sensor settings from `topic-setting`. Then, transfer the resulting data into the Doris (localhost:9030) database `database.monitoring_data`.  

- Kafka Topic `topic-sensor` Message
```json
{
    "location": "Area A",
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

- Kafka Topic `topic-setting` Message
```json
{
    "equipments": [
        {
            "id": "equipment001",
            "name": "機器1",
            "location": "Area A"
        }
    ],
    "sensors": [
        {
            "id": "sensor001",
            "equipments": ["equipment001", "equipment002"]
        },
        {
            "id": "sensor002",
            "equipments": ["equipment001", "equipment003"]
        }
    ]
}
```

- Doris Table `database.monitoring_data`
```
| equipment_id  | sensor_id | sensor_type   | sensor_timestamp      | sensor_value | sensor_unit  |  
|---------------|-----------|---------------|-----------------------|--------------|--------------|  
| equipment001  | sensor001 | Temperature   | 2024-05-02T08:00:00   | 25.5         | Celsius      |  
| equipment001  | sensor002 | Humidity      | 2024-05-02T08:00:00   | 60.2         | %            |  
```

### 6. KafkaToMongoDB

Transfer message in `topic` in Kafka (localhost:9092) to MongoDB (localhost:27017) `database.collection`  

