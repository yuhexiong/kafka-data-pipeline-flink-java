package com.examples.deserializer;

import com.examples.entity.KafkaTopicPayloadEvent;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;

public class KafkaTopicPayloadDeserializationSchema implements KafkaRecordDeserializationSchema<KafkaTopicPayloadEvent> {
    @Override
    public void deserialize(ConsumerRecord<byte[], byte[]> consumerRecord, Collector<KafkaTopicPayloadEvent> collector) throws IOException {
        // get topic and value (as payload) collect into collector
        collector.collect(new KafkaTopicPayloadEvent(consumerRecord.topic(), consumerRecord.value()));
    }

    @Override
    public TypeInformation<KafkaTopicPayloadEvent> getProducedType() {
        return TypeInformation.of(KafkaTopicPayloadEvent.class);
    }
}
