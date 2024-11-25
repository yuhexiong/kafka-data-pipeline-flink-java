# Kafka Data Pipeline Flink
使用 Flink 寫的資料管道，用於將資料從 Kafka 傳輸到 Kafka、Doris 和 MongoDB，也支援合併兩個資料來源。  

## Overview

- 平台: JDK 11
- 構建工具: Apache Maven v3.9.6
- 資料處理框架: Flink v1.18.1


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

編輯 `YourJavaClass` 成你想要跑的 Class  
```
docker compose run --rm -e MY_CLASS=YourJavaClass myFlinkJob
```



## Entry

### 1. KafkaToKafka

將 Kafka (localhost:9092) 中的 `topic-source` 的所有 message 轉換至 Kafka (localhost:9092) 中的 `topic-sink`  


### 2. KafkaRegexTopicsToKafka


將 Kafka (localhost:9092) 中所有符合正則表達式 `^topicV.*` 的 Topic 備份到 Kafka (localhost:9093)、 Kafka (localhost:9094) 和 Kafka (localhost:9095) 中的相同 Topic。  

### 3. KafkaToDorisByJDBCSink / KafkaToDorisByDorisSink

將 Kafka (localhost:9092) 中的 `topic-sensor` 的 `data` 這個 array/list 拆解後轉入 Doris (localhost:9030) 資料庫 (database.sensor)  

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

將 Doris (localhost:9030) 資料庫 `database.sensor` 的資料轉換成 `data` 名稱的 array/list 轉入 Kafka (localhost:9092) 的 `topic-sensor`  

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

將 Kafka (localhost:9092) 中的 `topic-sensor` 的 `data` 這個 array/list 拆解並結合 `topic-setting` 的 equipments 和 sensors 設定後 轉入 Doris (localhost:9030) 資料庫 `database.monitoring_data`  

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

將 Kafka (localhost:9092) 中 `topic` 的訊息轉換並存入 MongoDB (localhost:27017) 的 `database.collection`。  

