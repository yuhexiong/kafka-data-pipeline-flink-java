package com.examples.function;

import com.examples.entity.DorisMonitoringDataEvent;
import com.examples.entity.SensorEvent;
import com.examples.entity.SettingEvent;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;

import java.util.HashSet;
import java.util.Set;

public class MonitoringDataBroadcastProcessFunction extends BroadcastProcessFunction<SensorEvent, SettingEvent, DorisMonitoringDataEvent> {
    MapStateDescriptor<String, Set<String>> SensorEquipmentDescriptor = new MapStateDescriptor<>("tagMachineMap", BasicTypeInfo.STRING_TYPE_INFO,
            TypeInformation.of(new TypeHint<>() {
            }));

    public MapStateDescriptor<String, Set<String>> getMapStateDescriptor() {
        return SensorEquipmentDescriptor;
    }

    /**
     * @param sensorEvent
     * @param readOnlyContext
     * @param collector
     * @throws Exception
     */
    @Override
    public void processElement(SensorEvent sensorEvent, BroadcastProcessFunction<SensorEvent, SettingEvent, DorisMonitoringDataEvent>.ReadOnlyContext readOnlyContext, Collector<DorisMonitoringDataEvent> collector) throws Exception {
        Set<String> equipments = readOnlyContext.getBroadcastState(SensorEquipmentDescriptor).get(sensorEvent.getSensorId());
        if (equipments == null) { // no equipment
            return;
        }

        for (String equipmentId : equipments) {
            DorisMonitoringDataEvent monitoringDataEvent = getMonitoringDataEvent(sensorEvent, equipmentId);

            System.out.println("Insert monitoringDataEvent: " + monitoringDataEvent);
            collector.collect(monitoringDataEvent);
        }
    }

    private DorisMonitoringDataEvent getMonitoringDataEvent(SensorEvent sensorEvent, String equipmentId) {
        DorisMonitoringDataEvent monitoringDataEvent = new DorisMonitoringDataEvent();
        monitoringDataEvent.setEquipmentId(equipmentId);
        monitoringDataEvent.setSensorId(sensorEvent.getSensorId());
        monitoringDataEvent.setSensorType(sensorEvent.getSensorType());
        monitoringDataEvent.setSensorTimestamp(sensorEvent.getLongTimestamp());
        monitoringDataEvent.setSensorValue(sensorEvent.getValue());
        monitoringDataEvent.setSensorUnit(sensorEvent.getUnit());
        return monitoringDataEvent;
    }

    /**
     * @param settingEvent
     * @param context
     * @param collector
     * @throws Exception
     */
    @Override
    public void processBroadcastElement(SettingEvent settingEvent, BroadcastProcessFunction<SensorEvent, SettingEvent, DorisMonitoringDataEvent>.Context context, Collector<DorisMonitoringDataEvent> collector) throws Exception {
        // clear broadcast state
        context.getBroadcastState(SensorEquipmentDescriptor).clear();

        // sensor and equipment mapping
        for (SettingEvent.SensorSettingEvent sensor : settingEvent.getSensors()) {
            Set<String> equipmentSet = new HashSet<>();
            for (String equipmentId : sensor.getEquipmentIds()) {
                equipmentSet.add(equipmentId);
            }

            context.getBroadcastState(SensorEquipmentDescriptor).put(sensor.getId(), equipmentSet);
        }
    }
}
