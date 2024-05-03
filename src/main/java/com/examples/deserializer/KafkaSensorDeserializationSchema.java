package com.examples.deserializer;

import com.examples.entity.SensorEvent;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;
import com.alibaba.fastjson2.JSON;

import java.io.IOException;

public class KafkaSensorDeserializationSchema implements DeserializationSchema<SensorEvent> {
    @Override
    public SensorEvent deserialize(byte[] bytes) throws IOException {
        return null;
    }

    @Override
    public void deserialize(byte[] bytes, Collector<SensorEvent> collector) throws IOException {
        SensorEvent.SensorDataEvent sensorDataEvent = JSON.parseObject(bytes, SensorEvent.SensorDataEvent.class);
        
        // get timestamp and location
        String timestamp = sensorDataEvent.getTimestamp();
        Long longTimestamp = timestamp > MAX_TIMESTAMP_SECONDS ? timestamp : timestamp * 1000;
        String location = sensorDataEvent.getLocation();

        // use for loop to get every sensor
        for (SensorEvent.SensorData sensorData : sensorDataEvent.getData()) {
            SensorEvent sensorEvent = new SensorEvent(sensorData.getSensorId(), sensorData.getSensorType(), timestamp, longTimestamp, location, sensorData.getValue(), sensorData.getUnit());
            collector.collect(sensorEvent);
        }
    }

    @Override
    public boolean isEndOfStream(SensorEvent sensorEvent) {
        return false;
    }

    @Override
    public TypeInformation<SensorEvent> getProducedType() {
        return TypeInformation.of(SensorEvent.class);
    }
}
