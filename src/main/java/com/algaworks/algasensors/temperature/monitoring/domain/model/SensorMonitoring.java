package com.algaworks.algasensors.temperature.monitoring.domain.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

import static java.lang.Boolean.TRUE;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SensorMonitoring {
    @Id
    @AttributeOverride(name = "value", column = @Column(name = "id", columnDefinition = "BIGINT"))
    private SensorId id;
    private Double lastTemperature;
    private OffsetDateTime updateAt;
    private Boolean enabled;

    public boolean isEnabled() {
        return TRUE.equals(getEnabled());
    }
}
