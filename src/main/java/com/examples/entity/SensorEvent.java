package com.examples.entity;

import lombok.Data;
import java.util.List;
import com.alibaba.fastjson2.annotation.JSONField;

// define sensor event class
@Data // auto generate getter, setter
public class SensorEvent {
    // data structure we want to deserialize
    private String sensorId;
    private String sensorType;
    private String timestamp;
    private String location;
    private Double value;
    private String unit;

    // constructor
    public SensorEvent(String sensorId, String sensorType, String timestamp, String location, Double value, String unit) {
        this.sensorId = sensorId;
        this.sensorType = sensorType;
        this.timestamp = timestamp;
        this.location = location;
        this.value = value;
        this.unit = unit;
    }

    // data structure before we deserialize
    @Data
    public static class SensorDataEvent {
        private String location;
        private String timestamp;
        private List<SensorData> data;
    }

    @Data
    public static class SensorData {
        private String sensorId;
        private String sensorType;
        private Double value;
        private String unit;
    }
}
