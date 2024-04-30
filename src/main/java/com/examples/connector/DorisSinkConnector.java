package com.examples.connector;

import org.apache.doris.flink.cfg.DorisOptions;
import org.apache.doris.flink.cfg.DorisExecutionOptions;
import org.apache.doris.flink.cfg.DorisReadOptions;
import org.apache.doris.flink.sink.DorisSink;
import org.apache.doris.flink.sink.writer.serializer.DorisRecordSerializer;
import org.apache.doris.flink.sink.writer.serializer.RowDataSerializer;
import org.apache.flink.table.types.DataType;

import java.util.Properties;

public class DorisSinkConnector {
    String fenodes;
    String table;
    String username;
    String password;

    public DorisSinkConnector(String fenodes, String table, String username, String password) {
        this.fenodes = fenodes;
        this.table = table;
        this.username = username;
        this.password = password;
    }

    @SuppressWarnings("unchecked")
    public <T> DorisSink<T> buildDorisSink(String[] fields, DataType[] types) {
        //doris sink option
        DorisSink.Builder<T> builder = DorisSink.builder();
        DorisOptions.Builder dorisBuilder = DorisOptions.builder()
                .setFenodes(this.fenodes)
                .setTableIdentifier(this.table)
                .setUsername(this.username)
                .setPassword(this.password);

        Properties properties = new Properties();
        properties.setProperty("format", "json");
        properties.setProperty("read_json_by_line", "true");

        DorisExecutionOptions.Builder executionBuilder = DorisExecutionOptions.builder();
        executionBuilder.setLabelPrefix("labelPrefix")
                .setDeletable(false)
                .setStreamLoadProp(properties); // set properties

        RowDataSerializer rowDataSerializer = RowDataSerializer.builder().setFieldNames(fields).setType("json").setFieldType(types).build();
        builder.setDorisReadOptions(DorisReadOptions.builder().build())
                .setDorisExecutionOptions(executionBuilder.build())
                .setSerializer((DorisRecordSerializer<T>) rowDataSerializer)
                .setDorisOptions(dorisBuilder.build());

        return builder.build();
    }
}
