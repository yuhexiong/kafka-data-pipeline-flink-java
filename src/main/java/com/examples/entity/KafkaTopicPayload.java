package entity;

import lombok.Data;

@Data // aut generate getter, setter
public class KafkaTopicPayload {
    private String topicName;
    private byte[] payload;


    public KafkaTopicPayload(String topicName, byte[] payload) {
        this.topicName = topicName;
        this.payload = payload;
    }
}
