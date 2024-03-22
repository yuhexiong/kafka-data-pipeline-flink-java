package serializer;

import entity.KafkaTopicPayload;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;

public class KafkaTopicPayloadSerializationSchema implements KafkaRecordSerializationSchema<KafkaTopicPayload> {
    @Nullable
    @Override
    public ProducerRecord<byte[], byte[]> serialize(KafkaTopicPayload kafkaTopicPayload, KafkaSinkContext kafkaSinkContext, Long aLong) {
        return new ProducerRecord<>(kafkaTopicPayload.getTopicName(), kafkaTopicPayload.getPayload());
    }
}
