package com.examples.function;

import com.examples.entity.SensorEvent;
import org.apache.flink.api.common.functions.MapFunction;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class DorisSensorToKafkaMapFunction implements MapFunction<List<?>, SensorEvent.SensorDataEvent> {

    @Override
    public SensorEvent.SensorDataEvent map(List<?> objects) throws Exception {
        /* [id, type, location, timestamp, value, unit] */
        if (objects.size() < 6) {
            return null;
        }

        SensorEvent.SensorDataEvent sensorDataEvent = new SensorEvent.SensorDataEvent();
        sensorDataEvent.setLocation((String) objects.get(2));

        LocalDateTime localDateTimestamp = (LocalDateTime) objects.get(3);
        // localDateTime to long
        ZoneId zone = ZoneId.of("UTC");
        long longTimestamp =  localDateTimestamp.atZone(zone).toInstant().toEpochMilli();
        // long to string with pattern
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String strTimestamp = simpleDateFormat.format(new Date(longTimestamp));
        sensorDataEvent.setTimestamp(strTimestamp);

        // sensor data
        SensorEvent.SensorData sensorData = new SensorEvent.SensorData();
        sensorData.setSensorId((String) objects.get(0));
        sensorData.setSensorType((String) objects.get(1));
        sensorData.setValue((Double) objects.get(4));
        sensorData.setUnit((String) objects.get(5));

        sensorDataEvent.setData(List.of(new SensorEvent.SensorData[]{sensorData}));

        return sensorDataEvent;
    }
}
