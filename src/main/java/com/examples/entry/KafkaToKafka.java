package entry;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.connector.base.DeliveryGuarantee;

public class KafkaToKafka {

	public static void main(String[] args) throws Exception {
		// setup environment
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		// setup source(kafka)
		KafkaSource<String> source = KafkaSource.<String>builder()
				.setBootstrapServers("localhost:9092")
				.setTopics("topic1")
				.setGroupId("group1")
				.setStartingOffsets(OffsetsInitializer.earliest()) // read from earliest
				.setValueOnlyDeserializer(new SimpleStringSchema()) // as string
				.build();

		// create stream, noWatermarks -> no time attribute
		DataStream<String> stream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "kafka source");

		// setup destination(kafka)
		KafkaSink<String> sink = KafkaSink.<String>builder()
				.setBootstrapServers("localhost:9092")
				.setRecordSerializer(KafkaRecordSerializationSchema.builder()
						.setTopic("topic2")
						.setValueSerializationSchema(new SimpleStringSchema())
						.build()
				)
				.setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE) // guarantee at least send one time
				.build();

		// print log
		stream.print();
		// sink
		stream.sinkTo(sink);

		// run job
		env.execute("Flink Kafka To Kafka Example");
	}
}