package com.algaworks.algasensors.temperature.monitoring.api.controller;

import com.algaworks.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorId;
import com.algaworks.algasensors.temperature.monitoring.domain.model.TemperatureLog;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.TemperatureLogRepository;
import io.hypersistence.tsid.TSID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/sensors/{sensorId}/temperatures")
public class TemperatureLogController {

    private final TemperatureLogRepository temperatureLogRepository;

    @GetMapping
    public Page<TemperatureLogData> search(@PathVariable TSID sensorId, @PageableDefault Pageable pageable) {
        Page<TemperatureLog> temperatureLogs = temperatureLogRepository.findBySensorId(new SensorId(sensorId), pageable);
        return temperatureLogs.map(temperatureLog ->
            TemperatureLogData.builder()
                    .id(temperatureLog.getId().getValue())
                    .value(temperatureLog.getValue())
                    .registeredAt(temperatureLog.getRegisteredAt())
                    .build()
        );
    }

}
