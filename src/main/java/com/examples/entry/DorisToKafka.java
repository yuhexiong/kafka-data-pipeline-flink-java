package com.examples.entry;

import com.examples.connector.DorisSourceConnector;
import com.examples.entity.SensorEvent;
import com.examples.function.DorisSensorToKafkaMapFunction;
import com.examples.serializer.KafkaSensorSerializationSchema;
import org.apache.doris.flink.source.DorisSource;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;

public class DorisToKafka {

	public static void main(String[] args) throws Exception {
		// setup environment
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		// setup source(doris)
		DorisSourceConnector dorisSourceConnector = new DorisSourceConnector(
				"localhost:8030", "database.sensor", "root", "password");
		DorisSource<List<?>> source = dorisSourceConnector.buildDorisSource(null);

		// create stream, noWatermarks -> no time attribute
		DataStream<List<?>> stream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "doris source");

		// setup destination(kafka)
		KafkaSink<SensorEvent.SensorDataEvent> sink = KafkaSink.<SensorEvent.SensorDataEvent>builder()
				.setBootstrapServers("localhost:9092")
				.setRecordSerializer(new KafkaSensorSerializationSchema("topic-1"))
				.setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE) // guarantee at least send one time
				.build();

		// print log
		stream.print();
		// sink
		stream.map(new DorisSensorToKafkaMapFunction()).sinkTo(sink);

		// run job
		env.execute("Doris To Kafka Example");
	}
}