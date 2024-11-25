package com.examples.entry;

import com.examples.connector.DorisSinkConnector;
import com.examples.deserializer.KafkaSensorDeserializationSchema;
import com.examples.entity.SensorEvent;
import com.examples.function.SensorEventToRowDataFunction;
import org.apache.doris.flink.sink.DorisSink;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.data.GenericRowData;

public class KafkaToDorisByDorisSink {

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
        DorisSinkConnector dorisSinkConnector = new DorisSinkConnector(
                "localhost:8030", "database.sensor", "root", "password");
        DorisSink<GenericRowData> dorisSink = dorisSinkConnector.buildDorisSink(
                new String[] {"id", "type", "timestamp", "location", "value", "unit"},
                new org.apache.flink.table.types.DataType[]
                        {DataTypes.VARCHAR(25), DataTypes.VARCHAR(25), DataTypes.TIMESTAMP(9), DataTypes.VARCHAR(25), DataTypes.DOUBLE(), DataTypes.VARCHAR(25)}
        );

        // print log
        stream.print();

        // sink
        stream.map(new SensorEventToRowDataFunction()).sinkTo(dorisSink);

        env.execute("Kafka to Doris By DorisSink Example");
    }
}
