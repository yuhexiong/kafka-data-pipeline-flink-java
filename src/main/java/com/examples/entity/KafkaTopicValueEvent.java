package com.examples.entity;

import lombok.Data;

// define topic and value class
@Data // auto generate getter, setter
public class KafkaTopicValueEvent {
    // data structure we want to deserialize
    private String topicName;
    private byte[] value;

    // constructor
    public KafkaTopicValueEvent(String topicName, byte[] value) {
        this.topicName = topicName;
        this.value = value;
    }
}
