package com.examples.entry;

import com.mongodb.client.model.InsertOneModel;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.connector.mongodb.sink.MongoSink;
import org.bson.BsonDocument;

public class KafkaToMongoDB {
    public static void main(String[] args) throws Exception {
        // setup environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // setup source(kafka)
        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("topic1")
                .setGroupId("group-1")
                .setStartingOffsets(OffsetsInitializer.earliest()) // read from earliest
                .setValueOnlyDeserializer(new SimpleStringSchema()) // as string
                .build();

        // create stream, noWatermarks -> no time attribute
        DataStream<String> stream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "kafka source");

        // setup destination(mongoDB)
        MongoSink<String> sink = MongoSink.<String>builder()
                .setUri("mongodb://root:password@127.0.0.1:27017")
                .setDatabase("database")
                .setCollection("collection")
                .setBatchSize(1000)
                .setBatchIntervalMs(1000)
                .setMaxRetries(3)
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .setSerializationSchema(
                        (input, context) -> new InsertOneModel<>(BsonDocument.parse(input)))
                .build();


        // print log
        stream.print();
        // sink
        stream.sinkTo(sink);

        // run job
        env.execute("Kafka To MongoDB Example");
    }
}
