package com.examples.entry;

import com.examples.deserializer.KafkaSensorDeserializationSchema;
import com.examples.entity.SensorEvent;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.connector.jdbc.JdbcSink;

public class KafkaToDorisByJDBCSink {

    public static void main(String[] args) throws Exception {
        // setup environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // setup source(kafka)
        KafkaSource<SensorEvent> source = KafkaSource.<SensorEvent>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("topic-sensor")
                .setGroupId("group-1")
                .setStartingOffsets(OffsetsInitializer.earliest()) // read from earliest
                .setDeserializer(KafkaRecordDeserializationSchema.valueOnly(new KafkaSensorDeserializationSchema())) // as SensorEvent
                .build();

        // create stream, noWatermarks -> no time attribute
        DataStream<SensorEvent> stream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "kafka source");

        // setup destination(doris)
        SinkFunction<SensorEvent> jdbcSink = JdbcSink.sink(
                // setup query and value
                "INSERT INTO sensor (id, type, timestamp, location, value, unit) VALUES (?, ?, ?, ?, ?, ?)",
                (statement, sensorEvent) -> {
                    statement.setString(1, sensorEvent.getSensorId());
                    statement.setString(2, sensorEvent.getSensorType());
                    statement.setString(3, sensorEvent.getTimestamp());
                    statement.setString(4, sensorEvent.getLocation());
                    statement.setString(5, String.valueOf(sensorEvent.getValue()));
                    statement.setString(6, sensorEvent.getUnit());

                },
                // setup jdbc builder
                JdbcExecutionOptions.builder()
                        .withBatchSize(1000)
                        .withBatchIntervalMs(200)
                        .withMaxRetries(5)
                        .build(),
                // connect doris
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl("jdbc:mysql://localhost:9030/database?useSSL=false")
                        .withDriverName("com.mysql.cj.jdbc.Driver")
                        .withUsername("root")
                        .withPassword("password")
                        .build()
        );


        // print log
        stream.print();

        // sink
        stream.addSink(jdbcSink);

        env.execute("Kafka to Doris Example");
    }
}
