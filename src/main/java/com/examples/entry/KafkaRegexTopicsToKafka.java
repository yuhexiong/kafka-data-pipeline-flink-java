package com.examples.entry;

import com.examples.deserializer.KafkaTopicPayloadDeserializationSchema;
import com.examples.entity.KafkaTopicPayloadEvent;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import com.examples.serializer.KafkaTopicPayloadSerializationSchema;

import java.util.regex.Pattern;

public class KafkaRegexTopicsToKafka {
    public static void main(String[] args) throws Exception {
        // setup environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // setup source(kafka)
        KafkaSource<KafkaTopicPayloadEvent> source = KafkaSource.<KafkaTopicPayloadEvent>builder()
                .setBootstrapServers("localhost:9092")
                .setTopicPattern(Pattern.compile("^topicV.*"))
                .setGroupId("group-1")
                .setStartingOffsets(OffsetsInitializer.earliest()) // read from earliest
                .setDeserializer(new KafkaTopicPayloadDeserializationSchema()) // as KafkaTopicPayload
                .build();

        // create stream, noWatermarks -> no time attribute
        DataStream<KafkaTopicPayloadEvent> stream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "kafka source");

        // setup destination(kafka)
        KafkaSink<KafkaTopicPayloadEvent> sink = KafkaSink.<KafkaTopicPayloadEvent>builder()
                .setBootstrapServers("localhost:9093,localhost:9094,localhost:9095")
                .setRecordSerializer(new KafkaTopicPayloadSerializationSchema())
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE) // guarantee at least send one time
                .build();

        // print log
        stream.print();
        // sink
        stream.sinkTo(sink);

        // run job
        env.execute("Kafka Regex Topics To Kafka Example");
    }
}
