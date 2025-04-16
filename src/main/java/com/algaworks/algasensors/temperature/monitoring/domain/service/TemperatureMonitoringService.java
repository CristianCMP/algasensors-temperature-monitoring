package com.algaworks.algasensors.temperature.monitoring.domain.service;

import com.algaworks.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorId;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorMonitoring;
import com.algaworks.algasensors.temperature.monitoring.domain.model.TemperatureLog;
import com.algaworks.algasensors.temperature.monitoring.domain.model.TemperatureLogId;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.SensorMonitoringRepository;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.TemperatureLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class TemperatureMonitoringService {

    private final SensorMonitoringRepository sensorMonitoringRepository;
    private final TemperatureLogRepository temperatureLogRepository;

    @Transactional
    public void processTemperatureReading(TemperatureLogData temperatureLogData) {
//        log.info("processTemperatureReading");
//        if(temperatureLogData.getValue().equals(10.5)){
//            throw new RuntimeException("Test error");
//        }
        sensorMonitoringRepository
                .findById(new SensorId(temperatureLogData.getSensorId()))
                .ifPresentOrElse(
                        sensor -> handlerSensorMonitoring(temperatureLogData, sensor),
                        () -> logIgnoredTemperature(temperatureLogData)
                );
    }

    private void handlerSensorMonitoring(TemperatureLogData temperatureLogData, SensorMonitoring sensor) {
        if (sensor.isEnabled()) {
            sensor.setLastTemperature(temperatureLogData.getValue());
            sensor.setUpdateAt(OffsetDateTime.now());
            sensorMonitoringRepository.save(sensor);


            TemperatureLog temperatureLog = TemperatureLog.builder()
                    .id(new TemperatureLogId(temperatureLogData.getId()))
                    .registeredAt(temperatureLogData.getRegisteredAt())
                    .value(temperatureLogData.getValue())
                    .sensorId(new SensorId(temperatureLogData.getSensorId()))
                    .build();

            temperatureLogRepository.save(temperatureLog);
            log.info("Temperature Update: SensorId {} Temp {}", temperatureLog.getSensorId(),temperatureLog.getValue());
        } else {
            logIgnoredTemperature(temperatureLogData);
        }
    }

    private void logIgnoredTemperature(TemperatureLogData temperatureLogData) {
        log.info("Temperature Ignored: SensorId {} Temp {}", temperatureLogData.getSensorId(), temperatureLogData.getValue());
    }
}
