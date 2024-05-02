package com.examples.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

// define setting class
@Data // auto generate getter, setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SettingEvent {
    @JsonProperty("equipments")
    EquipmentSettingEvent[] equipments;

    @JsonProperty("sensors")
    SensorSettingEvent[] sensors;

    @Data // auto generate getter, setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EquipmentSettingEvent {
        @JsonProperty("id")
        String id;
        @JsonProperty("name")
        String name;
        @JsonProperty("location")
        String location;
    }

    @Data // auto generate getter, setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SensorSettingEvent {
        @JsonProperty("id")
        String id;
        @JsonProperty("equipments")
        String[] equipmentIds;
    }

}
