package deserializer;

import entity.KafkaTopicPayload;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;

public class KafkaTopicPayloadDeserializationSchema implements KafkaRecordDeserializationSchema<KafkaTopicPayload> {
    @Override
    public void deserialize(ConsumerRecord<byte[], byte[]> consumerRecord, Collector<KafkaTopicPayload> collector) throws IOException {
        collector.collect(new KafkaTopicPayload(consumerRecord.topic(), consumerRecord.value()));
    }

    @Override
    public TypeInformation<KafkaTopicPayload> getProducedType() {
        return TypeInformation.of(KafkaTopicPayload.class);
    }
}
