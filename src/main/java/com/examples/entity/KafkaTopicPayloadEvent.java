package com.examples.entity;

import lombok.Data;

// define topic and payload class
@Data // auto generate getter, setter
public class KafkaTopicPayloadEvent {
    // data structure we want to deserialize
    private String topicName;
    private byte[] payload;

    // constructor
    public KafkaTopicPayloadEvent(String topicName, byte[] payload) {
        this.topicName = topicName;
        this.payload = payload;
    }
}
