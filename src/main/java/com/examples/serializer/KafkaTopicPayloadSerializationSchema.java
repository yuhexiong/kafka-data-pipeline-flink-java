package com.examples.serializer;

import com.examples.entity.KafkaTopicPayloadEvent;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;

public class KafkaTopicPayloadSerializationSchema implements KafkaRecordSerializationSchema<KafkaTopicPayloadEvent> {
    // get topic and payload from collector
    @Nullable
    @Override
    public ProducerRecord<byte[], byte[]> serialize(KafkaTopicPayloadEvent kafkaTopicPayload, KafkaSinkContext kafkaSinkContext, Long aLong) {
        return new ProducerRecord<>(kafkaTopicPayload.getTopicName(), kafkaTopicPayload.getPayload());
    }
}
