package com.examples.deserializer;

import com.alibaba.fastjson2.JSON;
import com.examples.entity.SettingEvent;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;

import java.io.IOException;

public class KafkaSettingDeserializationSchema implements DeserializationSchema<SettingEvent> {
    @Override
    public SettingEvent deserialize(byte[] bytes) throws IOException {
        return null;
    }

    @Override
    public void deserialize(byte[] bytes, Collector<SettingEvent> collector) throws IOException {
        SettingEvent settingEvent = JSON.parseObject(bytes, SettingEvent.class);
        collector.collect(settingEvent);
    }

    @Override
    public boolean isEndOfStream(SettingEvent settingEvent) {
        return false;
    }

    @Override
    public TypeInformation<SettingEvent> getProducedType() {
        return TypeInformation.of(SettingEvent.class);
    }
}
