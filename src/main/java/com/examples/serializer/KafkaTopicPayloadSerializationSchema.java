package com.examples.serializer;

import com.examples.entity.KafkaTopicPayload;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;

public class KafkaTopicPayloadSerializationSchema implements KafkaRecordSerializationSchema<KafkaTopicPayload> {
    // get topic and payload from collector
    @Nullable
    @Override
    public ProducerRecord<byte[], byte[]> serialize(KafkaTopicPayload kafkaTopicPayload, KafkaSinkContext kafkaSinkContext, Long aLong) {
        return new ProducerRecord<>(kafkaTopicPayload.getTopicName(), kafkaTopicPayload.getPayload());
    }
}
