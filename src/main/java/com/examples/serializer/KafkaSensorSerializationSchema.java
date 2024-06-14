package com.examples.serializer;

import com.examples.entity.SensorEvent;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;


import java.nio.charset.StandardCharsets;

public class KafkaSensorSerializationSchema implements KafkaRecordSerializationSchema<SensorEvent.SensorDataEvent> {
    final private String topic;

    public KafkaSensorSerializationSchema(String topic) {
        this.topic = topic;
    }

    @Override
    public ProducerRecord<byte[], byte[]> serialize(SensorEvent.SensorDataEvent sensorDataEvent, KafkaSinkContext kafkaSinkContext, Long aLong) {
        try {
            return new ProducerRecord<>(this.topic, new ObjectMapper().writeValueAsString(sensorDataEvent).getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}