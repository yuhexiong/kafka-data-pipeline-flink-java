package com.examples.function;

import com.examples.entity.SensorEvent;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.StringData;
import org.apache.flink.table.data.Double;
import org.apache.flink.table.data.TimestampData;

public class SensorEventToRowDataFunction implements MapFunction<SensorEvent, GenericRowData> {

    /**
     * @param sensorEvent
     * @return
     * @throws Exception
     */
    @Override
    public GenericRowData map(SensorEvent sensorEvent) throws Exception {
        GenericRowData rowData = new GenericRowData(6);
        rowData.setField(0, StringData.fromString(sensorEvent.getSensorId()));
        rowData.setField(1, StringData.fromString(sensorEvent.getSensorType()));
        rowData.setField(2, TimestampData.fromEpochMillis(sensorEvent.getLongTimestamp()));
        rowData.setField(3, StringData.fromString(sensorEvent.getLocation()));
        rowData.setField(4, sensorEvent.getValue());
        rowData.setField(5, StringData.fromString(sensorEvent.getUnit()));
        return rowData;
    }
}
