package com.examples.function;

import com.examples.entity.DorisMonitoringDataEvent;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.StringData;
import org.apache.flink.table.data.TimestampData;

// DorisMonitoringDataEvent to GenericRowData
public class MonitoringDataToRowDataFunction implements MapFunction<DorisMonitoringDataEvent, GenericRowData> {
    public GenericRowData map(DorisMonitoringDataEvent monitoringDataEvent) throws Exception {
        GenericRowData genericRowData = new GenericRowData(6);
        genericRowData.setField(0, monitoringDataEvent.getEquipmentId());
        genericRowData.setField(1, monitoringDataEvent.getSensorId());
        genericRowData.setField(2, monitoringDataEvent.getSensorType());
        genericRowData.setField(3, TimestampData.fromEpochMillis(monitoringDataEvent.getSensorTimestamp()));
        genericRowData.setField(4, monitoringDataEvent.getSensorValue());
        genericRowData.setField(5, monitoringDataEvent.getSensorUnit());
        return genericRowData;
    }
}
