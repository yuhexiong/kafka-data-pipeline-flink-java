package com.examples.deserializer;

import com.examples.entity.SensorEvent;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;
import com.alibaba.fastjson2.JSON;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class KafkaSensorDeserializationSchema implements DeserializationSchema<SensorEvent> {
    @Override
    public SensorEvent deserialize(byte[] bytes) throws IOException {
        return null;
    }

    @Override
    public void deserialize(byte[] bytes, Collector<SensorEvent> collector) throws IOException {
        SensorEvent.SensorDataEvent sensorDataEvent = JSON.parseObject(bytes, SensorEvent.SensorDataEvent.class);
        
        // get timestamp
        String timestamp = sensorDataEvent.getTimestamp();
        long longTimestamp;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = dateFormat.parse(timestamp);
            longTimestamp = date.getTime();
            if (longTimestamp < Math.pow(10, 11)) {
                longTimestamp *= 1000; // convert seconds to milliseconds if timestamp is in seconds
            }
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle parsing exception
            longTimestamp = System.currentTimeMillis(); // use current time as fallback
        }

        // get location
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
