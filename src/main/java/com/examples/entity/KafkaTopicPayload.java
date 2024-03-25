package com.examples.entity;

import lombok.Data;

// define topic and payload class
@Data // auto generate getter, setter
public class KafkaTopicPayload {
    // data structure we want to deserialze
    private String topicName;
    private byte[] payload;

    // constructor
    public KafkaTopicPayload(String topicName, byte[] payload) {
        this.topicName = topicName;
        this.payload = payload;
    }
}
