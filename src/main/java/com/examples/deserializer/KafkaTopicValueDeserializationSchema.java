package com.examples.deserializer;

import com.examples.entity.KafkaTopicValueEvent;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;

public class KafkaTopicValueDeserializationSchema implements KafkaRecordDeserializationSchema<KafkaTopicValueEvent> {
    @Override
    public void deserialize(ConsumerRecord<byte[], byte[]> consumerRecord, Collector<KafkaTopicValueEvent> collector) throws IOException {
        // get topic and value collect into collector
        collector.collect(new KafkaTopicValueEvent(consumerRecord.topic(), consumerRecord.value()));
    }

    @Override
    public TypeInformation<KafkaTopicValueEvent> getProducedType() {
        return TypeInformation.of(KafkaTopicValueEvent.class);
    }
}
