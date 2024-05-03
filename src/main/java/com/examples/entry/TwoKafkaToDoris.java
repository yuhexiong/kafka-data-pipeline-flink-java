package com.examples.entry;

import com.examples.connector.DorisSinkConnector;
import com.examples.deserializer.KafkaSensorDeserializationSchema;
import com.examples.deserializer.KafkaSettingDeserializationSchema;
import com.examples.entity.SensorEvent;
import com.examples.entity.SettingEvent;
import com.examples.function.MonitoringDataBroadcastProcessFunction;
import com.examples.function.MonitoringDataToRowDataFunction;
import org.apache.doris.flink.sink.DorisSink;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.data.GenericRowData;

public class TwoKafkaToDoris {
    public static void main(String[] args) throws Exception {
        // setup environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(10000);
        env.setRuntimeMode(RuntimeExecutionMode.STREAMING);

        // setup source(kafka sensor)
        KafkaSource<SensorEvent> sensorSource = KafkaSource.<SensorEvent>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("topic-1")
                .setGroupId("group-1")
                .setStartingOffsets(OffsetsInitializer.earliest()) // read from earliest
                .setDeserializer(KafkaRecordDeserializationSchema.valueOnly(new KafkaSensorDeserializationSchema())) // as SensorEvent
                .build();

        // create stream, noWatermarks -> no time attribute
        DataStream<SensorEvent> sensorStream = env.fromSource(sensorSource, WatermarkStrategy.noWatermarks(), "kafka sensor source");

        // setup source(kafka setting)
        KafkaSource<SettingEvent> settingSource = KafkaSource.<SettingEvent>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("topic-2")
                .setGroupId("group-1")
                .setStartingOffsets(OffsetsInitializer.earliest()) // read from earliest
                .setDeserializer(KafkaRecordDeserializationSchema.valueOnly(new KafkaSettingDeserializationSchema())) // as SettingEvent
                .build();

        // create stream, noWatermarks -> no time attribute
        DataStream<SettingEvent> settingStream = env.fromSource(settingSource, WatermarkStrategy.noWatermarks(), "kafka setting source");

        // setup destination(doris)
        DorisSinkConnector dorisSinkConnector = new DorisSinkConnector(
                "localhost:8030", "database.monitoring_data", "root", "password");
        DorisSink<GenericRowData> dorisSink = dorisSinkConnector.buildDorisSink(
                new String[] {"equipment_id", "sensor_id", "sensor_type", "sensor_timestamp", "sensor_value", "sensor_unit"},
                new org.apache.flink.table.types.DataType[]
                        {DataTypes.VARCHAR(25), DataTypes.VARCHAR(25), DataTypes.VARCHAR(25), DataTypes.TIMESTAMP(9), DataTypes.DOUBLE(), DataTypes.VARCHAR(100)}
        );


        // connect stream and sink
        MonitoringDataBroadcastProcessFunction broadcastProcessFunction = new MonitoringDataBroadcastProcessFunction();
        BroadcastStream<SettingEvent> sensorMapBroadcastStream = settingStream.broadcast(broadcastProcessFunction.getMapStateDescriptor());
        sensorStream.connect(sensorMapBroadcastStream).process(broadcastProcessFunction).map(new MonitoringDataToRowDataFunction())
                .sinkTo(dorisSink);

        // print log
        sensorStream.print();

        env.execute("Two Kafka to Doris Example");
    }
}
