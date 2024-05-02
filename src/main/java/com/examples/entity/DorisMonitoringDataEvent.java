package com.examples.entity;

import lombok.Data;

// define doris monitoring data class
@Data // auto generate getter, setters
public class DorisMonitoringDataEvent {
    String equipmentId;
    String sensorId;
    String sensorType;
    long sensorTimestamp;
    Double sensorValue;
    String sensorUnit;
}
