package com.examples.serializer;

import com.examples.entity.KafkaTopicValueEvent;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;

public class KafkaTopicValueSerializationSchema implements KafkaRecordSerializationSchema<KafkaTopicValueEvent> {
    // get topic and value from collector
    @Nullable
    @Override
    public ProducerRecord<byte[], byte[]> serialize(KafkaTopicValueEvent kafkaTopicValue, KafkaSinkContext kafkaSinkContext, Long aLong) {
        return new ProducerRecord<>(kafkaTopicValue.getTopicName(), kafkaTopicValue.getValue());
    }
}
