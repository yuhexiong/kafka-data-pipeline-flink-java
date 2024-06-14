package com.examples.connector;

import org.apache.doris.flink.cfg.DorisOptions;
import org.apache.doris.flink.cfg.DorisReadOptions;
import org.apache.doris.flink.deserialization.SimpleListDeserializationSchema;
import org.apache.doris.flink.source.DorisSource;

import java.util.List;

public class DorisSourceConnector {
    String feNodes;
    String table;
    String username;
    String password;

    // constructor
    public DorisSourceConnector(String feNodes, String table, String username, String password) {
        this.feNodes = feNodes;
        this.table = table;
        this.username = username;
        this.password = password;
    }

    @SuppressWarnings("unchecked")
    public DorisSource<List<?>> buildDorisSource(String query) {
        // doris source option
        DorisOptions.Builder builder = DorisOptions.builder()
                .setFenodes(this.feNodes)
                .setTableIdentifier(this.table)
                .setUsername(this.username)
                .setPassword(this.password);

        DorisReadOptions.Builder readOptionsBuilder = DorisReadOptions.builder();
        if (query != null) {
            readOptionsBuilder.setFilterQuery(query);
        }

        return DorisSource.<List<?>>builder()
                .setDorisOptions(builder.build())
                .setDorisReadOptions(readOptionsBuilder.build())
                .setDeserializer(new SimpleListDeserializationSchema())
                .build();
    }
}
